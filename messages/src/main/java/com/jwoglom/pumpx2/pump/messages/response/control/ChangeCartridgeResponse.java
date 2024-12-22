package com.jwoglom.pumpx2.pump.messages.response.control;

import com.google.common.base.Preconditions;
import com.jwoglom.pumpx2.pump.messages.bluetooth.Characteristic;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.request.control.ChangeCartridgeRequest;

import java.math.BigInteger;

@MessageProps(
    opCode=-111,
    size=11,
    type=MessageType.RESPONSE,
    characteristic=Characteristic.CONTROL,
    signed=false, // NOT signed (?)
    modifiesInsulinDelivery=true,
    request=ChangeCartridgeRequest.class
)
public class ChangeCartridgeResponse extends Message {
    
    private int status;
    private int unknown1;
    
    public ChangeCartridgeResponse() {}


    public ChangeCartridgeResponse(byte[] raw) {
        this.cargo = raw;
        parse(raw);

    }
    
    public ChangeCartridgeResponse(int status, int unknown1) {
        this.cargo = buildCargo(status, unknown1);
        this.status = status;
        this.unknown1 = unknown1;
        
    }

    public void parse(byte[] raw) {
        Preconditions.checkArgument(raw.length == props().size());
        this.cargo = raw;
        this.status = raw[0];
        this.unknown1 = raw[1];
        
    }

    
    public static byte[] buildCargo(int status, int unknown1) {
        return Bytes.combine(
            new byte[]{ (byte) status },
            new byte[]{ (byte) unknown1 },
            new byte[]{ 0, 0, 0, 0, 0, 0, 0, 0, 0}
        );
    }
    
    public int getStatus() {
        return status;
    }
    
}