package com.jwoglom.pumpx2.pump.messages.builders;

import static com.jwoglom.pumpx2.pump.messages.response.currentStatus.ApiVersionResponse.ApiVersion.V2_API;

import android.content.Context;

import com.jwoglom.pumpx2.pump.PumpState;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.request.currentStatus.PumpFeaturesV1Request;
import com.jwoglom.pumpx2.pump.messages.request.currentStatus.PumpFeaturesV2Request;

/**
 * On a <V2_API pump, PumpFeaturesV2Request returns a bad opcode ErrorResponse
 */
public class PumpFeaturesBuilder {
    public static Message create(Context context) {
        if (PumpState.getPumpAPIVersion(context).greaterThanOrEqual(V2_API)) {
            return new PumpFeaturesV2Request();
        } else {
            return new PumpFeaturesV1Request();
        }
    }
}
