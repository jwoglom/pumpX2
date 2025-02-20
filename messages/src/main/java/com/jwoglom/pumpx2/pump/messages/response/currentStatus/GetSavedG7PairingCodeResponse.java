package com.jwoglom.pumpx2.pump.messages.response.currentStatus;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.bluetooth.Characteristic;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.request.currentStatus.GetSavedG7PairingCodeRequest;

@MessageProps(
    opCode=117,
    size=2,
    type=MessageType.RESPONSE,
    characteristic=Characteristic.CURRENT_STATUS,
    request=GetSavedG7PairingCodeRequest.class
)
public class GetSavedG7PairingCodeResponse extends Message {
    
    private int pairingCode;
    
    public GetSavedG7PairingCodeResponse() {}
    
    public GetSavedG7PairingCodeResponse(int pairingCode) {
        this.cargo = buildCargo(pairingCode);
        this.pairingCode = pairingCode;
        
    }

    public void parse(byte[] raw) { 
        Validate.isTrue(raw.length == props().size());
        this.cargo = raw;
        this.pairingCode = Bytes.readShort(raw, 0);
        
    }

    
    public static byte[] buildCargo(int pairingCode) {
        return Bytes.combine(
            Bytes.firstTwoBytesLittleEndian(pairingCode)
        );
    }
    
    public int getPairingCode() {
        return pairingCode;
    }
    
}