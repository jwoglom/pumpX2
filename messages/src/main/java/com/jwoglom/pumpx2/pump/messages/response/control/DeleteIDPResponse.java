package com.jwoglom.pumpx2.pump.messages.response.control;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.bluetooth.Characteristic;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.models.StatusMessage;
import com.jwoglom.pumpx2.pump.messages.models.SupportedDevices;
import com.jwoglom.pumpx2.pump.messages.request.control.DeleteIDPRequest;

import java.math.BigInteger;

@MessageProps(
    opCode=-81,
    size=2,
    type=MessageType.RESPONSE,
    characteristic=Characteristic.CONTROL,
    signed=true,
    request=DeleteIDPRequest.class,
    modifiesInsulinDelivery=true,
    supportedDevices=SupportedDevices.MOBI_ONLY
)
public class DeleteIDPResponse extends StatusMessage {
    
    private int status;
    private int deletedIdpId;
    
    public DeleteIDPResponse() {}
    
    public DeleteIDPResponse(int status) {
        this.cargo = buildCargo(status, 0);
        this.status = status;
    }

    public DeleteIDPResponse(int status, int deletedIdpId) {
        this.cargo = buildCargo(status, deletedIdpId);
        this.status = status;
        this.deletedIdpId = deletedIdpId;
    }

    public void parse(byte[] raw) { 
        raw = this.removeSignedRequestHmacBytes(raw);
        Validate.isTrue(raw.length == props().size());
        this.cargo = raw;
        this.status = raw[0];
        this.deletedIdpId = raw[1];
    }

    
    public static byte[] buildCargo(int status, int deletedIdpId) {
        return Bytes.combine(
            new byte[]{ (byte) status, (byte) deletedIdpId });
    }
    
    public int getStatus() {
        return status;
    }

    public int getDeletedIdpId() {
        return deletedIdpId;
    }
}