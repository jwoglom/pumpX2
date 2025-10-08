package com.jwoglom.pumpx2.pump.messages;

import com.jwoglom.pumpx2.shared.L;

/**
 * Represents the NEXT ID which can be used for a transaction in communicating with the pump.
 * We read the current txId, send a message with it, and then increment it.
 */
public class TransactionId {
    private static final String TAG = "TransactionId";
    private int txId = 0;

    public void set(int i) {
        if (i < 0 || i > 255) {
            i = 0;
        }
        if (i != txId + 1) {
            L.d(TAG, "set: old " + txId + " new " + i);
        }
        txId = i;
    }

    public void increment() {
        set(txId + 1);
    }

    public void reset() {
        set(0);
    }

    public byte get() {
        return (byte) txId;
    }

    public String toString() {
        return "TransactionId(txId="+txId+")";
    }
}
