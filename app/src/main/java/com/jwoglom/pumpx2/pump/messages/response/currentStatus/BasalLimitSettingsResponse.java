package com.jwoglom.pumpx2.pump.messages.response.currentStatus;

import com.google.common.base.Preconditions;
import com.jwoglom.pumpx2.pump.messages.Bytes;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.request.currentStatus.BasalLimitSettingsRequest;

@MessageProps(
    opCode=-117,
    size=8,
    type=MessageType.RESPONSE,
    request= BasalLimitSettingsRequest.class
)
public class BasalLimitSettingsResponse extends Message {
    
    private long basalLimit;
    private long basalLimitDefault;
    
    public BasalLimitSettingsResponse() {}
    
    public BasalLimitSettingsResponse(long basalLimit, long basalLimitDefault) {
        this.cargo = buildCargo(basalLimit, basalLimitDefault);
        this.basalLimit = basalLimit;
        this.basalLimitDefault = basalLimitDefault;
        
    }

    public void parse(byte[] raw) {
        Preconditions.checkArgument(raw.length == props().size());
        this.cargo = raw;
        this.basalLimit = Bytes.readUint32(raw, 0);
        this.basalLimitDefault = Bytes.readUint32(raw, 4);
        
    }

    
    public static byte[] buildCargo(long basalLimit, long basalLimitDefault) {
        return Bytes.combine(
            Bytes.toUint32(basalLimit), 
            Bytes.toUint32(basalLimitDefault));
    }
    
    public long getBasalLimit() {
        return basalLimit;
    }
    public long getBasalLimitDefault() {
        return basalLimitDefault;
    }
    
}