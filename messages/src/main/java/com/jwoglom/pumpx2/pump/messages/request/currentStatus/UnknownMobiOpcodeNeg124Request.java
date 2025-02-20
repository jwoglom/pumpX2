package com.jwoglom.pumpx2.pump.messages.request.currentStatus;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.bluetooth.Characteristic;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.models.KnownApiVersion;
import com.jwoglom.pumpx2.pump.messages.models.SupportedDevices;
import com.jwoglom.pumpx2.pump.messages.response.currentStatus.UnknownMobiOpcodeNeg124Response;

@MessageProps(
    opCode=-124,
    size=0, // 24 bytes with trailer
    type=MessageType.REQUEST,
    signed=true,
    characteristic=Characteristic.CONTROL,
    minApi=KnownApiVersion.MOBI_API_V3_5,
    supportedDevices=SupportedDevices.MOBI_ONLY,
    response=UnknownMobiOpcodeNeg124Response.class
)
public class UnknownMobiOpcodeNeg124Request extends Message {
    public UnknownMobiOpcodeNeg124Request(byte[] raw) {
        parse(raw);
    }

    public UnknownMobiOpcodeNeg124Request() {
        this.cargo = EMPTY;
    }

    public void parse(byte[] raw) {
        raw = removeSignedRequestHmacBytes(raw);
        Validate.isTrue(raw.length == props().size());
        this.cargo = raw;
        
    }

    
}