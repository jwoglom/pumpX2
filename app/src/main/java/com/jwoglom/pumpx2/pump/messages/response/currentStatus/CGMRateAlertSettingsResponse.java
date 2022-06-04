package com.jwoglom.pumpx2.pump.messages.response.currentStatus;

import com.google.common.base.Preconditions;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.request.currentStatus.CGMRateAlertSettingsRequest;

@MessageProps(
    opCode=93,
    size=6,
    type=MessageType.RESPONSE,
    request=CGMRateAlertSettingsRequest.class
)
public class CGMRateAlertSettingsResponse extends Message {
    
    private int riseRateThreshold;
    private int riseRateEnabled;
    private int riseRateDefaultBitmask;
    private int fallRateThreshold;
    private int fallRateEnabled;
    private int fallRateDefaultBitmask;
    
    public CGMRateAlertSettingsResponse() {}
    
    public CGMRateAlertSettingsResponse(int riseRateThreshold, int riseRateEnabled, int riseRateDefaultBitmask, int fallRateThreshold, int fallRateEnabled, int fallRateDefaultBitmask) {
        this.cargo = buildCargo(riseRateThreshold, riseRateEnabled, riseRateDefaultBitmask, fallRateThreshold, fallRateEnabled, fallRateDefaultBitmask);
        this.riseRateThreshold = riseRateThreshold;
        this.riseRateEnabled = riseRateEnabled;
        this.riseRateDefaultBitmask = riseRateDefaultBitmask;
        this.fallRateThreshold = fallRateThreshold;
        this.fallRateEnabled = fallRateEnabled;
        this.fallRateDefaultBitmask = fallRateDefaultBitmask;
        
    }

    public void parse(byte[] raw) {
        Preconditions.checkArgument(raw.length == props().size());
        this.cargo = raw;
        this.riseRateThreshold = raw[0];
        this.riseRateEnabled = raw[1];
        this.riseRateDefaultBitmask = raw[2]; // TODO: what is this bitmask?
        this.fallRateThreshold = raw[3];
        this.fallRateEnabled = raw[4];
        this.fallRateDefaultBitmask = raw[5]; // TODO: what is this bitmask?
        
    }

    
    public static byte[] buildCargo(int riseRateThreshold, int riseRateEnabled, int riseRateDefaultBitmask, int fallRateThreshold, int fallRateEnabled, int fallRateDefaultBitmask) {
        return Bytes.combine(
            new byte[]{ (byte) riseRateThreshold }, 
            new byte[]{ (byte) riseRateEnabled }, 
            new byte[]{ (byte) riseRateDefaultBitmask }, 
            new byte[]{ (byte) fallRateThreshold }, 
            new byte[]{ (byte) fallRateEnabled }, 
            new byte[]{ (byte) fallRateDefaultBitmask });
    }
    
    public int getRiseRateThreshold() {
        return riseRateThreshold;
    }
    public int getRiseRateEnabled() {
        return riseRateEnabled;
    }
    public int getRiseRateDefaultBitmask() {
        return riseRateDefaultBitmask;
    }
    public int getFallRateThreshold() {
        return fallRateThreshold;
    }
    public int getFallRateEnabled() {
        return fallRateEnabled;
    }
    public int getFallRateDefaultBitmask() {
        return fallRateDefaultBitmask;
    }
    
}