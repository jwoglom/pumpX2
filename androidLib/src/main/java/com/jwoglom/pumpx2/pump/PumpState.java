package com.jwoglom.pumpx2.pump;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Pair;

import com.jwoglom.pumpx2.pump.bluetooth.TandemPump;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.PacketArrayList;
import com.jwoglom.pumpx2.pump.messages.bluetooth.Characteristic;
import com.jwoglom.pumpx2.pump.messages.bluetooth.CharacteristicUUID;
import com.jwoglom.pumpx2.pump.messages.bluetooth.PumpStateSupplier;
import com.jwoglom.pumpx2.pump.messages.models.ApiVersion;
import com.jwoglom.pumpx2.pump.messages.models.KnownApiVersion;
import com.jwoglom.pumpx2.pump.messages.models.PairingCodeType;

import org.apache.commons.lang3.Validate;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import timber.log.Timber;

public class PumpState {
    // TODO: Refactor this class to be less hacky.

    static {
        PumpStateSupplier.pumpPairingCode = PumpState::getPairingCodeCached;
        PumpStateSupplier.jpakeDerivedSecretHex = PumpState::getJpakeDerivedSecretCached;
        PumpStateSupplier.jpakeServerNonceHex = PumpState::getJpakeServerNonceCached;
        PumpStateSupplier.pumpTimeSinceReset = PumpState::getPumpTimeSinceReset;
        PumpStateSupplier.pumpApiVersion = PumpState::getPumpAPIVersion;
        PumpStateSupplier.actionsAffectingInsulinDeliveryEnabled = PumpState::actionsAffectingInsulinDeliveryEnabled;
    }

    public static void resetState(Context context) {
        setPairingCode(context, null);
        setJpakeDerivedSecret(context, null);
        setJpakeServerNonce(context, null);
        pumpTimeSinceReset = null;
        selfTimeSinceReset = null;
        failedPumpConnectionAttempts = 0;
        setSavedBluetoothMAC(context, null);
        setPumpAPIVersion(null);
        setPumpSerialNum(null);
        clearRequestMessages();
        clearSavedPacketArrayLists();
        processedResponseMessages = 0;
        processedResponseMessagesFromUs = 0;
        resetInitialConnectionNoReplyFailures();
        clearInitialConnectionHardAuthFailure();
    }

