package com.jwoglom.pumpx2.pump.messages.response.currentStatus;

import com.google.common.base.Preconditions;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.request.currentStatus.ExtendedBolusStatusRequest;

import java.math.BigInteger;

@MessageProps(
    opCode=47,
    size=18,
    type=MessageType.RESPONSE,
    request=ExtendedBolusStatusRequest.class
)
public class ExtendedBolusStatusResponse extends Message {
    
    private int bolusStatus;
    private int bolusId;
    private long timestamp;
    private long requestedVolume;
    private long duration;
    private int bolusSource;
    
    public ExtendedBolusStatusResponse() {}
    
    public ExtendedBolusStatusResponse(int bolusStatus, int bolusId, long timestamp, long requestedVolume, long duration, int bolusSource) {
        this.cargo = buildCargo(bolusStatus, bolusId, timestamp, requestedVolume, duration, bolusSource);
        this.bolusStatus = bolusStatus;
        this.bolusId = bolusId;
        this.timestamp = timestamp;
        this.requestedVolume = requestedVolume;
        this.duration = duration;
        this.bolusSource = bolusSource;
        
    }

    public void parse(byte[] raw) {
        Preconditions.checkArgument(raw.length == props().size());
        this.cargo = raw;
        this.bolusStatus = raw[0];
        this.bolusId = Bytes.readShort(raw, 1);
        this.timestamp = Bytes.readUint32(raw, 5);
        this.requestedVolume = Bytes.readUint32(raw, 9);
        this.duration = Bytes.readUint32(raw, 13);
        this.bolusSource = raw[17];
        
    }

    
    public static byte[] buildCargo(int bolusStatus, int bolusId, long timestamp, long requestedVolume, long duration, int bolusSource) {
        return Bytes.combine(
            new byte[]{ (byte) bolusStatus }, 
            Bytes.firstTwoBytesLittleEndian(bolusId), 
            Bytes.toUint32(timestamp), 
            Bytes.toUint32(requestedVolume), 
            Bytes.toUint32(duration), 
            new byte[]{ (byte) bolusSource });
    }
    
    public int getBolusStatus() {
        return bolusStatus;
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
    public long getDuration() {
        return duration;
    }
    public int getBolusSource() {
        return bolusSource;
    }
    
}