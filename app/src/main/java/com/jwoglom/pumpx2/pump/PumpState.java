package com.jwoglom.pumpx2.pump;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Pair;

import com.jwoglom.pumpx2.pump.messages.Message;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.SynchronousQueue;

import timber.log.Timber;

public class PumpState {

    private static SharedPreferences prefs(Context context) {
        return context.getSharedPreferences("PumpState", Context.MODE_PRIVATE);
    }

    // The pairing code is also called the authentication key
    private static final String PAIRING_CODE_PREF = "pairingCode";
    public static void setPairingCode(Context context, String pairingCode) {
        prefs(context).edit().putString(PAIRING_CODE_PREF, pairingCode).apply();
    }

    public static String getPairingCode(Context context) {
        return prefs(context).getString(PAIRING_CODE_PREF, null);
    }

    public static long timeSinceReset = 0;

    // This is filled on app start by the result of getPairingCode()
    // and is used within bluetooth internals which don't have a
    // Context passed to them
    public static String authenticationKey = "";
    public static String getAuthenticationKey() {
        return authenticationKey;
    }

    public static int failedPumpConnectionAttempts = 0;


    // The most recent Bluetooth MAC of the connected pump
    private static final String SAVED_BLUETOOTH_MAC_PREF = "savedBluetoothMAC";
    public static void setSavedBluetoothMAC(Context context, String bluetoothMAC) {
        prefs(context).edit().putString(SAVED_BLUETOOTH_MAC_PREF, bluetoothMAC).apply();
    }

    public static String getSavedBluetoothMAC(Context context) {
        return prefs(context).getString(SAVED_BLUETOOTH_MAC_PREF, null);
    }

    // The state of recent messages sent to the pump paired with the transaction id.
    private static Queue<Pair<Message, Byte>> requestMessages = new LinkedList<>();
    public static synchronized void pushRequestMessage(Message m, byte txId) {
        requestMessages.add(Pair.create(m, txId));
    }

    public static synchronized Pair<Message, Byte> popRequestMessage() {
        return requestMessages.poll();
    }
}
