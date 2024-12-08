package com.jwoglom.pumpx2.pump.bluetooth;

import android.bluetooth.le.ScanResult;
import android.content.Context;

import com.google.common.base.Strings;
import com.jwoglom.pumpx2.pump.PumpState;
import com.jwoglom.pumpx2.pump.TandemError;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.Packetize;
import com.jwoglom.pumpx2.pump.messages.bluetooth.CharacteristicUUID;
import com.jwoglom.pumpx2.pump.messages.bluetooth.ServiceUUID;
import com.jwoglom.pumpx2.pump.messages.bluetooth.TronMessageWrapper;
import com.jwoglom.pumpx2.pump.messages.bluetooth.models.Packet;
import com.jwoglom.pumpx2.pump.messages.builders.CentralChallengeRequestBuilder;
import com.jwoglom.pumpx2.pump.messages.builders.JpakeAuthBuilder;
import com.jwoglom.pumpx2.pump.messages.builders.PumpChallengeRequestBuilder;
import com.jwoglom.pumpx2.pump.messages.models.PairingCodeType;
import com.jwoglom.pumpx2.pump.messages.request.authentication.AbstractCentralChallengeRequest;
import com.jwoglom.pumpx2.pump.messages.request.currentStatus.ApiVersionRequest;
import com.jwoglom.pumpx2.pump.messages.request.currentStatus.TimeSinceResetRequest;
import com.jwoglom.pumpx2.pump.messages.response.authentication.AbstractCentralChallengeResponse;
import com.jwoglom.pumpx2.pump.messages.response.authentication.AbstractPumpChallengeResponse;
import com.jwoglom.pumpx2.pump.messages.response.authentication.PumpChallengeResponse;
import com.jwoglom.pumpx2.pump.messages.response.qualifyingEvent.QualifyingEvent;
import com.welie.blessed.BluetoothPeripheral;
import com.welie.blessed.HciStatus;
import com.welie.blessed.WriteType;

import com.jwoglom.pumpx2.shared.Hex;

import org.apache.commons.codec.DecoderException;

import java.util.ArrayList;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import javax.annotation.Nullable;

import timber.log.Timber;

/**
 * An abstract class containing callback methods which are invoked when various Bluetooth events
 * occur related to the pump, as well as methods to manipulate the pump and receive responses.
 * For more under-the-hood details into the Bluetooth connection, see {@link TandemBluetoothHandler}
 */
public abstract class TandemPump {
    public final Context context;

    final Optional<String> filterToBluetoothMac;
    /** for KnownApiVersion.API_V3_2 and above, use instanceId=1 and not 0 */
    private int appInstanceId = 1;

    public TandemPump(Context context, TandemConfig config) {
        this.context = context;

        this.filterToBluetoothMac = config.getFilterToBluetoothMac();
        if (config.getPairingCodeType().isPresent()) {
            PumpState.pairingCodeType = config.getPairingCodeType().get();
        }

        PumpState.savedAuthenticationKey = PumpState.getPairingCode(context);
    }

    public TandemPump(Context context, Optional<String> filterToBluetoothMac) {
        this(context, new TandemConfig().withFilterToBluetoothMac(filterToBluetoothMac.orElse(null)));
    }

    public TandemPump(Context context) {
        this(context, new TandemConfig());
    }

    /**
     * Callback invoked when the initial Bluetooth connection with the pump is established,
     * but before the pump authentication process begins. The default behavior is to trigger
     * a CentralChallenge which starts the authentication process.
     * Note that this callback may be skipped when PumpX2 detects that the t:connect app is also
     * in use on the device and as a result the pump is already authenticated.
     * TODO: we need to still have the pairing key in order to send signed messages in this flow,
     * which needs to be captured in the API semantics.
     * @param peripheral the BluetoothPeripheral representing the connected pump
     */
    public void onInitialPumpConnection(BluetoothPeripheral peripheral) {
        Timber.i("TandemPump: onInitialPumpConnection (" + appInstanceId + ")");
        if (!PumpState.relyOnConnectionSharingForAuthentication) {
            AbstractCentralChallengeRequest request = CentralChallengeRequestBuilder.create(appInstanceId);
            if (request != null) {
                sendCommand(peripheral, request);
            } else {
                onWaitingForPairingCode(peripheral, null);
            }
        }
    }

