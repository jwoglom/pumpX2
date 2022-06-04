package com.jwoglom.pumpx2.pump.messages.response.currentStatus;

import com.google.common.base.Preconditions;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.request.currentStatus.CurrentBolusStatusRequest;

@MessageProps(
    opCode=45,
    size=15,
    type=MessageType.RESPONSE,
    request=CurrentBolusStatusRequest.class
)
public class CurrentBolusStatusResponse extends Message {
    
    private int status;
    private int bolusId;
    private long timestamp;
    private long requestedVolume;
    private int bolusSource;
    private int bolusTypeBitmask;
    
    public CurrentBolusStatusResponse() {}
    
    public CurrentBolusStatusResponse(int status, int bolusId, long timestamp, long requestedVolume, int bolusSource, int bolusTypeBitmask) {
        this.cargo = buildCargo(status, bolusId, timestamp, requestedVolume, bolusSource, bolusTypeBitmask);
        this.status = status;
        this.bolusId = bolusId;
        this.timestamp = timestamp;
        this.requestedVolume = requestedVolume;
        this.bolusSource = bolusSource;
        this.bolusTypeBitmask = bolusTypeBitmask;
        
    }

    public void parse(byte[] raw) {
        Preconditions.checkArgument(raw.length == props().size());
        this.cargo = raw;
        this.status = raw[0];
        this.bolusId = Bytes.readShort(raw, 1);
        this.timestamp = Bytes.readUint32(raw, 5);
        this.requestedVolume = Bytes.readUint32(raw, 9);
        this.bolusSource = raw[13];
        this.bolusTypeBitmask = raw[14];
        
    }

    
    public static byte[] buildCargo(int status, int bolusId, long timestamp, long requestedVolume, int bolusSource, int bolusTypeBitmask) {
        return Bytes.combine(
            new byte[]{ (byte) status }, 
            Bytes.firstTwoBytesLittleEndian(bolusId),
            new byte[]{ 0, 0 },
            Bytes.toUint32(timestamp), 
            Bytes.toUint32(requestedVolume), 
            new byte[]{ (byte) bolusSource }, 
            new byte[]{ (byte) bolusTypeBitmask });
    }
    
    public int getStatus() {
        return status;
    }
    public int getBolusId() {
        return bolusId;
    }
    public long getTimestamp() {
        return timestamp;
    }
    public long getRequestedVolume() {
        return requestedVolume;
    }
    public int getBolusSource() {
        return bolusSource;
    }
    public int getBolusTypeBitmask() {
        return bolusTypeBitmask;
    }
    
}