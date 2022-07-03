package com.jwoglom.pumpx2.pump.messages.response.currentStatus;

import com.google.common.base.Preconditions;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.request.currentStatus.CGMOORAlertSettingsRequest;

@MessageProps(
    opCode=95,
    size=3,
    type=MessageType.RESPONSE,
    request=CGMOORAlertSettingsRequest.class
)
public class CGMOORAlertSettingsResponse extends Message {

    private int sensorTimeoutAlertThreshold;
    private int sensorTimeoutAlertEnabled;
    private int sensorTimeoutDefaultBitmask;
    
    public CGMOORAlertSettingsResponse() {}
    
    public CGMOORAlertSettingsResponse(int sensorTimeoutAlertThreshold, int sensorTimeoutAlertEnabled, int sensorTimeoutDefaultBitmask) {
        this.cargo = buildCargo(sensorTimeoutAlertThreshold, sensorTimeoutAlertEnabled, sensorTimeoutDefaultBitmask);
        this.sensorTimeoutAlertThreshold = sensorTimeoutAlertThreshold;
        this.sensorTimeoutAlertEnabled = sensorTimeoutAlertEnabled;
        this.sensorTimeoutDefaultBitmask = sensorTimeoutDefaultBitmask;
        
    }

    public void parse(byte[] raw) {
        Preconditions.checkArgument(raw.length == props().size());
        this.cargo = raw;
        this.sensorTimeoutAlertThreshold = raw[0];
        this.sensorTimeoutAlertEnabled = raw[1];
        this.sensorTimeoutDefaultBitmask = raw[2];
        
    }

    
    public static byte[] buildCargo(int sensorTimeoutAlertThreshold, int sensorTimeoutAlertEnabled, int sensorTimeoutDefaultBitmask) {
        return Bytes.combine(
            new byte[]{ (byte) sensorTimeoutAlertThreshold },
            new byte[]{ (byte) sensorTimeoutAlertEnabled },
            new byte[]{ (byte) sensorTimeoutDefaultBitmask });
    }

    public int getSensorTimeoutAlertThreshold() {
        return sensorTimeoutAlertThreshold;
    }
    public int getSensorTimeoutAlertEnabled() {
        return sensorTimeoutAlertEnabled;
    }
    public int getSensorTimeoutDefaultBitmask() {
        return sensorTimeoutDefaultBitmask;
    }
    
}