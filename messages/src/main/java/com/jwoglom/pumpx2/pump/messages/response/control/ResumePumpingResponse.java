package com.jwoglom.pumpx2.pump.messages.response.control;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.bluetooth.Characteristic;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.models.SupportedDevices;
import com.jwoglom.pumpx2.pump.messages.request.control.ResumePumpingRequest;

import java.math.BigInteger;

@MessageProps(
    opCode=-101,
    size=1,
    type=MessageType.RESPONSE,
    characteristic=Characteristic.CONTROL,
    signed=true,
    modifiesInsulinDelivery=true,
    request=ResumePumpingRequest.class,
    supportedDevices=SupportedDevices.MOBI_ONLY
)
public class ResumePumpingResponse extends Message {

    private int status;

    public ResumePumpingResponse() {
        this.cargo = EMPTY;
    }
    
    public ResumePumpingResponse(byte[] cargo) {
        this.cargo = cargo;
    }

    public ResumePumpingResponse(int status) {
        this.cargo = buildCargo(status);
        parse(cargo);
    }

    public void parse(byte[] raw) { 
        raw = this.removeSignedRequestHmacBytes(raw);
        Validate.isTrue(raw.length == props().size());
        this.cargo = raw;
        
    }
    public static byte[] buildCargo(int status) {
        return Bytes.combine(
                new byte[]{(byte) status}
        );
    }

    public int getStatus() {
        return status;
    }

    
    
}