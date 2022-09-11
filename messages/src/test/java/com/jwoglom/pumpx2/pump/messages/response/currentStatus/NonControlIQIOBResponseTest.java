package com.jwoglom.pumpx2.pump.messages.response.currentStatus;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;
import static org.junit.Assert.assertEquals;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.messages.bluetooth.CharacteristicUUID;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class NonControlIQIOBResponseTest {
    @Test
    public void testIOBResponse() throws DecoderException {
        //  NonControlIQIOBResponse[iob=195,timeRemaining=14640,totalIOB=241,cargo={-61,0,0,0,48,57,0,0,-15,0,0,0}]
        NonControlIQIOBResponse expected = new NonControlIQIOBResponse(195, 14640, 241);

        NonControlIQIOBResponse parsedRes = (NonControlIQIOBResponse) MessageTester.test(
                "000427040cc300000030390000f100000008f7",
                4,
                1,
                CharacteristicUUID.CURRENT_STATUS_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }
}
