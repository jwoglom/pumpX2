package com.jwoglom.pumpx2.pump.messages.response.currentStatus;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.bluetooth.Characteristic;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.models.StatusMessage;
import com.jwoglom.pumpx2.pump.messages.request.currentStatus.UnknownMobiOpcode20Request;

import java.math.BigInteger;

@MessageProps(
    opCode=21,
    size=3,
    type=MessageType.RESPONSE,
    characteristic=Characteristic.CURRENT_STATUS,
    request=UnknownMobiOpcode20Request.class
)
public class UnknownMobiOpcode20Response extends StatusMessage {
    private int status;
    private int unknown;
    
    public UnknownMobiOpcode20Response() {
        this.cargo = EMPTY;
        
    }
    public UnknownMobiOpcode20Response(byte[] raw) {
        this.cargo = raw;
        parse(raw);

    }
    public UnknownMobiOpcode20Response(int status, int unknown) {
        this.cargo = buildCargo(status, unknown);
        parse(cargo);

    }

    public void parse(byte[] raw) { 
        Validate.isTrue(raw.length == props().size());
        this.cargo = raw;
        this.status = raw[0];
        this.unknown = Bytes.readShort(raw, 1);
    }

    public static byte[] buildCargo(int status, int unknown) {
        return Bytes.combine(
                new byte[]{ (byte) status },
                Bytes.firstTwoBytesLittleEndian(unknown));
    }

    public int getStatus() {
        return status;
    }


    public int getUnknown() {
        return unknown;
    }
}