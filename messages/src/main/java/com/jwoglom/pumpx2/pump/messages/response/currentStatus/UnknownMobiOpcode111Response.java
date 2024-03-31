package com.jwoglom.pumpx2.pump.messages.response.currentStatus;

import com.google.common.base.Preconditions;
import com.jwoglom.pumpx2.pump.messages.bluetooth.Characteristic;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.models.KnownApiVersion;
import com.jwoglom.pumpx2.pump.messages.models.SupportedDevices;

import com.jwoglom.pumpx2.pump.messages.request.currentStatus.UnknownMobiOpcode111Request;

import java.math.BigInteger;

@MessageProps(
    opCode=111,
    size=4,
    type=MessageType.RESPONSE,
    characteristic=Characteristic.CURRENT_STATUS,
    minApi=KnownApiVersion.MOBI_API_V3_5,
    supportedDevices=SupportedDevices.MOBI_ONLY,
    request=UnknownMobiOpcode111Request.class
)
public class UnknownMobiOpcode111Response extends Message {
    
    public UnknownMobiOpcode111Response(byte[] raw) {
        parse(raw);
    }

    public UnknownMobiOpcode111Response() {
    }

    public void parse(byte[] raw) {
        Preconditions.checkArgument(raw.length == props().size());
        this.cargo = raw;
        
    }

    
    
}