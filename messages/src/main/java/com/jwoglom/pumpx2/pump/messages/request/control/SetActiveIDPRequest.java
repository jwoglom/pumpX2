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
    
    public SetActiveIDPRequest() {}

    public SetActiveIDPRequest(byte[] raw) {
        parse(raw);
    }

    /**
     * @param idpId the insulin delivery profile ID (NOT SLOT)
     */
    public SetActiveIDPRequest(int idpId) {
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
            new byte[]{1}
        );
    }
    public int getIdpId() {
        return idpId;
    }
    
    
}