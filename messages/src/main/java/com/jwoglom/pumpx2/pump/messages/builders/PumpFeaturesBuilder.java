package com.jwoglom.pumpx2.pump.messages.builders;

import static com.jwoglom.pumpx2.pump.messages.response.currentStatus.ApiVersionResponse.ApiVersion.V21;

import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.request.currentStatus.PumpFeaturesV1Request;
import com.jwoglom.pumpx2.pump.messages.request.currentStatus.PumpFeaturesV2Request;
import com.jwoglom.pumpx2.pump.messages.response.currentStatus.ApiVersionResponse;

/**
 * On a <V2_API pump, PumpFeaturesV2Request returns a bad opcode ErrorResponse
 */
public class PumpFeaturesBuilder {
    public static Message create(ApiVersionResponse.ApiVersion apiVersion) {
        if (apiVersion.greaterThan(V21)) {
            return new PumpFeaturesV2Request();
        } else {
            return new PumpFeaturesV1Request();
        }
    }
}