    public static String exportState(Context context) {
        try {
            JSONObject o = new JSONObject();
            o.put("pairingCode", getPairingCode(context));
            o.put("jpakeDerivedSecret", getJpakeDerivedSecret(context));
            o.put("jpakeServerNonce", getJpakeServerNonce(context));
            o.put("savedBluetoothMAC", getSavedBluetoothMAC(context));
            if (getPumpSerialNum() != null && !getPumpSerialNum().isBlank()) {
                o.put("pumpSerialNum", getPumpSerialNum());
            }
            o.put("pumpAPIVersion", getPumpAPIVersion().serialize());
            return o.toString();
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    public static void importState(Context context, String stateJson) {
        try {
            JSONObject o = new JSONObject(stateJson);
            setPairingCode(context, o.getString("pairingCode"));
            setJpakeDerivedSecret(context, o.getString("jpakeDerivedSecret"));
            setJpakeServerNonce(context, o.getString("jpakeServerNonce"));
            setSavedBluetoothMAC(context, o.getString("savedBluetoothMAC"));
            if (o.has("pumpSerialNum") && !o.getString("pumpSerialNum").isBlank()) {
                setPumpSerialNum(o.getString("pumpSerialNum"));
            }
            setPumpAPIVersion(ApiVersion.deserialize(o.getString("pumpAPIVersion")));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Plaintext preferences for non-secret pump state (Bluetooth MAC, and anything else added
     * here later). Secrets go to {@link #secretPrefs} instead.
     */
    private static SharedPreferences prefs(Context context) {
        return context.getApplicationContext().getSharedPreferences("PumpState", Context.MODE_PRIVATE);
    }

    /**
     * Encrypted-at-rest preferences, used only for the values below that are genuinely secret.
     * Keeping the rest on {@link #prefs} avoids an Android Keystore round-trip on every pump
     * state access, much of which happens on the BLE callback path.
     */
    private static SharedPreferences secretPrefs(Context context) {
        SharedPreferences encrypted = PumpSecrets.prefs(context);
        migrateLegacySecrets(context, encrypted);
        return encrypted;
    }

    // One-time migration of the secret values from the previous plaintext "PumpState"
    // preferences into the encrypted store, so existing pairing/JPAKE state survives an app
    // upgrade. Non-secret keys are left where they are -- they still live in prefs(context).
    private static boolean legacySecretsMigrated = false;
    private static synchronized void migrateLegacySecrets(Context context, SharedPreferences encrypted) {
        if (legacySecretsMigrated) {
            return;
        }

        // The keys moved out of the plaintext store and into PumpSecrets. Declared here rather
        // than as a static field because the *_PREF constants are declared further down.
        String[] secretPrefKeys = {
                PAIRING_CODE_PREF, JPAKE_DERIVED_SECRET_PREF, JPAKE_SERVER_NONCE_PREF
        };

        SharedPreferences legacy = context.getApplicationContext()
                .getSharedPreferences("PumpState", Context.MODE_PRIVATE);

        SharedPreferences.Editor encryptedEdit = encrypted.edit();
        SharedPreferences.Editor legacyEdit = legacy.edit();
        boolean foundAny = false;
        for (String key : secretPrefKeys) {
            String value = legacy.getString(key, null);
            if (value == null) {
                continue;
            }
            foundAny = true;
            encryptedEdit.putString(key, value);
            legacyEdit.remove(key);
        }

        // Only drop the plaintext copies once the encrypted write has actually landed, so an
        // interrupted migration leaves the secrets readable rather than losing them.
        if (!foundAny || (encryptedEdit.commit() && legacyEdit.commit())) {
            // Latch only on success: a failed migration should be retried on the next access
            // rather than silently leaving the secrets stranded in the plaintext store.
            legacySecretsMigrated = true;
        } else {
            Timber.w("Could not migrate pump secrets to encrypted preferences; will retry");
        }
    }

    // The pairing code is also called the authentication key
    private static final String PAIRING_CODE_PREF = "pairingCode";
    public static String savedPairingCode = null;
    public static void setPairingCode(Context context, String pairingCode) {
        secretPrefs(context).edit().putString(PAIRING_CODE_PREF, pairingCode).commit();
        savedPairingCode = pairingCode;
    }

    public static String getPairingCode(Context context) {
        savedPairingCode = secretPrefs(context).getString(PAIRING_CODE_PREF, null);
        return savedPairingCode;
    }

    public static String getPairingCodeCached() {
        return savedPairingCode;
    }

    private static final String JPAKE_DERIVED_SECRET_PREF = "jpakeDerivedSecret";
    public static String savedJpakeDerivedSecret = null;
    public static String getJpakeDerivedSecret(Context context) {
        savedJpakeDerivedSecret = secretPrefs(context).getString(JPAKE_DERIVED_SECRET_PREF, null);
        return savedJpakeDerivedSecret;
    }

    public static void setJpakeDerivedSecret(Context context, String hexDerivedSecret) {
        secretPrefs(context).edit().putString(JPAKE_DERIVED_SECRET_PREF, hexDerivedSecret).commit();
        savedJpakeDerivedSecret = hexDerivedSecret;
    }

    public static String getJpakeDerivedSecretCached() {
        return savedJpakeDerivedSecret;
    }


    private static final String JPAKE_SERVER_NONCE_PREF = "jpakeServerNonce";
    public static String savedJpakeServerNonce = null;
    public static String getJpakeServerNonce(Context context) {
        savedJpakeServerNonce = secretPrefs(context).getString(JPAKE_SERVER_NONCE_PREF, null);
        return savedJpakeServerNonce;
    }

    public static void setJpakeServerNonce(Context context, String hexDerivedSecret) {
        secretPrefs(context).edit().putString(JPAKE_SERVER_NONCE_PREF, hexDerivedSecret).commit();
        savedJpakeServerNonce = hexDerivedSecret;
    }

    public static String getJpakeServerNonceCached() {
        return savedJpakeServerNonce;
    }

    // This is used during packet generation for signed messages,
    // and is filled by calling TimeSinceResetRequest
    public static Long pumpTimeSinceReset = null;
    // This is the time at which pumpTimeSinceReset was fetched
    public static Long selfTimeSinceReset = null;

    public static Long getPumpTimeSinceReset() {
        return pumpTimeSinceReset;
    }

    public static void setPumpTimeSinceReset(long time) {
        pumpTimeSinceReset = time;
        selfTimeSinceReset = System.currentTimeMillis();
    }

    public static int failedPumpConnectionAttempts = 0;


    // The most recent Bluetooth MAC of the connected pump
    private static final String SAVED_BLUETOOTH_MAC_PREF = "savedBluetoothMAC";
    public static void setSavedBluetoothMAC(Context context, String bluetoothMAC) {
        prefs(context).edit().putString(SAVED_BLUETOOTH_MAC_PREF, bluetoothMAC).commit();
    }

    public static String getSavedBluetoothMAC(Context context) {
        return prefs(context).getString(SAVED_BLUETOOTH_MAC_PREF, null);
    }


    // The (major, minor) pump version returned from ApiVersionResponse
    private static ApiVersion pumpApiVersion;
    public static void setPumpAPIVersion(ApiVersion apiVersion) {
        pumpApiVersion = apiVersion;
    }

    public static ApiVersion getPumpAPIVersion() {
        if (pumpApiVersion == null) {
            if (PumpState.pairingCodeType == PairingCodeType.SHORT_6CHAR) {
                Timber.w("PumpState: Falling back on safe 6-character pairing code default for pumpApiVersion (API_V3_4)");
                return KnownApiVersion.API_V3_4.get();
            }
            Timber.w("PumpState: Falling back on safe 16-character pairing code default for pumpApiVersion because ApiVersionResponse hasn't been received yet");
            return KnownApiVersion.API_V2_1.get();
        }
        return pumpApiVersion;
    }

    private static String pumpSerialNum = null;
    public static void setPumpSerialNum(String pumpSerialNum) {
        PumpState.pumpSerialNum = pumpSerialNum;
    }
    public static String getPumpSerialNum() {
        return pumpSerialNum;
    }

    // The state of recent messages sent to the pump paired with the transaction id.
    private static final Map<Pair<Characteristic, Byte>, Pair<Boolean, Message>> requestMessages = new HashMap<>();
    public static void pushRequestMessage(Message m, byte txId) {
        synchronized (requestMessages) {
            Characteristic c = Characteristic.of(CharacteristicUUID.determine(m));
            Pair<Characteristic, Byte> key = Pair.create(c, txId);
            if (requestMessages.containsKey(key)) {
                if (processedResponseMessages >= 255) {
                    Timber.d("requestMessages contained %s when pushing request message due to presumed opcode looparound (processed %d so far): %s", key, processedResponseMessages, m);
                } else {
                    Timber.w("requestMessages should not contain %s when pushing request message unless due to opcode looparound (processed %d so far): %s", key, processedResponseMessages, m);
                }
            }
            requestMessages.put(key, Pair.create(false, m));
        }
    }

    public static Optional<Message> readRequestMessage(Characteristic c, byte txId) {
        synchronized (requestMessages) {
            Pair<Boolean, Message> pair = requestMessages.get(Pair.create(c, txId));
            if (pair == null) {
                return Optional.empty();
            }
            return Optional.of(pair.second);
        }
    }

    public static void finishRequestMessage(Characteristic c, byte txId) {
        synchronized (requestMessages) {
            Pair<Characteristic, Byte> key = Pair.create(c, txId);
            Pair<Boolean, Message> pair = requestMessages.get(key);
            Validate.notNull(pair, "could not find requestMessage for txId " + txId + " and char " + c);
            if (pair.first) {
                Timber.w("txId " + txId + " was already processed for char " + c + ": pair=" + pair + " requestMessages=" + requestMessages);
            }
            requestMessages.put(key, Pair.create(true, pair.second));
        }
    }

    public static void clearRequestMessages() {
        synchronized (requestMessages) {
            Timber.d("requestMessages clear: %s", requestMessages);
            requestMessages.clear();
            processedResponseMessages = 0;
            processedResponseMessagesFromUs = 0;
        }
    }

    public static boolean hasPendingAuthorizationRequest() {
        synchronized (requestMessages) {
            return requestMessages.entrySet().stream()
                    .anyMatch(e -> e.getKey().first == Characteristic.AUTHORIZATION && !e.getValue().first);
        }
    }

    private static final Map<Pair<Characteristic, Byte>, PacketArrayList> savedPacketArrayList = new HashMap<>();
    public static synchronized void savePacketArrayList(Characteristic c, byte txId, PacketArrayList l) {
        Pair<Characteristic, Byte> key = Pair.create(c, txId);
        savedPacketArrayList.put(key, l);
    }

    public static synchronized Optional<PacketArrayList> checkForSavedPacketArrayList(Characteristic c, byte txId) {
        return Optional.ofNullable(savedPacketArrayList.get(Pair.create(c, txId)));
    }

    public static synchronized void removeSavedPacketArrayList(Characteristic c, byte txId) {
        savedPacketArrayList.remove(Pair.create(c, txId));
    }

    public static synchronized void clearSavedPacketArrayLists() {
        savedPacketArrayList.clear();
    }

    private static boolean actionsAffectingInsulinDeliveryEnabled = false;
    public static boolean actionsAffectingInsulinDeliveryEnabled() {
        return actionsAffectingInsulinDeliveryEnabled;
    }

    public static void enableActionsAffectingInsulinDelivery() {
        Timber.i("PumpState.enableActionsAffectingInsulinDelivery()");
        actionsAffectingInsulinDeliveryEnabled = true;
    }


    /**
     * Set via {@link TandemPump#enableTconnectAppConnectionSharing()}
     */
    public static boolean tconnectAppConnectionSharing = false;

    /**
     * Set via {@link TandemPump#enableSendSharedConnectionResponseMessages()}
     */
    public static boolean sendSharedConnectionResponseMessages = false;

    /**
     * Set via {@link TandemPump#relyOnConnectionSharingForAuthentication()}
     */
    public static boolean relyOnConnectionSharingForAuthentication = false;

    /**
     * When set internally by PumpX2, TandemPump will not send authentication messages on start.
     * This is set to true when the initial authentication step fails, which means the pump is
     * already authenticated with the running t:connect app.
     */
    public static boolean tconnectAppAlreadyAuthenticated = false;

    /**
     * Set internally by PumpX2 when tconnectAppAlreadyAuthenticated is enabled due to a failed
     * authentication message, and causes an error on writing the first pump message from PumpX2 to
     * be ignored, so that we can synchronize the initial opcode with the t:connect app.
     */
    public static boolean tconnectAppConnectionSharingIgnoreInitialFailingWrite = false;

    /**
     * When enabled, any requests attempted to be sent to the pump via PumpX2 will be BLOCKED, and
     * instead the library will just listen silently to all BT characteristic reads which occur.
     */
    public static boolean onlySnoopBluetooth = false;

    /**
     * Incremented each time during this connection to the pump that a response message has been
     * received. Checked to avoid race conditions with t:connect app connection sharing.
     */
    public static int processedResponseMessages = 0;
    public static int processedResponseMessagesFromUs = 0;

    /**
     * Count of consecutive initial-connection windows where requests were sent but no replies were
     * received. Used to avoid aggressively unbonding after a single transient timeout.
     */
    public static int initialConnectionNoReplyFailures = 0;

    public static void resetInitialConnectionNoReplyFailures() {
        initialConnectionNoReplyFailures = 0;
    }

    public static int incrementInitialConnectionNoReplyFailures() {
        return ++initialConnectionNoReplyFailures;
    }

    /**
     * Tracks whether the current connection encountered a hard authentication failure where the
     * existing bond/pairing is likely invalid.
     */
    public static boolean initialConnectionHardAuthFailure = false;

    public static void markInitialConnectionHardAuthFailure() {
        initialConnectionHardAuthFailure = true;
    }

    public static void clearInitialConnectionHardAuthFailure() {
        initialConnectionHardAuthFailure = false;
    }

    /**
     * Used in the TandemPump constructor to set the pairing code type.
     */
    public static PairingCodeType pairingCodeType = PairingCodeType.LONG_16CHAR;

}
