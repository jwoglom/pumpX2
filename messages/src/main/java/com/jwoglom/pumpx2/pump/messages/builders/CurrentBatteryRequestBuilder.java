package com.jwoglom.pumpx2.pump.messages.builders;

import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.models.ApiVersion;
import com.jwoglom.pumpx2.pump.messages.models.KnownApiVersion;
import com.jwoglom.pumpx2.pump.messages.request.currentStatus.CurrentBatteryV1Request;
import com.jwoglom.pumpx2.pump.messages.request.currentStatus.CurrentBatteryV2Request;

/**
 * On an earlier than V2_API pump, if CurrentBatteryV2Request is sent and a CurrentBatteryV1Request
 * has never been sent to the pump, a CurrentBatteryV1Response is returned -- presumably the
 * pump assumes that the client is a "new" client not aware of the legacy behavior. If a
 * CurrentBatteryV1Request is sent after a CurrentBatteryV2Request, then an ErrorResponse
 * is sent with BAD_OPCODE.
 */
public class CurrentBatteryRequestBuilder {
    public static Message create(ApiVersion apiVersion) {
        if (apiVersion.greaterThan(KnownApiVersion.API_V2_1)) {
            return new CurrentBatteryV2Request();
        } else {
            return new CurrentBatteryV1Request();
        }
    }
}
