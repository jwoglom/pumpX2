package com.jwoglom.pumpx2.pump.messages;

public class TransactionId {
    private int txId = 0;

    public void set(int i) {
        if (i < 0 || i > 255) {
            i = 0;
        }
        txId = i;
    }

    public void increment() {
        set(txId + 1);
    }

    public byte get() {
        return (byte) txId;
    }

    public String toString() {
        return "TransactionId(txId="+txId+")";
    }
}
