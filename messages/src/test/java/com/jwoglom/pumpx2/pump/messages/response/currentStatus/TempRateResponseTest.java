package com.jwoglom.pumpx2.pump.messages.response.currentStatus;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.messages.bluetooth.CharacteristicUUID;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class TempRateResponseTest {
    @Test
    public void testTempRateResponseNotActive() throws DecoderException {
        TempRateResponse expected = new TempRateResponse(
            // boolean active, int percentage, long startTime, long duration
                false, 0, 0, 0
        );

        TempRateResponse parsedRes = (TempRateResponse) MessageTester.test(
                "00032b030a0000000000000000000039c6",
                3,
                1,
                CharacteristicUUID.CURRENT_STATUS_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }

    @Test
    public void testTempRateResponse_values() {
        TempRateResponse r;

        r = new TempRateResponse(
                new byte[]{1,-6,-38,0,-13,31,-124,3,0,0}
        );
        assertTrue(r.getActive());
        assertEquals(900, r.getDuration());
        assertEquals(536019162, r.getStartTimeRaw());
        assertEquals(250, r.getPercentage());

        r = new TempRateResponse(
            new byte[]{1,-11,-99,5,-13,31,-124,3,0,0}
        );
        assertTrue(r.getActive());
        assertEquals(900, r.getDuration());
        assertEquals(536020381, r.getStartTimeRaw());
        assertEquals(245, r.getPercentage());


        r = new TempRateResponse(
                new byte[]{1,-56,57,5,-13,31,8,7,0,0}
        );
        assertTrue(r.getActive());
        assertEquals(1800, r.getDuration());
        assertEquals(536020281, r.getStartTimeRaw());
        assertEquals(200, r.getPercentage());


    }
}