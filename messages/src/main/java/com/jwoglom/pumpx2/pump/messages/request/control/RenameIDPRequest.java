package com.jwoglom.pumpx2.pump.messages.request.control;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.bluetooth.Characteristic;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.models.SupportedDevices;
import com.jwoglom.pumpx2.pump.messages.response.control.RenameIDPResponse;

/**
 * Updates the Insulin Delivery Profile with the given idpId with a new profile name.
 */
@MessageProps(
    opCode=-88,
    size=19,
    type=MessageType.REQUEST,
    characteristic=Characteristic.CONTROL,
    signed=true,
    response=RenameIDPResponse.class,
    modifiesInsulinDelivery=true,
    supportedDevices=SupportedDevices.MOBI_ONLY
)
public class RenameIDPRequest extends Message { 
    private int idpId;
    private String profileName;
    
    public RenameIDPRequest() {}

    /**
     * @param idpId ID of insulin delivery profile (NOT SLOT)
     * @param profileName new name to set on profile
     */
    public RenameIDPRequest(int idpId, String profileName) {
        this.cargo = buildCargo(idpId, profileName);
        this.idpId = idpId;
        this.profileName = profileName;
        
    }

    public void parse(byte[] raw) { 
        raw = this.removeSignedRequestHmacBytes(raw);
        Validate.isTrue(raw.length == props().size());
        this.cargo = raw;
        this.idpId = raw[0];
        this.profileName = Bytes.readString(raw, 2, 16);
    }

    
    public static byte[] buildCargo(int idpId, String profileName) {
        return Bytes.combine(
            new byte[]{ (byte) idpId },
            new byte[]{ 1 },
            Bytes.writeString(profileName, 16),
            new byte[]{ 0 }
        );
    }
    public int getIdpId() {
        return idpId;
    }
    public String getProfileName() {
        return profileName;
    }
    
    
}