package com.jwoglom.pumpx2.pump;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyStore;

import timber.log.Timber;

/**
 * Encrypted-at-rest preferences backed by the Android Keystore, holding only the pump secrets:
 * the pairing code and the JPAKE derived secret / server nonce. Keys are encrypted with
 * AES256-SIV and values with AES256-GCM.
 *
 * Non-secret pump state (Bluetooth MAC, API version, counters) stays in the plaintext
 * {@code PumpState} preferences -- see {@link PumpState}. Routing everything through here would
 * put an Android Keystore round-trip on every pump state read and write, much of it on the BLE
 * callback path.
 */
public final class PumpSecrets {
    private static final String SECRETS_PREF_NAME = "PumpStateSecrets";
    private static final String ANDROID_KEYSTORE = "AndroidKeyStore";
    private static SharedPreferences cachedPrefs;

    private PumpSecrets() {}

    public static synchronized SharedPreferences prefs(Context context) {
        if (cachedPrefs == null) {
            Context appContext = context.getApplicationContext();
            try {
                cachedPrefs = create(appContext);
            } catch (GeneralSecurityException | IOException e) {
                // The keystore master key can become permanently unusable through no fault of
                // the app: a device restore onto new hardware, a lock-screen credential reset,
                // or an OEM upgrade that drops keystore entries. The stored ciphertext is then
                // unrecoverable, so the only way forward is to discard it and start clean.
                // Rethrowing instead would leave PumpState permanently unusable, with every
                // later call failing and the app unable to reach the pump at all.
                Timber.w(e, "Encrypted preferences unreadable; discarding and recreating. "
                        + "Stored pump secrets are lost and the pump must be re-paired.");
                reset(appContext);
                try {
                    cachedPrefs = create(appContext);
                } catch (GeneralSecurityException | IOException retryFailure) {
                    throw new IllegalStateException(
                            "Unable to open encrypted preferences after resetting them", retryFailure);
                }
            }
        }
        return cachedPrefs;
    }

    private static SharedPreferences create(Context appContext)
            throws GeneralSecurityException, IOException {
        MasterKey masterKey = new MasterKey.Builder(appContext)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build();
        return EncryptedSharedPreferences.create(
                appContext,
                SECRETS_PREF_NAME,
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM);
    }

    /**
     * Discards the encrypted store and the master key protecting it. Both have to go: the
     * ciphertext cannot be decrypted without the original key, and an invalidated key keeps
     * failing until its alias is removed.
     */
    private static void reset(Context appContext) {
        try {
            KeyStore keyStore = KeyStore.getInstance(ANDROID_KEYSTORE);
            keyStore.load(null);
            keyStore.deleteEntry(MasterKey.DEFAULT_MASTER_KEY_ALIAS);
        } catch (GeneralSecurityException | IOException e) {
            Timber.w(e, "Could not delete the encrypted preferences master key");
        }
        appContext.deleteSharedPreferences(SECRETS_PREF_NAME);
    }

    /**
     * Drops the cached handle so the next {@link #prefs} call rebuilds it. Visible for tests.
     */
    static synchronized void clearCache() {
        cachedPrefs = null;
    }
}
