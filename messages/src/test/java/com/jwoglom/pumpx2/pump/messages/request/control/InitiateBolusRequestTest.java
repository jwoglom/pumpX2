package com.jwoglom.pumpx2.pump.messages.request.control;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.messages.bluetooth.CharacteristicUUID;
import com.jwoglom.pumpx2.pump.messages.request.control.InitiateBolusRequest;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class InitiateBolusRequestTest {
    @Test
    public void testInitiateBolusRequest() throws DecoderException {
        // empty cargo
        InitiateBolusRequest expected = new InitiateBolusRequest();

        InitiateBolusRequest parsedReq = (InitiateBolusRequest) MessageTester.test(
                "xxxx",
                3,
                1,
                CharacteristicUUID.CURRENT_STATUS_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedReq.getCargo());
    }
}