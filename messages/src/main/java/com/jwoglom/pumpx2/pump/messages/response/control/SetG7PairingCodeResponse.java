package com.jwoglom.pumpx2.pump.messages.response.control;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.bluetooth.Characteristic;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.models.StatusMessage;
import com.jwoglom.pumpx2.pump.messages.request.control.SetG7PairingCodeRequest;

import java.math.BigInteger;

@MessageProps(
    opCode=-3,
    size=2, // +24
    type=MessageType.RESPONSE,
    characteristic=Characteristic.CONTROL,
    signed=true,
    request=SetG7PairingCodeRequest.class
)
public class SetG7PairingCodeResponse extends StatusMessage {
    
    private int status;
    
    public SetG7PairingCodeResponse() {}
    
    public SetG7PairingCodeResponse(int status) {
        this.cargo = buildCargo(status);
        this.status = status;
        
    }

    public void parse(byte[] raw) { 
        raw = this.removeSignedRequestHmacBytes(raw);
        Validate.isTrue(raw.length == props().size());
        this.cargo = raw;
        this.status = raw[0];
        
    }

    
    public static byte[] buildCargo(int status) {
        return Bytes.combine(
            new byte[]{ (byte) status, 0 });
    }
    
    public int getStatus() {
        return status;
    }
    
}