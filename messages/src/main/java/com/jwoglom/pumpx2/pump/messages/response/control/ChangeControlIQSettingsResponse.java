package com.jwoglom.pumpx2.pump.messages.response.control;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.bluetooth.Characteristic;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.models.StatusMessage;
import com.jwoglom.pumpx2.pump.messages.request.control.ChangeControlIQSettingsRequest;

import java.math.BigInteger;

@MessageProps(
    opCode=-53,
    size=3,
    type=MessageType.RESPONSE,
    characteristic=Characteristic.CONTROL,
    signed=true,
    request=ChangeControlIQSettingsRequest.class
)
public class ChangeControlIQSettingsResponse extends StatusMessage {
    
    private int status;

    public ChangeControlIQSettingsResponse() {
        this.cargo = EMPTY;
    }

    public ChangeControlIQSettingsResponse(byte[] raw) {
        parse(raw);
    }

    public void parse(byte[] raw) { 
        raw = this.removeSignedRequestHmacBytes(raw);
        Validate.isTrue(raw.length == props().size());
        this.cargo = raw;
        this.status = raw[0];
    }


    @Override
    public int getStatus() {
        return this.status;
    }
}