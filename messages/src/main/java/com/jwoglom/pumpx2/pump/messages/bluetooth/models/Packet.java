package com.jwoglom.pumpx2.pump.messages.bluetooth.models;

import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;

/**
 * Packet represents the raw byte string included within a single Bluetooth response packet
 *
 * A combination of Packet's sent over a Bluetooth characteristic by one end of the BT communication
 * represents one {@link com.jwoglom.pumpx2.pump.messages.Message}.
 */
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
        return new Packet(newPacket.packetsRemaining, transactionId, Bytes.combine(this.internalCargo, newPacket.internalCargo));
    }

}