    /**
     * Sends a request message to the pump. Most requests have no arguments and can be initialized
     * with their no-arg constructor. The pump will send a response message to every request,
     * which you can retrieve the result of asynchronously via the onReceiveMessage() callback.
     * @param peripheral the BluetoothPeripheral representing the pump
     * @param message a request message from {@link com.jwoglom.pumpx2.pump.messages.request}
     */
    public synchronized void sendCommand(BluetoothPeripheral peripheral, Message message) {
        if (PumpState.onlySnoopBluetooth) {
            Timber.d("TandemPump: onlySnoopBluetooth blocked SendCommand(" + message + ")");
            return;
        }

        Timber.i("TandemPump: sendCommand(" + message + ")");
        ArrayList<byte[]> bytes = new ArrayList<>();
        byte currentTxId = Packetize.txId.get();
        PumpState.pushRequestMessage(message, currentTxId);
        TronMessageWrapper wrapper = new TronMessageWrapper(message, currentTxId);
        Packetize.txId.increment();

        for (Packet packet : wrapper.packets()) {
            bytes.add(packet.build());
        }

        for (byte[] b : bytes) {
            UUID uuid = CharacteristicUUID.determine(message);
            Timber.d("TandemPump: raw sendCommand to characteristic %s: %s", CharacteristicUUID.which(uuid), Hex.encodeHexString(b));
            peripheral.writeCharacteristic(ServiceUUID.PUMP_SERVICE_UUID,
                    uuid,
                    b,
                    WriteType.WITH_RESPONSE);
        }
    }

    /**
     * Callback invoked to notify on an invalid pump pairing code
     * @param peripheral the BluetoothPeripheral representing the pump
     * @param resp the PumpChallengeResponse which was just received from the pump. null if the
     *             PumpX2 library didn't send the request because the pairing code didn't match the
     *             expected format of 16 alphanumeric characters
     */
    public void onInvalidPairingCode(BluetoothPeripheral peripheral, @Nullable AbstractPumpChallengeResponse resp) {
        Timber.i("TandemPump: onInvalidPairingCode: %s", resp);
    }

    /**
     * Callback invoked when we receive the response to a command sent to the pump.
     * Note that authentication responses are not returned here, they are handled inside of
     * {@link TandemBluetoothHandler} and considered an implementation detail of the PumpX2 library.
     * @param peripheral the BluetoothPeripheral representing the pump
     * @param message the parsed message which extends the Message class. To identify what type of
     *                message was found, check with `if (message instanceof ApiVersionResponse) ..`
     */
    public abstract void onReceiveMessage(BluetoothPeripheral peripheral, Message message);

    /**
     * Callback invoked when we receive a qualifying event from the pump. When an event is received,
     * a bitmask of different parameters is received over the Bluetooth connection.
     * @param peripheral the BluetoothPeripheral representing the pump
     * @param events a set of QualifyingEvent enums representing the event(s) which occurred. Note
     *               that some of these enum values represent metadata about the event, rather than
     *               specifying that distinct events occurred.
     */
    public abstract void onReceiveQualifyingEvent(BluetoothPeripheral peripheral, Set<QualifyingEvent> events);

    /**
     * Callback invoked when a pump which we were connected to over Bluetooth is disconnected.
     * @param peripheral the BluetoothPeripheral representing the pump
     * @param status the Bluetooth reason for disconnection
     * @return true if we should attempt to re-connect to the device, false otherwise
     */
    public boolean onPumpDisconnected(BluetoothPeripheral peripheral, HciStatus status) {
        Timber.i("TandemPump: onPumpDisconnected with status '%s'", status);
        return true;
    }

