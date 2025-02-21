package com.jwoglom.pumpx2.pump.messages.response.control;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.bluetooth.Characteristic;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.models.SupportedDevices;
import com.jwoglom.pumpx2.pump.messages.request.control.CreateIDPRequest;

@MessageProps(
    opCode=-25,
    size=2,
    type=MessageType.RESPONSE,
    characteristic=Characteristic.CONTROL,
    signed=true,
    request= CreateIDPRequest.class,
    modifiesInsulinDelivery=true,
    supportedDevices=SupportedDevices.MOBI_ONLY
)
public class CreateIDPResponse extends Message {
    
    private int status;
    private int newIdpId;
    
    public CreateIDPResponse() {}

    public CreateIDPResponse(byte[] raw) {
        this.cargo = raw;
        parse(raw);

    }
    public CreateIDPResponse(int status, int newIdpId) {
        this.cargo = buildCargo(status, newIdpId);
        this.status = status;
        this.newIdpId = newIdpId;
        
    }

    public void parse(byte[] raw) { 
        raw = this.removeSignedRequestHmacBytes(raw);
        Validate.isTrue(raw.length == props().size());
        this.cargo = raw;
        this.status = raw[0];
        this.newIdpId = raw[1];
        
    }

    
    public static byte[] buildCargo(int status, int newIdpId) {
        return Bytes.combine(
            new byte[]{ (byte) status, (byte) newIdpId });
    }
    
    public int getStatus() {
        return status;
    }

    public int getNewIdpId() {
        return newIdpId;
    }
}