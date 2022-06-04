package com.jwoglom.pumpx2.pump.messages.request.currentStatus;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.bluetooth.CharacteristicUUID;
import com.jwoglom.pumpx2.pump.messages.request.currentStatus.TempRateRequest;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class TempRateRequestTest {
    @Test
    public void testTempRateRequest() throws DecoderException {
        // empty cargo
        TempRateRequest expected = new TempRateRequest();

        TempRateRequest parsedReq = (TempRateRequest) MessageTester.test(
                "00032a0300c8d8",
                3,
                1,
                CharacteristicUUID.CURRENT_STATUS_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedReq.getCargo());
    }
}