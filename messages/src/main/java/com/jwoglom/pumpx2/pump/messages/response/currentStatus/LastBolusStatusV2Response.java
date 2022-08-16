package com.jwoglom.pumpx2.pump.messages.response.currentStatus;

import com.google.common.base.Preconditions;
import com.jwoglom.pumpx2.pump.messages.annotations.ApiVersionDependent;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.request.currentStatus.LastBolusStatusV2Request;
import com.jwoglom.pumpx2.pump.messages.response.historyLog.BolusDeliveryHistoryLog;

import java.util.Set;

@MessageProps(
    opCode=-91,
    size=24,
    type=MessageType.RESPONSE,
    request=LastBolusStatusV2Request.class
)
@ApiVersionDependent
public class LastBolusStatusV2Response extends Message {
    
    private int bit0;
    private int bolusId;
    private long timestamp;
    private long deliveredVolume;
    private int bolusStatusId;
    private int bolusSourceId;
    private int bolusTypeBitmask;
    private long extendedBolusDuration;
    private long requestedVolume;
    
    public LastBolusStatusV2Response() {}
    
    public LastBolusStatusV2Response(int bit0, int bolusId, long timestamp, long deliveredVolume, int bolusStatusId, int bolusSourceId, int bolusTypeBitmask, long extendedBolusDuration, long requestedVolume) {
        this.cargo = buildCargo(bit0, bolusId, timestamp, deliveredVolume, bolusStatusId, bolusSourceId, bolusTypeBitmask, extendedBolusDuration, requestedVolume);
        this.bit0 = bit0;
        this.bolusId = bolusId;
        this.timestamp = timestamp;
        this.deliveredVolume = deliveredVolume;
        this.bolusStatusId = bolusStatusId;
        this.bolusSourceId = bolusSourceId;
        this.bolusTypeBitmask = bolusTypeBitmask;
        this.extendedBolusDuration = extendedBolusDuration;
        this.requestedVolume = requestedVolume;
        
    }

    public void parse(byte[] raw) {
        Preconditions.checkArgument(raw.length == props().size());
        this.cargo = raw;
        this.bit0 = raw[0];
        this.bolusId = Bytes.readShort(raw, 1);
        this.timestamp = Bytes.readUint32(raw, 5);
        this.deliveredVolume = Bytes.readUint32(raw, 9);
        this.bolusStatusId = raw[13];
        this.bolusSourceId = raw[14];
        this.bolusTypeBitmask = raw[15];
        this.extendedBolusDuration = Bytes.readUint32(raw, 16);
        this.requestedVolume = Bytes.readUint32(raw, 20);
        
    }


    public static byte[] buildCargo(int bit0, int bolusId, long timestamp, long deliveredVolume, int bit13, int bit14, int bit15, long extendedBolusDuration, long requestedVolume) {
        return Bytes.combine(
            new byte[]{ (byte) bit0 }, 
            Bytes.firstTwoBytesLittleEndian(bolusId),
            new byte[]{ 0, 0 },
            Bytes.toUint32(timestamp),
            Bytes.toUint32(deliveredVolume),
            new byte[]{ (byte) bit13 }, 
            new byte[]{ (byte) bit14 }, 
            new byte[]{ (byte) bit15 }, 
            Bytes.toUint32(extendedBolusDuration),
            Bytes.toUint32(requestedVolume));
    }
    
    public int getBit0() {
        return bit0;
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
    public enum BolusStatus {
        // TODO: this is guesswork and is incomplete
        STOPPED(0),
        COMPLETE(3),
        ;

        private final int id;
        BolusStatus(int id) {
            this.id = id;
        }

        public int id() {
            return id;
        }

        public static BolusStatus fromId(int id) {
            for (BolusStatus s : values()) {
                if (s.id() == id) {
                    return s;
                }
            }
            return null;
        }
    }

    public BolusStatus getBolusStatus() {
        return BolusStatus.fromId(bolusStatusId);
    }
    public int getBolusSourceId() {
        return bolusSourceId;
    }
    public BolusDeliveryHistoryLog.BolusSource getBolusSource() {
        return BolusDeliveryHistoryLog.BolusSource.fromId(bolusSourceId);
    }
    public int getBolusTypeBitmask() {
        return bolusTypeBitmask;
    }
    public Set<BolusDeliveryHistoryLog.BolusType> getBolusType() {
        return BolusDeliveryHistoryLog.BolusType.fromBitmask(bolusTypeBitmask);
    }
    public long getExtendedBolusDuration() {
        return extendedBolusDuration;
    }
    public long getRequestedVolume() {
        return requestedVolume;
    }
    
}