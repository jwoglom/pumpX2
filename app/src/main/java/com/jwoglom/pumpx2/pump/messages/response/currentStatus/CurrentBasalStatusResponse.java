package com.jwoglom.pumpx2.pump.messages.response.currentStatus;

import com.google.common.base.Preconditions;
import com.jwoglom.pumpx2.pump.messages.Bytes;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.request.currentStatus.CurrentBasalStatusRequest;

import java.math.BigInteger;

@MessageProps(
    opCode=41,
    size=9,
    type=MessageType.RESPONSE,
    request=CurrentBasalStatusRequest.class
)
public class CurrentBasalStatusResponse extends Message {
    
    private long profileBasalRate;
    private long currentBasalRate;
    private int basalModifiedBitmask;
    
    public CurrentBasalStatusResponse() {}
    
    public CurrentBasalStatusResponse(long profileBasalRate, long currentBasalRate, int basalModifiedBitmask) {
        this.cargo = buildCargo(profileBasalRate, currentBasalRate, basalModifiedBitmask);
        this.profileBasalRate = profileBasalRate;
        this.currentBasalRate = currentBasalRate;
        this.basalModifiedBitmask = basalModifiedBitmask;
        
    }

    public void parse(byte[] raw) {
        Preconditions.checkArgument(raw.length == props().size());
        this.cargo = raw;
        this.profileBasalRate = Bytes.readUint32(raw, 0);
        this.currentBasalRate = Bytes.readUint32(raw, 4);
        this.basalModifiedBitmask = raw[8];
        
    }

    
    public static byte[] buildCargo(long profileBasalRate, long currentBasalRate, int basalModifiedBitmask) {
        return Bytes.combine(
            Bytes.toUint32(profileBasalRate), 
            Bytes.toUint32(currentBasalRate), 
            new byte[]{ (byte) basalModifiedBitmask });
    }
    
    public long getProfileBasalRate() {
        return profileBasalRate;
    }
    public long getCurrentBasalRate() {
        return currentBasalRate;
    }
    public int getBasalModifiedBitmask() {
        return basalModifiedBitmask;
    }
    
}