package com.jwoglom.pumpx2.pump.messages.response.control;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.bluetooth.Characteristic;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.models.StatusMessage;
import com.jwoglom.pumpx2.pump.messages.models.SupportedDevices;
import com.jwoglom.pumpx2.pump.messages.request.control.RenameIDPRequest;

@MessageProps(
    opCode=-87,
    size=2,
    type=MessageType.RESPONSE,
    characteristic=Characteristic.CONTROL,
    signed=true,
    request=RenameIDPRequest.class,
    modifiesInsulinDelivery=true,
    supportedDevices=SupportedDevices.MOBI_ONLY
)
public class RenameIDPResponse extends StatusMessage {
    
    private int status;
    private int numberOfProfiles;
    
    public RenameIDPResponse() {}
    
    public RenameIDPResponse(int status, int numberOfProfiles) {
        this.cargo = buildCargo(status, numberOfProfiles);
        this.status = status;
        this.numberOfProfiles = numberOfProfiles;
        
    }

    public void parse(byte[] raw) { 
        raw = this.removeSignedRequestHmacBytes(raw);
        Validate.isTrue(raw.length == props().size());
        this.cargo = raw;
        this.status = raw[0];
        this.numberOfProfiles = raw[1]; // ?
        
    }

    
    public static byte[] buildCargo(int status, int newIdpId) {
        return Bytes.combine(
            new byte[]{ (byte) status },
            new byte[]{ (byte) newIdpId });
    }
    
    public int getStatus() {
        return status;
    }

    public int getNumberOfProfiles() {
        return numberOfProfiles;
    }
}