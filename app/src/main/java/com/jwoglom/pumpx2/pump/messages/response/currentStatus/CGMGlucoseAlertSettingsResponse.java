package com.jwoglom.pumpx2.pump.messages.response.currentStatus;

import com.google.common.base.Preconditions;
import com.jwoglom.pumpx2.pump.messages.Bytes;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.request.currentStatus.CGMGlucoseAlertSettingsRequest;

@MessageProps(
    opCode=91,
    size=12,
    type=MessageType.RESPONSE,
    request=CGMGlucoseAlertSettingsRequest.class
)
public class CGMGlucoseAlertSettingsResponse extends Message {
    
    private int highGlucoseAlertThreshold;
    private int highGlucoseAlertEnabled;
    private int highGlucoseRepeatDuration;
    private int highGlucoseAlertDefaultBitmask;
    private int lowGlucoseAlertThreshold;
    private int lowGlucoseAlertEnabled;
    private int lowGlucoseRepeatDuration;
    private int lowGlucoseAlertDefaultBitmask;
    
    public CGMGlucoseAlertSettingsResponse() {}
    
    public CGMGlucoseAlertSettingsResponse(int highGlucoseAlertThreshold, int highGlucoseAlertEnabled, int highGlucoseRepeatDuration, int highGlucoseAlertDefaultBitmask, int lowGlucoseAlertThreshold, int lowGlucoseAlertEnabled, int lowGlucoseRepeatDuration, int lowGlucoseAlertDefaultBitmask) {
        this.cargo = buildCargo(highGlucoseAlertThreshold, highGlucoseAlertEnabled, highGlucoseRepeatDuration, highGlucoseAlertDefaultBitmask, lowGlucoseAlertThreshold, lowGlucoseAlertEnabled, lowGlucoseRepeatDuration, lowGlucoseAlertDefaultBitmask);
        this.highGlucoseAlertThreshold = highGlucoseAlertThreshold;
        this.highGlucoseAlertEnabled = highGlucoseAlertEnabled;
        this.highGlucoseRepeatDuration = highGlucoseRepeatDuration;
        this.highGlucoseAlertDefaultBitmask = highGlucoseAlertDefaultBitmask;
        this.lowGlucoseAlertThreshold = lowGlucoseAlertThreshold;
        this.lowGlucoseAlertEnabled = lowGlucoseAlertEnabled;
        this.lowGlucoseRepeatDuration = lowGlucoseRepeatDuration;
        this.lowGlucoseAlertDefaultBitmask = lowGlucoseAlertDefaultBitmask;
        
    }

    public void parse(byte[] raw) {
        Preconditions.checkArgument(raw.length == props().size());
        this.cargo = raw;
        this.highGlucoseAlertThreshold = Bytes.readShort(raw, 0);
        this.highGlucoseAlertEnabled = raw[2];
        this.highGlucoseRepeatDuration = Bytes.readShort(raw, 3);
        this.highGlucoseAlertDefaultBitmask = raw[5]; // TODO: what is this bitmask?
        this.lowGlucoseAlertThreshold = Bytes.readShort(raw, 6);
        this.lowGlucoseAlertEnabled = raw[8];
        this.lowGlucoseRepeatDuration = Bytes.readShort(raw, 9);
        this.lowGlucoseAlertDefaultBitmask = raw[11]; // TODO: what is this bitmask?
        
    }

    
    public static byte[] buildCargo(int byte0short, int byte2, int byte3short, int byte5, int byte6short, int byte8, int byte9short, int byte11) {
        return Bytes.combine(
            Bytes.firstTwoBytesLittleEndian(byte0short), 
            new byte[]{ (byte) byte2 }, 
            Bytes.firstTwoBytesLittleEndian(byte3short), 
            new byte[]{ (byte) byte5 }, 
            Bytes.firstTwoBytesLittleEndian(byte6short), 
            new byte[]{ (byte) byte8 }, 
            Bytes.firstTwoBytesLittleEndian(byte9short), 
            new byte[]{ (byte) byte11 });
    }
    
    public int getHighGlucoseAlertThreshold() {
        return highGlucoseAlertThreshold;
    }
    public int getHighGlucoseAlertEnabled() {
        return highGlucoseAlertEnabled;
    }
    public int getHighGlucoseRepeatDuration() {
        return highGlucoseRepeatDuration;
    }
    public int getHighGlucoseAlertDefaultBitmask() {
        return highGlucoseAlertDefaultBitmask;
    }
    public int getLowGlucoseAlertThreshold() {
        return lowGlucoseAlertThreshold;
    }
    public int getLowGlucoseAlertEnabled() {
        return lowGlucoseAlertEnabled;
    }
    public int getLowGlucoseRepeatDuration() {
        return lowGlucoseRepeatDuration;
    }
    public int getLowGlucoseAlertDefaultBitmask() {
        return lowGlucoseAlertDefaultBitmask;
    }
    
}