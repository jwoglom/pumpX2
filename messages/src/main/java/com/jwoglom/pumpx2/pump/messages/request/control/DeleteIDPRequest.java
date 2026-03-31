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
    private int profileIndex;

    public DeleteIDPRequest() {}

    /**
     * @deprecated Use {@link #DeleteIDPRequest(int, int)} instead to pass the correct profileIndex.
     */
    @Deprecated
    public DeleteIDPRequest(int idpId) {
        this(idpId, 0);
    }

    /**
     * @param idpId the insulin delivery profile ID (NOT SLOT)
     * @param profileIndex the slot index (0-5) of this profile in ProfileStatusResponse
     */
    public DeleteIDPRequest(int idpId, int profileIndex) {
        this.cargo = buildCargo(idpId, profileIndex);
        this.idpId = idpId;
        this.profileIndex = profileIndex;

    }

    public void parse(byte[] raw) {
        raw = this.removeSignedRequestHmacBytes(raw);
        Validate.isTrue(raw.length == props().size());
        this.cargo = raw;
        this.idpId = raw[0];
        this.profileIndex = raw[1];

    }


    public static byte[] buildCargo(int idpId, int profileIndex) {
        return Bytes.combine(
            new byte[]{ (byte) idpId },
            new byte[]{ (byte) profileIndex }
        );
    }
    public int getIdpId() {
        return idpId;
    }
    public int getProfileIndex() {
        return profileIndex;
    }
    
    
}