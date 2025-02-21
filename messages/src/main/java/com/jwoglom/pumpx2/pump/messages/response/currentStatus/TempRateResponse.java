package com.jwoglom.pumpx2.pump.messages.response.currentStatus;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.helpers.Dates;
import com.jwoglom.pumpx2.pump.messages.request.currentStatus.TempRateRequest;

import java.time.Instant;

@MessageProps(
    opCode=43,
    size=10,
    type=MessageType.RESPONSE,
    request=TempRateRequest.class
)
public class TempRateResponse extends Message {
    
    private boolean active;
    private int percentage;
    private long startTimeRaw;
    private long duration;
    
    public TempRateResponse() {}
    public TempRateResponse(byte[] raw) {
        parse(raw);
    }
    
    public TempRateResponse(boolean active, int percentage, long startTimeRaw, long duration) {
        this.cargo = buildCargo(active, percentage, startTimeRaw, duration);
        this.active = active;
        this.percentage = percentage;
        this.startTimeRaw = startTimeRaw;
        this.duration = duration;
        
    }

    public void parse(byte[] raw) {
        Validate.isTrue(raw.length == props().size());
        this.cargo = raw;
        this.active = raw[0] != 0;
        this.percentage = (int) (raw[1] & 255);
        this.startTimeRaw = Bytes.readUint32(raw, 2);
        this.duration = Bytes.readUint32(raw, 6);
        
    }

    
    public static byte[] buildCargo(boolean active, int percentage, long startTime, long duration) {
        return Bytes.combine(
            new byte[]{ (byte) (active ? 1 : 0) }, 
            new byte[]{ (byte) percentage }, 
            Bytes.toUint32(startTime), 
            Bytes.toUint32(duration));
    }
    
    public boolean getActive() {
        return active;
    }
    public int getPercentage() {
        return percentage;
    }
    public long getStartTimeRaw() {
        return startTimeRaw;
    }

    /**
     * @return the evaluation of timeSinceReset against the epoch
     */
    public Instant getStartTimeInstant() {
        return Dates.fromJan12008EpochSecondsToDate(startTimeRaw);
    }

    public long getDuration() {
        return duration;
    }
    
}