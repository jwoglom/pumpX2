package com.jwoglom.pumpx2.pump.messages.response.currentStatus;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.bluetooth.Characteristic;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.models.KnownApiVersion;
import com.jwoglom.pumpx2.pump.messages.models.SupportedDevices;

import com.jwoglom.pumpx2.pump.messages.request.currentStatus.UnknownMobiOpcode110Request;

import java.math.BigInteger;

@MessageProps(
    opCode=111,
    size=4,
    type=MessageType.RESPONSE,
    characteristic=Characteristic.CURRENT_STATUS,
    minApi=KnownApiVersion.MOBI_API_V3_5,
    supportedDevices=SupportedDevices.MOBI_ONLY,
    request=UnknownMobiOpcode110Request.class
)
public class UnknownMobiOpcode110Response extends Message {
    
    public UnknownMobiOpcode110Response(byte[] raw) {
        parse(raw);
    }

    public UnknownMobiOpcode110Response() {
    }

    public void parse(byte[] raw) {
        Validate.isTrue(raw.length == props().size());
        this.cargo = raw;
        
    }

    
    
}