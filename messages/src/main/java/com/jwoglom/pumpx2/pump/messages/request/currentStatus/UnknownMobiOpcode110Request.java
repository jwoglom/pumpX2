package com.jwoglom.pumpx2.pump.messages.request.currentStatus;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.bluetooth.Characteristic;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.models.KnownApiVersion;
import com.jwoglom.pumpx2.pump.messages.models.SupportedDevices;
import com.jwoglom.pumpx2.pump.messages.response.currentStatus.UnknownMobiOpcode110Response;

@MessageProps(
    opCode=110,
    size=0,
    type=MessageType.REQUEST,
    characteristic=Characteristic.CURRENT_STATUS,
    minApi=KnownApiVersion.MOBI_API_V3_5,
    supportedDevices=SupportedDevices.MOBI_ONLY,
    response=UnknownMobiOpcode110Response.class
)
public class UnknownMobiOpcode110Request extends Message { 
    public UnknownMobiOpcode110Request() {
        this.cargo = EMPTY;
    }

    public void parse(byte[] raw) {
        Validate.isTrue(raw.length == props().size());
        this.cargo = raw;
        
    }

    
}