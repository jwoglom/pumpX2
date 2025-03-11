package com.jwoglom.pumpx2.pump.messages.response.control;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.bluetooth.Characteristic;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.models.StatusMessage;
import com.jwoglom.pumpx2.pump.messages.request.control.SetIDPSegmentRequest;

import java.math.BigInteger;

@MessageProps(
    opCode=-85,
    size=2,
    type=MessageType.RESPONSE,
    characteristic=Characteristic.CONTROL,
    signed=true,
    request=SetIDPSegmentRequest.class
)
public class SetIDPSegmentResponse extends StatusMessage {
    
    private int status;
    private int unknown;

    public SetIDPSegmentResponse() {
        this.cargo = EMPTY;
        
    }

    public SetIDPSegmentResponse(byte[] raw) {
        this.cargo = raw;
        parse(raw);
    }

    public void parse(byte[] raw) { 
        raw = this.removeSignedRequestHmacBytes(raw);
        Validate.isTrue(raw.length == props().size());
        this.cargo = raw;
        this.status = raw[0];
        this.unknown = raw[1];
        
    }


    @Override
    public int getStatus() {
        return status;
    }

    public int getUnknown() {
        return unknown;
    }
}