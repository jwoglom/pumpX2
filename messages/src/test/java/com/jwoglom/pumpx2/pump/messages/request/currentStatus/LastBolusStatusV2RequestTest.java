package com.jwoglom.pumpx2.pump.messages.request.currentStatus;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.messages.bluetooth.CharacteristicUUID;
import com.jwoglom.pumpx2.pump.messages.request.currentStatus.LastBolusStatusV2Request;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class LastBolusStatusV2RequestTest {
    @Test
    public void testLastBolusStatusV2Request() throws DecoderException {
        // empty cargo
        LastBolusStatusV2Request expected = new LastBolusStatusV2Request();

        LastBolusStatusV2Request parsedReq = (LastBolusStatusV2Request) MessageTester.test(
                "0004a404000461",
                4,
                1,
                CharacteristicUUID.CURRENT_STATUS_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedReq.getCargo());
    }
}