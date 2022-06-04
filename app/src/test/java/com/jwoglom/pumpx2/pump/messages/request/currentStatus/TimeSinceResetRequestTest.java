package com.jwoglom.pumpx2.pump.messages.request.currentStatus;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.bluetooth.CharacteristicUUID;
import com.jwoglom.pumpx2.pump.messages.request.currentStatus.TimeSinceResetRequest;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class TimeSinceResetRequestTest {
    @Test
    public void testTimeSinceResetRequest() throws DecoderException {
        // empty cargo
        TimeSinceResetRequest expected = new TimeSinceResetRequest();

        TimeSinceResetRequest parsedReq = (TimeSinceResetRequest) MessageTester.test(
                "0003360300caee",
                3,
                1,
                CharacteristicUUID.CURRENT_STATUS_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedReq.getCargo());
    }
}