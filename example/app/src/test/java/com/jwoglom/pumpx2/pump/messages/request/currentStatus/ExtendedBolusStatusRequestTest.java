package com.jwoglom.pumpx2.pump.messages.request.currentStatus;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.bluetooth.CharacteristicUUID;
import com.jwoglom.pumpx2.pump.messages.request.currentStatus.ExtendedBolusStatusRequest;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class ExtendedBolusStatusRequestTest {
    @Test
    public void testExtendedBolusStatusRequest() throws DecoderException {
        // empty cargo
        ExtendedBolusStatusRequest expected = new ExtendedBolusStatusRequest();

        ExtendedBolusStatusRequest parsedReq = (ExtendedBolusStatusRequest) MessageTester.test(
                "00062e0600fdfb",
                6,
                1,
                CharacteristicUUID.CURRENT_STATUS_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedReq.getCargo());
    }
}