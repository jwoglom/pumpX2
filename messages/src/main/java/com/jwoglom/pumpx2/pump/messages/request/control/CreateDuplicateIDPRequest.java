package com.jwoglom.pumpx2.pump.messages.request.control;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.bluetooth.Characteristic;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.models.SupportedDevices;
import com.jwoglom.pumpx2.pump.messages.response.control.CreateDuplicateIDPResponse;

/**
 * Creates a new Insulin Delivery Profile from sourceIdpId with a new title.
 * The pump copies all profile configurations and segments from sourceIdpId to the new IDP.
 */
@MessageProps(
    opCode=-26,
    size=35,
    type=MessageType.REQUEST,
    characteristic=Characteristic.CONTROL,
    signed=true,
    response=CreateDuplicateIDPResponse.class,
    modifiesInsulinDelivery=true,
    supportedDevices=SupportedDevices.MOBI_ONLY
)
public class CreateDuplicateIDPRequest extends Message { 
    private String newProfileName;
    private int sourceIdpId;
    
    public CreateDuplicateIDPRequest() {}

    public CreateDuplicateIDPRequest(String newProfileName, int sourceIdpId) {
        this.cargo = buildCargo(newProfileName, sourceIdpId);
        this.newProfileName = newProfileName;
        this.sourceIdpId = sourceIdpId;
        
    }

    public void parse(byte[] raw) { 
        raw = this.removeSignedRequestHmacBytes(raw);
        Validate.isTrue(raw.length == props().size());
        this.cargo = raw;
        this.newProfileName = Bytes.readString(raw, 0, 16);
        // empty 17 - 32
        this.sourceIdpId = raw[33];
    }

    
    public static byte[] buildCargo(String newProfileName, int sourceIdpId) {
        return Bytes.combine(
            Bytes.writeString(newProfileName, 16),
            new byte[17],
            new byte[]{ (byte) sourceIdpId },
            new byte[]{ 0 }
        );
    }
}