package com.jwoglom.pumpx2.pump.messages.response.currentStatus;

import com.google.common.base.Preconditions;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.helpers.Dates;
import com.jwoglom.pumpx2.pump.messages.request.currentStatus.TimeSinceResetRequest;

import java.time.Instant;

/**
 * Returns information on the pump's current internal time. Different from the human-visible
 * time and date which is stored in the history log.
 */
@MessageProps(
    opCode=55,
    size=8,
    type=MessageType.RESPONSE,
    request=TimeSinceResetRequest.class
)
public class TimeSinceResetResponse extends Message {
    
    private long timeSinceReset;
    private long pumpTime;
    
    public TimeSinceResetResponse() {}
    
    public TimeSinceResetResponse(long timeSinceReset, long pumpTime) {
        this.cargo = buildCargo(timeSinceReset, pumpTime);
        this.timeSinceReset = timeSinceReset;
        this.pumpTime = pumpTime;
        
    }

    public void parse(byte[] raw) {
        Preconditions.checkArgument(raw.length == props().size());
        this.cargo = raw;
        this.timeSinceReset = Bytes.readUint32(raw, 0);
        this.pumpTime = Bytes.readUint32(raw, 4);
        
    }

    
    public static byte[] buildCargo(long timeSinceReset, long pumpTime) {
        return Bytes.combine(
            Bytes.toUint32(timeSinceReset), 
            Bytes.toUint32(pumpTime));
    }

    /**
     * IMPORTANT NOTE: These names are actually switched: this returns the pump's internal
     * realtime clock which is an actual date, instead of the internal epoch time... yes, it's
     * confusing.
     */
    public long getTimeSinceReset() {
        return timeSinceReset;
    }

    /**
     * @return the evaluation of timeSinceReset against the epoch
     */
    public Instant getTimeSinceResetInstant() {
        return Dates.fromJan12008EpochSecondsToDate(timeSinceReset);
    }

    /**
     * @return the number of seconds the pump has been on since it was last reset
     */
    public long getPumpTimeSeconds() {
        return pumpTime;
    }
    
}