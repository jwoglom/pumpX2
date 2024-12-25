package com.jwoglom.pumpx2.pump.messages.request.currentStatus;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.messages.bluetooth.CharacteristicUUID;
import com.jwoglom.pumpx2.pump.messages.request.currentStatus.OtherNotificationStatusRequest;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class OtherNotificationStatusRequestTest {
    @Test
    public void testOtherNotificationStatusRequest() throws DecoderException {
        OtherNotificationStatusRequest expected = new OtherNotificationStatusRequest(new byte[]{2});

        OtherNotificationStatusRequest parsedReq = (OtherNotificationStatusRequest) MessageTester.test(
                "0018921801028656",
                24,
                1,
                CharacteristicUUID.CURRENT_STATUS_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedReq.getCargo());
    }
}