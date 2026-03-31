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
    private int profileIndex;
    private String profileName;

    public RenameIDPRequest() {}

    /**
     * @deprecated Use {@link #RenameIDPRequest(int, int, String)} instead to pass the correct profileIndex.
     */
    @Deprecated
    public RenameIDPRequest(int idpId, String profileName) {
        this(idpId, 0, profileName);
    }

    /**
     * @param idpId ID of insulin delivery profile (NOT SLOT)
     * @param profileIndex the slot index (0-5) of this profile in ProfileStatusResponse
     * @param profileName new name to set on profile
     */
    public RenameIDPRequest(int idpId, int profileIndex, String profileName) {
        this.cargo = buildCargo(idpId, profileIndex, profileName);
        this.idpId = idpId;
        this.profileIndex = profileIndex;
        this.profileName = profileName;

    }

    public void parse(byte[] raw) {
        raw = this.removeSignedRequestHmacBytes(raw);
        Validate.isTrue(raw.length == props().size());
        this.cargo = raw;
        this.idpId = raw[0];
        this.profileIndex = raw[1];
        this.profileName = Bytes.readString(raw, 2, 16);
    }


    public static byte[] buildCargo(int idpId, int profileIndex, String profileName) {
        return Bytes.combine(
            new byte[]{ (byte) idpId },
            new byte[]{ (byte) profileIndex },
            Bytes.writeString(profileName, 16),
            new byte[]{ 0 }
        );
    }
    public int getIdpId() {
        return idpId;
    }
    public int getProfileIndex() {
        return profileIndex;
    }
    public String getProfileName() {
        return profileName;
    }
    
    
}