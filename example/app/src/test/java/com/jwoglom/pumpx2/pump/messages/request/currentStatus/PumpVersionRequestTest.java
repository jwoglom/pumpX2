package com.jwoglom.pumpx2.pump.messages.request.currentStatus;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.bluetooth.CharacteristicUUID;
import com.jwoglom.pumpx2.pump.messages.request.currentStatus.PumpVersionRequest;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class PumpVersionRequestTest {
    @Test
    public void testPumpVersionRequest() throws DecoderException {
        // empty cargo
        PumpVersionRequest expected = new PumpVersionRequest();

        PumpVersionRequest parsedReq = (PumpVersionRequest) MessageTester.test(
                "0003540300c11b",
                3,
                1,
                CharacteristicUUID.CURRENT_STATUS_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedReq.getCargo());
    }
}