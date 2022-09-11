package com.jwoglom.pumpx2.pump.messages.builders;

import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.models.ApiVersion;
import com.jwoglom.pumpx2.pump.messages.models.KnownApiVersion;
import com.jwoglom.pumpx2.pump.messages.request.currentStatus.CurrentBatteryV1Request;
import com.jwoglom.pumpx2.pump.messages.request.currentStatus.CurrentBatteryV2Request;
import com.jwoglom.pumpx2.pump.messages.request.currentStatus.LastBolusStatusRequest;
import com.jwoglom.pumpx2.pump.messages.request.currentStatus.LastBolusStatusV2Request;

public class LastBolusStatusRequestBuilder {
    public static Message create(ApiVersion apiVersion) {
        if (apiVersion.greaterThan(KnownApiVersion.API_V2_1)) {
            return new LastBolusStatusV2Request();
        } else {
            return new LastBolusStatusRequest();
        }
    }
}
