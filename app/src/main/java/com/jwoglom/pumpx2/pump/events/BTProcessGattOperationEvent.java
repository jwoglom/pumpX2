package com.jwoglom.pumpx2.pump.events;

import org.apache.commons.codec.binary.Hex;

import java.util.UUID;

public class BTProcessGattOperationEvent {
    private final UUID uuid;
    private final byte[] data;
    private final boolean multiplePackets;

    public BTProcessGattOperationEvent(UUID uuid, byte[] data, boolean multiplePackets) {
        this.uuid = uuid;
        this.data = data;
        this.multiplePackets = multiplePackets;
    }

    public UUID uuid() {
        return uuid;
    }

    public byte[] data() {
        return data;
    }

    public boolean multiplePackets() {
        return multiplePackets;
    }

    public String toString() {
        return "BTProcessGattOperationEvent(uuid="+uuid.toString()+", data="+ Hex.encodeHexString(data)+", multiplePackets="+multiplePackets+")";
    }
}
