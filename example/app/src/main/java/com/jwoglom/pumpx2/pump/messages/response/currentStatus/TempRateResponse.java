package com.jwoglom.pumpx2.pump.messages.response.currentStatus;

import com.google.common.base.Preconditions;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.request.currentStatus.TempRateRequest;

@MessageProps(
    opCode=43,
    size=10,
    type=MessageType.RESPONSE,
    request=TempRateRequest.class
)
public class TempRateResponse extends Message {
    
    private boolean active;
    private int percentage;
    private long startTime;
    private long duration;
    
    public TempRateResponse() {}
    
    public TempRateResponse(boolean active, int percentage, long startTime, long duration) {
        this.cargo = buildCargo(active, percentage, startTime, duration);
        this.active = active;
        this.percentage = percentage;
        this.startTime = startTime;
        this.duration = duration;
        
    }

    public void parse(byte[] raw) {
        Preconditions.checkArgument(raw.length == props().size());
        this.cargo = raw;
        this.active = raw[0] != 0;
        this.percentage = raw[1];
        this.startTime = Bytes.readUint32(raw, 2);
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
    public long getStartTime() {
        return startTime;
    }
    public long getDuration() {
        return duration;
    }
    
}