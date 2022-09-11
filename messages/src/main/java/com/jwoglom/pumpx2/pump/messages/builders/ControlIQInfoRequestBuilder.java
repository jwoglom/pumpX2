package com.jwoglom.pumpx2.pump.messages.builders;

import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.models.ApiVersion;
import com.jwoglom.pumpx2.pump.messages.models.KnownApiVersion;
import com.jwoglom.pumpx2.pump.messages.request.currentStatus.ControlIQInfoV1Request;
import com.jwoglom.pumpx2.pump.messages.request.currentStatus.ControlIQInfoV2Request;

public class ControlIQInfoRequestBuilder {
    public static Message create(ApiVersion apiVersion) {
        if (apiVersion.greaterThan(KnownApiVersion.API_V2_1)) {
            return new ControlIQInfoV2Request();
        } else {
            return new ControlIQInfoV1Request();
        }
    }
}
