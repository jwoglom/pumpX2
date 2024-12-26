package com.jwoglom.pumpx2.pump.messages.response.control;

import com.google.common.base.Preconditions;
import com.jwoglom.pumpx2.pump.messages.bluetooth.Characteristic;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.models.KnownApiVersion;
import com.jwoglom.pumpx2.pump.messages.request.control.SetG6TransmitterIdRequest;

import java.math.BigInteger;

@MessageProps(
    opCode=-79,
    size=1,
    type=MessageType.RESPONSE,
    characteristic=Characteristic.CONTROL,
    signed=true,
    request=SetG6TransmitterIdRequest.class,
    minApi=KnownApiVersion.MOBI_API_V3_5
)
public class SetG6TransmitterIdResponse extends Message {
    private int status;

    public SetG6TransmitterIdResponse() {
        this.cargo = EMPTY;
        
    }

    public SetG6TransmitterIdResponse(int status) {
        this.cargo = buildCargo(status);
        this.status = status;

    }

    public void parse(byte[] raw) {
        raw = this.removeSignedRequestHmacBytes(raw);
        Preconditions.checkArgument(raw.length == props().size());
        this.cargo = raw;
        this.status = raw[0];

    }

    public static byte[] buildCargo(int status) {
        return Bytes.combine(
                new byte[]{ (byte) status });
    }

    public int getStatus() {
        return status;
    }
}