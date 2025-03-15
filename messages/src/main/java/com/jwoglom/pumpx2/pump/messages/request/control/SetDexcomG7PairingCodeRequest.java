package com.jwoglom.pumpx2.pump.messages.request.control;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.bluetooth.Characteristic;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.response.control.SetDexcomG7PairingCodeResponse;

@MessageProps(
    opCode=-4,
    size=8, // +24
    type=MessageType.REQUEST,
    characteristic=Characteristic.CONTROL,
    signed=true,
    response= SetDexcomG7PairingCodeResponse.class
)
public class SetDexcomG7PairingCodeRequest extends Message {
    private int pairingCode;
    
    public SetDexcomG7PairingCodeRequest() {}

    public SetDexcomG7PairingCodeRequest(int pairingCode) {
        this.cargo = buildCargo(pairingCode);
        this.pairingCode = pairingCode;
        
    }

    public void parse(byte[] raw) { 
        raw = this.removeSignedRequestHmacBytes(raw);
        Validate.isTrue(raw.length == props().size());
        this.cargo = raw;
        this.pairingCode = Bytes.readShort(raw, 0);
        
    }

    
    public static byte[] buildCargo(int pairingCode) {
        return Bytes.combine(
            Bytes.firstTwoBytesLittleEndian(pairingCode),
            new byte[6]
        );
    }
    public int getPairingCode() {
        return pairingCode;
    }
    
    
}