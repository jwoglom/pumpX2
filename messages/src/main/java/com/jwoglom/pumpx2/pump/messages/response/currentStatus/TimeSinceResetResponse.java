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
 * Returns information on the pump's current internal time, which is different from the
 * human-visible time and date which is stored in the history log. The currentTime is the
 * user-set clock time.
 */
@MessageProps(
    opCode=55,
    size=8,
    type=MessageType.RESPONSE,
    request=TimeSinceResetRequest.class
)
public class TimeSinceResetResponse extends Message {
    
    private long currentTime;
    private long pumpTimeSinceReset;
    
    public TimeSinceResetResponse() {}
    
    public TimeSinceResetResponse(long currentTime, long pumpTimeSinceReset) {
        this.cargo = buildCargo(currentTime, pumpTimeSinceReset);
        this.currentTime = currentTime;
        this.pumpTimeSinceReset = pumpTimeSinceReset;
        
    }

    public void parse(byte[] raw) {
        Preconditions.checkArgument(raw.length == props().size());
        this.cargo = raw;
        this.currentTime = Bytes.readUint32(raw, 0);
        this.pumpTimeSinceReset = Bytes.readUint32(raw, 4);
    }

    
    public static byte[] buildCargo(long timeSinceReset, long pumpTime) {
        return Bytes.combine(
            Bytes.toUint32(timeSinceReset), 
            Bytes.toUint32(pumpTime));
    }

    /**
     * @return the pump's internal realtime clock which is an actual date,
     * instead of the internal epoch time
     */
    public long getCurrentTime() {
        return currentTime;
    }

    /**
     * @return the evaluation of timeSinceReset against the epoch
     */
    public Instant getCurrentTimeInstant() {
        return Dates.fromJan12008EpochSecondsToDate(currentTime);
    }

    /**
     * @return the number of seconds the pump has been on since it was last reset
     */
    public long getPumpTimeSecondsSinceReset() {
        return pumpTimeSinceReset;
    }
    
}