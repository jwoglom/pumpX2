package com.jwoglom.pumpx2.pump.messages.request.currentStatus;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.bluetooth.CharacteristicUUID;
import com.jwoglom.pumpx2.pump.messages.request.currentStatus.LastBGRequest;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class LastBGRequestTest {
    @Test
    public void testLastBGRequest() throws DecoderException {
        // empty cargo
        LastBGRequest expected = new LastBGRequest();

        LastBGRequest parsedReq = (LastBGRequest) MessageTester.test(
                "00033203000a32",
                3,
                1,
                CharacteristicUUID.CURRENT_STATUS_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedReq.getCargo());
    }
}