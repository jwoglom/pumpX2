package com.jwoglom.pumpx2.pump.messages.response.currentStatus;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.helpers.Dates;
import com.jwoglom.pumpx2.pump.messages.request.currentStatus.CurrentBolusStatusRequest;
import com.jwoglom.pumpx2.pump.messages.response.historyLog.BolusDeliveryHistoryLog;

import java.time.Instant;
import java.util.Set;

@MessageProps(
    opCode=45,
    size=15,
    type=MessageType.RESPONSE,
    request=CurrentBolusStatusRequest.class
)
public class CurrentBolusStatusResponse extends Message {
    
    private int statusId;
    private int bolusId;
    private long timestamp;
    private long requestedVolume;
    private int bolusSourceId;
    private int bolusTypeBitmask;
    
    public CurrentBolusStatusResponse() {}
    
    public CurrentBolusStatusResponse(int statusId, int bolusId, long timestamp, long requestedVolume, int bolusSourceId, int bolusTypeBitmask) {
        this.cargo = buildCargo(statusId, bolusId, timestamp, requestedVolume, bolusSourceId, bolusTypeBitmask);
        this.statusId = statusId;
        this.bolusId = bolusId;
        this.timestamp = timestamp;
        this.requestedVolume = requestedVolume;
        this.bolusSourceId = bolusSourceId;
        this.bolusTypeBitmask = bolusTypeBitmask;
        
    }

    public void parse(byte[] raw) {
        Validate.isTrue(raw.length == props().size());
        this.cargo = raw;
        this.statusId = raw[0];
        this.bolusId = Bytes.readShort(raw, 1);
        this.timestamp = Bytes.readUint32(raw, 5);
        this.requestedVolume = Bytes.readUint32(raw, 9);
        this.bolusSourceId = raw[13];
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
    
    public int getStatusId() {
        return statusId;
    }
    public CurrentBolusStatus getStatus() {
        return CurrentBolusStatus.fromId(statusId);
    }

    public enum CurrentBolusStatus {
        REQUESTING(2),
        DELIVERING(1),
        ALREADY_DELIVERED_OR_INVALID(0),
        ;

        private final int id;
        CurrentBolusStatus(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }

        public static CurrentBolusStatus fromId(int id) {
            for (CurrentBolusStatus s : values()) {
                if (s.id == id) {
                    return s;
                }
            }
            return null;
        }
    }

    public int getBolusId() {
        return bolusId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    /**
     * @return the {@link Instant} representing the timestamp in which the current bolus state
     * ({@link CurrentBolusStatus}) was entered, according to the pump. i.e., if the status is in
     * DELIVERING, then the timestamp the delivery started.
     */
    public Instant getTimestampInstant() {
        return Dates.fromJan12008EpochSecondsToDate(timestamp);
    }

    /**
     * @return the requested insulin volume in milliunits. To convert to human-readable format,
     * use {@link com.jwoglom.pumpx2.pump.messages.models.InsulinUnit#from1000To1(Long)}).
     */
    public long getRequestedVolume() {
        return requestedVolume;
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
    public Set<BolusDeliveryHistoryLog.BolusType> getBolusTypes() {
        return BolusDeliveryHistoryLog.BolusType.fromBitmask(bolusTypeBitmask);
    }

    /**
     * @return whether data is filled and this output can be used. If not valid, then the bolus
     * is no longer "current" and {@link com.jwoglom.pumpx2.pump.messages.builders.LastBolusStatusRequestBuilder} must be used.
     */
    public boolean isValid() {
        return !(getStatus() == CurrentBolusStatus.ALREADY_DELIVERED_OR_INVALID && getBolusId() == 0 && getTimestamp() == 0);

    }
    
}