package com.jwoglom.pumpx2.pump.messages.request.currentStatus;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.messages.bluetooth.CharacteristicUUID;
import com.jwoglom.pumpx2.pump.messages.request.currentStatus.LastBolusStatusRequest;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class LastBolusStatusRequestTest {
    @Test
    public void testLastBolusStatusRequest() throws DecoderException {
        // empty cargo
        LastBolusStatusRequest expected = new LastBolusStatusRequest();

        LastBolusStatusRequest parsedReq = (LastBolusStatusRequest) MessageTester.test(
                "xxxx",
                3,
                1,
                CharacteristicUUID.CURRENT_STATUS_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedReq.getCargo());
    }
}