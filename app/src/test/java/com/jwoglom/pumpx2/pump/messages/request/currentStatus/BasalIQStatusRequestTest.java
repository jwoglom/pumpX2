package com.jwoglom.pumpx2.pump.messages.request.currentStatus;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.bluetooth.CharacteristicUUID;
import com.jwoglom.pumpx2.pump.messages.request.currentStatus.BasalIQStatusRequest;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class BasalIQStatusRequestTest {
    @Test
    public void testBasalIQStatusRequest() throws DecoderException {
        // empty cargo
        BasalIQStatusRequest expected = new BasalIQStatusRequest();

        BasalIQStatusRequest parsedReq = (BasalIQStatusRequest) MessageTester.test(
                "000570050061eb",
                5,
                1,
                CharacteristicUUID.CURRENT_STATUS_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedReq.getCargo());
    }
}