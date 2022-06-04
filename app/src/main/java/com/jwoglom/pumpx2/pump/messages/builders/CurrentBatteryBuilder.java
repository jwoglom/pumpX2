package com.jwoglom.pumpx2.pump.messages.builders;

import static com.jwoglom.pumpx2.pump.messages.response.currentStatus.ApiVersionResponse.ApiVersion.V2_API;

import android.content.Context;

import com.jwoglom.pumpx2.pump.PumpState;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.request.currentStatus.CurrentBatteryV1Request;
import com.jwoglom.pumpx2.pump.messages.request.currentStatus.CurrentBatteryV2Request;
import com.jwoglom.pumpx2.pump.messages.response.currentStatus.ApiVersionResponse.ApiVersion;

/**
 * On a <V2_API pump, if a CurrentBatteryV2Request is sent and a CurrentBatteryV1Request
 * has never been sent to the pump, a CurrentBatteryV1Response is returned -- presumably the
 * pump assumes that the client is a "new" client not aware of the legacy behavior. If a
 * CurrentBatteryV1Request is sent after a CurrentBatteryV2Request, then an ErrorResponse
 * is sent with BAD_OPCODE.
 */
public class CurrentBatteryBuilder {
    public static Message create(Context context) {
        if (PumpState.getPumpAPIVersion(context).greaterThanOrEqual(V2_API)) {
            return new CurrentBatteryV2Request();
        } else {
            return new CurrentBatteryV1Request();
        }
    }
}
