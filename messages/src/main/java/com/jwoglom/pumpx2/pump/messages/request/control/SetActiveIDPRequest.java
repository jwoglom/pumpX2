package com.jwoglom.pumpx2.pump.messages.request.control;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.bluetooth.Characteristic;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.models.SupportedDevices;
import com.jwoglom.pumpx2.pump.messages.response.control.SetActiveIDPResponse;

/**
 * Activates an Insulin Delivery Profile (aka profile).
 */
@MessageProps(
    opCode=-20,
    size=2,
    type=MessageType.REQUEST,
    characteristic=Characteristic.CONTROL,
    signed=true,
    response=SetActiveIDPResponse.class,
    modifiesInsulinDelivery=true,
    supportedDevices=SupportedDevices.MOBI_ONLY
)
public class SetActiveIDPRequest extends Message {
    private int idpId;
    private int profileIndex;

    public SetActiveIDPRequest() {}

    public SetActiveIDPRequest(byte[] raw) {
        parse(raw);
    }

    /**
     * @deprecated Use {@link #SetActiveIDPRequest(int, int)} instead to pass the correct profileIndex.
     */
    @Deprecated
    public SetActiveIDPRequest(int idpId) {
        this(idpId, 0);
    }

    /**
     * @param idpId the insulin delivery profile ID (NOT SLOT)
     * @param profileIndex the slot index (0-5) of this profile in ProfileStatusResponse
     */
    public SetActiveIDPRequest(int idpId, int profileIndex) {
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