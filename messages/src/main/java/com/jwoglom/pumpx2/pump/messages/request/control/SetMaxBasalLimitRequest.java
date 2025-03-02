package com.jwoglom.pumpx2.pump.messages.request.control;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.bluetooth.Characteristic;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.response.control.SetMaxBasalLimitResponse;

@MessageProps(
    opCode=-120,
    size=4,
    type=MessageType.REQUEST,
    characteristic=Characteristic.CONTROL,
    signed=true,
    response=SetMaxBasalLimitResponse.class
)
public class SetMaxBasalLimitRequest extends Message { 
    private int maxHourlyBasalMilliunits;
    
    public SetMaxBasalLimitRequest() {}

    public SetMaxBasalLimitRequest(int maxHourlyBasalMilliunits) {
        this.cargo = buildCargo(maxHourlyBasalMilliunits);
        this.maxHourlyBasalMilliunits = maxHourlyBasalMilliunits;
        
    }

    public void parse(byte[] raw) { 
        raw = this.removeSignedRequestHmacBytes(raw);
        Validate.isTrue(raw.length == props().size());
        this.cargo = raw;
        this.maxHourlyBasalMilliunits = Bytes.readShort(raw, 0);
        
    }

    
    public static byte[] buildCargo(int maxHourlyBasalMilliunits) {
        return Bytes.combine(
            Bytes.firstTwoBytesLittleEndian(maxHourlyBasalMilliunits),
            new byte[2]
        );
    }
    public int getMaxHourlyBasalMilliunits() {
        return maxHourlyBasalMilliunits;
    }
    
    
}