package com.jwoglom.pumpx2.pump.messages.request.currentStatus;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.bluetooth.CharacteristicUUID;
import com.jwoglom.pumpx2.pump.messages.request.currentStatus.ControlIQSleepScheduleRequest;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class ControlIQSleepScheduleRequestTest {
    @Test
    public void testControlIQSleepScheduleRequest() throws DecoderException {
        // empty cargo
        ControlIQSleepScheduleRequest expected = new ControlIQSleepScheduleRequest();

        ControlIQSleepScheduleRequest parsedReq = (ControlIQSleepScheduleRequest) MessageTester.test(
                "00036a030065c5",
                3,
                1,
                CharacteristicUUID.CURRENT_STATUS_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedReq.getCargo());
    }
}