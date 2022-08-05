package com.jwoglom.pumpx2.pump.messages.builders;

import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.models.ApiVersion;
import com.jwoglom.pumpx2.pump.messages.models.KnownApiVersion;
import com.jwoglom.pumpx2.pump.messages.request.currentStatus.PumpFeaturesV1Request;
import com.jwoglom.pumpx2.pump.messages.request.currentStatus.PumpFeaturesV2Request;

/**
 * On an earlier than V2_API pump, PumpFeaturesV2Request returns a bad opcode ErrorResponse
 */
public class PumpFeaturesBuilder {
    public static Message create(ApiVersion apiVersion) {
        if (apiVersion.greaterThan(KnownApiVersion.API_V2_1)) {
            return new PumpFeaturesV2Request();
        } else {
            return new PumpFeaturesV1Request();
        }
    }
}
