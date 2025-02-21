package com.jwoglom.pumpx2.pump.messages.request.currentStatus;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.annotations.ApiVersionDependent;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.models.KnownApiVersion;
import com.jwoglom.pumpx2.pump.messages.response.currentStatus.LastBolusStatusV2Response;

/**
 * See {@link com.jwoglom.pumpx2.pump.messages.builders.LastBolusStatusRequestBuilder}
 */
@MessageProps(
    opCode=-92,
    size=0,
    type=MessageType.REQUEST,
    response=LastBolusStatusV2Response.class,
    minApi=KnownApiVersion.API_V2_5
)
@ApiVersionDependent
public class LastBolusStatusV2Request extends Message { 
    public LastBolusStatusV2Request() {
        this.cargo = EMPTY;
    }

    public void parse(byte[] raw) {
        Validate.isTrue(raw.length == props().size());
        this.cargo = raw;
    }
}