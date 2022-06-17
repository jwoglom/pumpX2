package com.jwoglom.pumpx2.pump.messages;

public class StreamPacketArrayList extends PacketArrayList {
    public StreamPacketArrayList(byte expectedopCode, byte expectedCargoSize, byte expectedTxId, boolean isSigned) {
        super(expectedopCode, expectedCargoSize, expectedTxId, isSigned);
    }
}
