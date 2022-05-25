package com.jwoglom.pumpx2.pump.messages.request;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.bluetooth.CharacteristicUUID;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class PumpGlobalsRequestTest {
    @Test
    public void testPumpGlobalsRequest() throws DecoderException {
        // empty cargo
        PumpGlobalsRequest expected = new PumpGlobalsRequest();

        PumpGlobalsRequest parsedReq = (PumpGlobalsRequest) MessageTester.test(
                "0003560300a175",
                3,
                1,
                CharacteristicUUID.CURRENT_STATUS_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedReq.getCargo());
    }
}
