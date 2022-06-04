package com.jwoglom.pumpx2.pump.messages.response.currentStatus;

import com.google.common.base.Preconditions;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.helpers.Dates;
import com.jwoglom.pumpx2.pump.messages.request.currentStatus.TimeSinceResetRequest;

import java.time.Instant;
import java.util.Date;

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
    
    public long getTimeSinceResetRaw() {
        return timeSinceReset;
    }
    public Instant getTimeSinceReset() {
        return Dates.fromJan12008EpochSecondsToDate(timeSinceReset);
    }
    public long getPumpTimeSeconds() {
        return pumpTime;
    }
    
}