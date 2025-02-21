package com.jwoglom.pumpx2.pump.messages.response.control;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.bluetooth.Characteristic;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.models.StatusMessage;
import com.jwoglom.pumpx2.pump.messages.request.control.SetQuickBolusSettingsRequest;

import java.math.BigInteger;

@MessageProps(
    opCode=-45,
    size=1,
    type=MessageType.RESPONSE,
    characteristic=Characteristic.CONTROL,
    signed=true,
    request=SetQuickBolusSettingsRequest.class
)
public class SetQuickBolusSettingsResponse extends StatusMessage {

    private int status;
    
    
    public SetQuickBolusSettingsResponse() {
        this.cargo = EMPTY;
        
    }

    public SetQuickBolusSettingsResponse(byte[] raw) {
        this.cargo = raw;
        parse(raw);
    }

    public void parse(byte[] raw) { 
        raw = this.removeSignedRequestHmacBytes(raw);
        Validate.isTrue(raw.length == props().size());
        this.cargo = raw;
        this.status = raw[0]; // 0 = ok
        
    }


    /**
     * @return 0 if successful
     */
    public int getStatus() {
        return status;
    }
}