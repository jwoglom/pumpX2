package com.jwoglom.pumpx2.pump.messages.response.currentStatus;

import com.google.common.base.Preconditions;
import com.jwoglom.pumpx2.pump.messages.bluetooth.Characteristic;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.models.KnownApiVersion;
import com.jwoglom.pumpx2.pump.messages.models.SupportedDevices;
import com.jwoglom.pumpx2.pump.messages.request.currentStatus.UnknownMobiOpcodeNeg69Request;

import java.math.BigInteger;

@MessageProps(
    opCode=-69,
    size=53,
    type=MessageType.RESPONSE,
    characteristic=Characteristic.CURRENT_STATUS,
    minApi=KnownApiVersion.MOBI_API_V3_5,
    supportedDevices=SupportedDevices.MOBI_ONLY,
    request=UnknownMobiOpcodeNeg69Request.class
)
public class UnknownMobiOpcodeNeg69Response extends Message {
    
    public UnknownMobiOpcodeNeg69Response(byte[] raw) {
        parse(raw);
    }

    public UnknownMobiOpcodeNeg69Response() {
    }


    public void parse(byte[] raw) {
        Preconditions.checkArgument(raw.length == props().size());
        this.cargo = raw;
        
    }

    
    
}