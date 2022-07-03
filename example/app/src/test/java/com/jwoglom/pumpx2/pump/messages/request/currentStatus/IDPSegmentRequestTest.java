package com.jwoglom.pumpx2.pump.messages.request.currentStatus;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.bluetooth.CharacteristicUUID;
import com.jwoglom.pumpx2.pump.messages.request.currentStatus.IDPSegmentRequest;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class IDPSegmentRequestTest {
    @Test
    public void testIDPSegmentRequest() throws DecoderException {
        // empty cargo
        IDPSegmentRequest expected = new IDPSegmentRequest(0, 0);

        IDPSegmentRequest parsedReq = (IDPSegmentRequest) MessageTester.test(
                "0004420402000076e0",
                4,
                1,
                CharacteristicUUID.CURRENT_STATUS_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedReq.getCargo());
    }
}