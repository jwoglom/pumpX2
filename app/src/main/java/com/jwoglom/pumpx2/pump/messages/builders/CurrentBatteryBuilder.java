package com.jwoglom.pumpx2.pump.messages.builders;

import android.content.Context;

import com.jwoglom.pumpx2.pump.PumpState;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.request.CurrentBatteryV1Request;
import com.jwoglom.pumpx2.pump.messages.request.CurrentBatteryV2Request;
import com.jwoglom.pumpx2.pump.messages.response.ApiVersionResponse.ApiVersion;

public class CurrentBatteryBuilder {
    private static final ApiVersion V2_API = new ApiVersion(2, 2);
    public static Message create(Context context) {
        if (PumpState.getPumpAPIVersion(context).greaterThanOrEqual(V2_API)) {
            return new CurrentBatteryV2Request();
        } else {
            return new CurrentBatteryV1Request();
        }
    }
}
