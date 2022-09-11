package com.jwoglom.pumpx2.pump.messages.response.control;

import com.google.common.base.Preconditions;
import com.jwoglom.pumpx2.pump.messages.bluetooth.Characteristic;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.models.KnownApiVersion;
import com.jwoglom.pumpx2.pump.messages.request.control.OpcodeNegative16Request;

import java.math.BigInteger;

@MessageProps(
    opCode=-15,
    size=1,
    type=MessageType.RESPONSE,
    characteristic=Characteristic.CONTROL,
    request=OpcodeNegative16Request.class,
    signed=true,
    minApi=KnownApiVersion.API_V2_5
)
public class OpcodeNegative16Response extends Message {
    private int status;
    
    public OpcodeNegative16Response() {}

    public OpcodeNegative16Response(int status) {
        this.cargo = buildCargo(status);
        this.status = status;
    }

    public static byte[] buildCargo(int status) {
        return Bytes.combine(
                Bytes.firstByteLittleEndian(status)
        );
    }

    public void parse(byte[] raw) {
        raw = removeSignedRequestHmacBytes(raw);
        Preconditions.checkArgument(raw.length == props().size(), "length: " + raw.length);
        this.cargo = raw;
    }

    public int getStatus() {
        return status;
    }
}