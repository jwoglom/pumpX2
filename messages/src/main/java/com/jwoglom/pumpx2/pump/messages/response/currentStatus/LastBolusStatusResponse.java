package com.jwoglom.pumpx2.pump.messages.response.currentStatus;

import com.google.common.base.Preconditions;
import com.jwoglom.pumpx2.pump.messages.bluetooth.Characteristic;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.request.currentStatus.LastBolusStatusRequest;

import java.math.BigInteger;

/**
 * Does not contain requestedVolume which {@link LastBolusStatusV2Response} has
 */
@MessageProps(
    opCode=49,
    size=20,
    type=MessageType.RESPONSE,
    characteristic=Characteristic.CURRENT_STATUS,
    request=LastBolusStatusRequest.class
)
public class LastBolusStatusResponse extends LastBolusStatusAbstractResponse {
    
    private int status;
    private int bolusId;
    private long timestamp;
    private long deliveredVolume;
    private int bolusStatusId;
    private int bolusSourceId;
    private int bolusTypeBitmask;
    private long extendedBolusDuration;
    private byte[] unknown;
    
    public LastBolusStatusResponse() {}
    
    public LastBolusStatusResponse(int status, int bolusId, long timestamp, long deliveredVolume, int bolusStatusId, int bolusSourceId, int bolusTypeBitmask, long extendedBolusDuration, byte[] unknown) {
        this.cargo = buildCargo(status, bolusId, timestamp, deliveredVolume, bolusStatusId, bolusSourceId, bolusTypeBitmask, extendedBolusDuration, unknown);
        this.status = status;
        this.bolusId = bolusId;
        this.timestamp = timestamp;
        this.deliveredVolume = deliveredVolume;
        this.bolusStatusId = bolusStatusId;
        this.bolusSourceId = bolusSourceId;
        this.bolusTypeBitmask = bolusTypeBitmask;
        this.extendedBolusDuration = extendedBolusDuration;
        this.unknown = unknown;
    }

    public void parse(byte[] raw) {
        Preconditions.checkArgument(raw.length == props().size());
        this.cargo = raw;
        this.status = raw[0];
        this.bolusId = Bytes.readShort(raw, 1);
        this.unknown = new byte[]{raw[3], raw[4]};
        this.timestamp = Bytes.readUint32(raw, 5);
        this.deliveredVolume = Bytes.readUint32(raw, 9);
        this.bolusStatusId = raw[13];
        this.bolusSourceId = raw[14];
        this.bolusTypeBitmask = raw[15];
        this.extendedBolusDuration = Bytes.readUint32(raw, 16);
    }

    
    public static byte[] buildCargo(int status, int bolusId, long timestamp, long deliveredVolume, int bolusStatusId, int bolusSourceId, int bolusTypeBitmask, long extendedBolusDuration, byte[] unknown) {
        return Bytes.combine(
            new byte[]{ (byte) status }, 
            Bytes.firstTwoBytesLittleEndian(bolusId),
            unknown,
            Bytes.toUint32(timestamp), 
            Bytes.toUint32(deliveredVolume), 
            new byte[]{ (byte) bolusStatusId }, 
            new byte[]{ (byte) bolusSourceId }, 
            new byte[]{ (byte) bolusTypeBitmask }, 
            Bytes.toUint32(extendedBolusDuration)
        );
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
    public long getDeliveredVolume() {
        return deliveredVolume;
    }
    public int getBolusStatusId() {
        return bolusStatusId;
    }
    public int getBolusSourceId() {
        return bolusSourceId;
    }
    public int getBolusTypeBitmask() {
        return bolusTypeBitmask;
    }
    public long getExtendedBolusDuration() {
        return extendedBolusDuration;
    }
    
}