    /**
     * Callback invoked when a pump is found during the Bluetooth search, or when a Tandem pump
     * is already bonded, to determine whether the detected pump should be connected to.
     * @param peripheral the BluetoothPeripheral representing the pump which was found
     * @param scanResult the result of the Bluetooth search, including e.g. connection strength, if
     *                   the pump was discovered as a result of a Bluetooth search; if the pump is
     *                   already bonded to the device, then is null.
     * @return true if we should connect to the detected pump, false otherwise
     */
    public boolean onPumpDiscovered(BluetoothPeripheral peripheral, @Nullable ScanResult scanResult) {
        Timber.i("TandemPump: onPumpDiscovered(peripheral=%s, scanResult=%s)", peripheral, scanResult);
        if (filterToBluetoothMac.isPresent()) {
            if (filterToBluetoothMac.get().equals(peripheral.getAddress())) {
                Timber.i("TandemPump: found matching MAC (%s)", peripheral.getAddress());
                return true;
            } else {
                Timber.i("TandemPump: Ignoring peripheral with mismatching MAC (found %s, expected %s)", peripheral.getAddress(), filterToBluetoothMac.get());
                return false;
            }
        }
        return true;
    }

    /**
     * Called after authentication is complete and the pump can be connected to.
     * The ApiVersionRequest and TimeSinceResetRequest commands are sent by default when the
     * pump is connected so that internal state can be filled; to avoid sending these messages,
     * override this function in your subclass of {@link TandemPump} and do not call super().
     * @param peripheral the BluetoothPeripheral representing the pump which was found
     */
    public void onPumpConnected(BluetoothPeripheral peripheral) {
        Timber.i("TandemPump: onPumpConnected");
        sendCommand(peripheral, new ApiVersionRequest());
        sendCommand(peripheral, new TimeSinceResetRequest());
    }

    /**
     * Called when the pump model number is returned from the Bluetooth DIS
     * @param peripheral the BluetoothPeripheral representing the pump which is connected
     * @param modelNumber the model number of the pump. Expected value is "X2" for the t:slim X2;
     *                    may be different in the future, e.g. for Mobi)
     */
    public void onPumpModel(BluetoothPeripheral peripheral, String modelNumber) {
        Timber.i("TandemPump: onPumpModel: %s", modelNumber);
    }

    /**
     * Callback invoked when the CentralChallengeResponse is received, and pair() can be invoked
     * with a pairing code displayed on the pump to instantiate the pump-to-device connection.
     * @param peripheral the BluetoothPeripheral representing the pump
     * @param centralChallenge the CentralChallengeResponse which should be passed to pair()
     */
    public abstract void onWaitingForPairingCode(BluetoothPeripheral peripheral, @Nullable AbstractCentralChallengeResponse centralChallenge);


