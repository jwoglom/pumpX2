package com.jwoglom.pumpx2.pump.messages.response.currentStatus;

import com.google.common.base.Preconditions;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.request.currentStatus.GlobalMaxBolusSettingsRequest;

@MessageProps(
    opCode=-115,
    size=4,
    type=MessageType.RESPONSE,
    request=GlobalMaxBolusSettingsRequest.class
)
public class GlobalMaxBolusSettingsResponse extends Message {
    
    private int maxBolus;
    private int maxBolusDefault;
    
    public GlobalMaxBolusSettingsResponse() {}
    
    public GlobalMaxBolusSettingsResponse(int maxBolus, int maxBolusDefault) {
        this.cargo = buildCargo(maxBolus, maxBolusDefault);
        this.maxBolus = maxBolus;
        this.maxBolusDefault = maxBolusDefault;
        
    }

    public void parse(byte[] raw) {
        Preconditions.checkArgument(raw.length == props().size());
        this.cargo = raw;
        this.maxBolus = Bytes.readShort(raw, 0);
        this.maxBolusDefault = Bytes.readShort(raw, 2);
        
    }

    
    public static byte[] buildCargo(int maxBolus, int maxBolusDefault) {
        return Bytes.combine(
            Bytes.firstTwoBytesLittleEndian(maxBolus), 
            Bytes.firstTwoBytesLittleEndian(maxBolusDefault));
    }
    
    public int getMaxBolus() {
        return maxBolus;
    }
    public int getMaxBolusDefault() {
        return maxBolusDefault;
    }
    
}