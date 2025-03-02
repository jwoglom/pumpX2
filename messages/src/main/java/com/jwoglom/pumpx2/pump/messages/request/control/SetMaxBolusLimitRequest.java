package com.jwoglom.pumpx2.pump.messages.request.control;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.bluetooth.Characteristic;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.response.control.SetMaxBolusLimitResponse;

@MessageProps(
    opCode=-122,
    size=2,
    type=MessageType.REQUEST,
    characteristic=Characteristic.CONTROL,
    signed=true,
    response= SetMaxBolusLimitResponse.class
)
public class SetMaxBolusLimitRequest extends Message {
    private int maxBolusMilliunits;

    public SetMaxBolusLimitRequest() {}

    public SetMaxBolusLimitRequest(int maxBolusMilliunits) {
        this.cargo = buildCargo(maxBolusMilliunits);
        this.maxBolusMilliunits = maxBolusMilliunits;
        
    }

    public void parse(byte[] raw) { 
        raw = this.removeSignedRequestHmacBytes(raw);
        Validate.isTrue(raw.length == props().size());
        this.cargo = raw;
        this.maxBolusMilliunits = Bytes.readShort(raw, 0);
        
    }

    
    public static byte[] buildCargo(int maxBolusMilliunits) {
        return Bytes.combine(
            Bytes.firstTwoBytesLittleEndian(maxBolusMilliunits)
        );
    }
    public int getMaxBolusMilliunits() {
        return maxBolusMilliunits;
    }

    
}