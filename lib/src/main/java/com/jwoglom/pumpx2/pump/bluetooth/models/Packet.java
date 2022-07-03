package com.jwoglom.pumpx2.pump.bluetooth.models;

import kotlin.collections.ArraysKt;

public class Packet {
    private final byte packetsRemaining;
    private final byte transactionId;
    private final byte[] internalCargo;

    public Packet(byte packetsRemaining, byte transactionId, byte[] internalCargo) {
        this.packetsRemaining = packetsRemaining;
        this.transactionId = transactionId;
        this.internalCargo = internalCargo;
    }

    public byte[] build() {
        byte[] ret = new byte[internalCargo.length + 2];
        ret[0] = (byte)packetsRemaining;
        ret[1] = transactionId;
        System.arraycopy(internalCargo, 0, ret, 2, internalCargo.length);
        return ret;
    }

    public byte transactionId() {
        return transactionId;
    }

    public byte[] internalCargo() {
        return internalCargo;
    }

    public Packet merge(Packet newPacket) {
        return new Packet(newPacket.packetsRemaining, transactionId, ArraysKt.plus(this.internalCargo, newPacket.internalCargo));
    }

}
