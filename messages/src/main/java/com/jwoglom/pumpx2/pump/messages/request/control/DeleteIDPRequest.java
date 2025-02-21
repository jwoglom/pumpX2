package com.jwoglom.pumpx2.pump.messages.request.control;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.bluetooth.Characteristic;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.models.SupportedDevices;
import com.jwoglom.pumpx2.pump.messages.response.control.DeleteIDPResponse;

@MessageProps(
    opCode=-82,
    size=2,
    type=MessageType.REQUEST,
    characteristic=Characteristic.CONTROL,
    signed=true,
    response=DeleteIDPResponse.class,
    modifiesInsulinDelivery=true,
    supportedDevices=SupportedDevices.MOBI_ONLY
)
public class DeleteIDPRequest extends Message { 
    private int idpId;
    
    public DeleteIDPRequest() {}

    public DeleteIDPRequest(int idpId) {
        this.cargo = buildCargo(idpId);
        this.idpId = idpId;
        
    }

    public void parse(byte[] raw) { 
        raw = this.removeSignedRequestHmacBytes(raw);
        Validate.isTrue(raw.length == props().size());
        this.cargo = raw;
        this.idpId = raw[0];
        
    }

    
    public static byte[] buildCargo(int idpId) {
        return Bytes.combine(
            new byte[]{ (byte) idpId },
            new byte[]{ 1 }
        );
    }
    public int getIdpId() {
        return idpId;
    }
    
    
}