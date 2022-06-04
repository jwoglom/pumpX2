package com.jwoglom.pumpx2.pump.messages.response.currentStatus;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.bluetooth.CharacteristicUUID;
import com.jwoglom.pumpx2.pump.messages.response.currentStatus.InsulinStatusResponse;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class InsulinStatusResponseTest {
    @Test
    public void testInsulinStatusResponse() throws DecoderException {
        InsulinStatusResponse expected = new InsulinStatusResponse(
            // int currentInsulinAmount, int isEstimate, int insulinLowAmount
            0, 0, 35
        );

        InsulinStatusResponse parsedRes = (InsulinStatusResponse) MessageTester.test(
                "000425040400000023397d",
                4,
                1,
                CharacteristicUUID.CURRENT_STATUS_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }
}