    /**
     * Invoked by the user to pair to the connected pump, with the CentralChallengeResponse received
     * from the onWaitingForPairingCode callback.
     * @param peripheral the BluetoothPeripheral representing the pump
     * @param centralChallenge the CentralChallengeResponse received from onWaitingForPairingCode
     * @param pairingCode with {@link PairingCodeType#LONG_16CHAR}: the 16-character pairing code
     *                    displayed on the pump screen. Any whitespace or dashes will be removed.
     *                    with {@link PairingCodeType#SHORT_6CHAR}: the 6-character pairing code
     *                    displayed on the pump screen.
     */
    public void pair(BluetoothPeripheral peripheral, @Nullable AbstractCentralChallengeResponse centralChallenge, String pairingCode) {
        if (PumpState.pairingCodeType == PairingCodeType.LONG_16CHAR) {
            Timber.i("TandemPump: pair(LONG_16CHAR, " + pairingCode + ")");
            try {
                Message message = PumpChallengeRequestBuilder.create(centralChallenge, pairingCode);
                sendCommand(peripheral, message);
            } catch (PumpChallengeRequestBuilder.InvalidPairingCodeFormat e) {
                Timber.e(e);
                onInvalidPairingCode(peripheral, null);
            }
        } else if (PumpState.pairingCodeType == PairingCodeType.SHORT_6CHAR) {
            String jpakeSecretHex = PumpState.getJpakeDerivedSecret(context);
            if (Strings.isNullOrEmpty(jpakeSecretHex)) {
                Timber.i("TandemPump: pair(SHORT_6CHAR, " + pairingCode + ", BOOTSTRAP)");
                JpakeAuthBuilder.clearInstance();
                Message message = JpakeAuthBuilder.initializeWithPairingCode(pairingCode).nextRequest();
                sendCommand(peripheral, message);
            } else {
                Timber.i("TandemPump: pair(SHORT_6CHAR, " + pairingCode + ", CONFIRM)");
                JpakeAuthBuilder.clearInstance();
                try {
                    Message message = JpakeAuthBuilder.initializeWithDerivedSecret(pairingCode, Hex.decodeHex(jpakeSecretHex)).nextRequest();
                    sendCommand(peripheral, message);
                } catch (DecoderException e) {
                    Timber.e(e);
                    onInvalidPairingCode(peripheral, null);
                    PumpState.setJpakeDerivedSecret(context, "");
                }
            }
        } else {
            throw new IllegalArgumentException("no pairingCodeType");
        }
    }

    /**
     * Invoked when an error occurs within PumpX2.
     * @param peripheral the BluetoothPeripheral representing the pump
     * @param reason a value from the {@link TandemError} enum representing the error which occurred
     */
    public void onPumpCriticalError(BluetoothPeripheral peripheral, TandemError reason) {
        Timber.e("onPumpCriticalError: %s: %s", peripheral, reason);
    }

    /**
     * Enables actions which affect insulin delivery, e.g. remote bolus.
     */
    public final void enableActionsAffectingInsulinDelivery() {
        PumpState.enableActionsAffectingInsulinDelivery();
    }

    /**
     * Sets the appInstanceId for use in the initial request which is used as part of multi-device
     * connected setups. If requested, this should be called immediately after initialization.
     */
    public final void setAppInstanceId(int appInstanceId) {
        this.appInstanceId = appInstanceId;
    }

    /**
     * When enabled, PumpX2 will adapt its behavior to support connecting to a Tandem pump which
     * is also connected via the t:connect app on the same device by sharing the Bluetooth connection.
     * If requested, this should be called immediately after initialization.
     * If not enabled, when t:connect or another application on the device is detected to be sharing
     * the Bluetooth connection with us, onPumpCriticalError will be invoked with
     * {@link TandemError#SHARING_CONNECTION_WITH_TCONNECT_APP}
     */
    public final void enableTconnectAppConnectionSharing() {
        PumpState.tconnectAppConnectionSharing = true;
    }

    /**
     * When enabled, will send response messages for requests which were not initiated by this device
     * when t:connect app connection sharing is enabled. If requested, this should be called
     * immediately after initialization.
     */
    public final void enableSendSharedConnectionResponseMessages() {
        PumpState.sendSharedConnectionResponseMessages = true;
    }


    /**
     * When enabled, will rely on the t:connect app to perform authentication and will not send any
     * authentication messages. Assumes {@link #enableTconnectAppConnectionSharing()} is also true.
     */
    public final void relyOnConnectionSharingForAuthentication() {
        PumpState.relyOnConnectionSharingForAuthentication = true;
    }

    /**
     * When enabled, any requests attempted to be sent to the pump via PumpX2 will be BLOCKED, and
     * instead the library will just listen silently to all BT characteristic reads which occur.
     */
    public final void onlySnoopBluetoothAndBlockAllPumpX2Functionality() {
        PumpState.onlySnoopBluetooth = true;
    }
}
