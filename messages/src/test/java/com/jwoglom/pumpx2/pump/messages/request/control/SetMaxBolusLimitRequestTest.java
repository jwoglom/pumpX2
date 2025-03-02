package com.jwoglom.pumpx2.pump.messages.request.control;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;
import static com.jwoglom.pumpx2.pump.messages.MessageTester.initPumpState;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.messages.PacketArrayList;
import com.jwoglom.pumpx2.pump.messages.bluetooth.CharacteristicUUID;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class SetMaxBolusLimitRequestTest {
    @Test
    public void testSetDeliveryLimitsRequest_20u() throws DecoderException {
        initPumpState(PacketArrayList.IGNORE_INVALID_HMAC, 1L);

        SetMaxBolusLimitRequest expected = new SetMaxBolusLimitRequest(20_000);

        SetMaxBolusLimitRequest parsedReq = (SetMaxBolusLimitRequest) MessageTester.test(
                "016186611a204e92e14920c2f4aee8ca1b7d1960",
                97,
                1,
                CharacteristicUUID.CONTROL_CHARACTERISTICS,
                expected,
                "006175ffd49542eeb8c009ed2022f2"
        );

        assertHexEquals(expected.getCargo(), parsedReq.getCargo());
    }
}