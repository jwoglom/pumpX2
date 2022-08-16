package com.jwoglom.pumpx2.pump.messages.request.currentStatus;

import com.google.common.base.Preconditions;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.ApiVersionDependent;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.models.KnownApiVersion;
import com.jwoglom.pumpx2.pump.messages.response.currentStatus.PumpFeaturesV2Response;

@MessageProps(
    opCode=-96,
    size=1,
    type=MessageType.REQUEST,
    response=PumpFeaturesV2Response.class,
    minApi=KnownApiVersion.API_V2_5
)
@ApiVersionDependent
public class PumpFeaturesV2Request extends Message {
    public PumpFeaturesV2Request(int input) {
        this.cargo = Bytes.firstByteLittleEndian(input);
    }

    public PumpFeaturesV2Request() {
        this(2);
    }

    public void parse(byte[] raw) {
        Preconditions.checkArgument(raw.length == props().size());
        this.cargo = raw;
        
    }

    
}