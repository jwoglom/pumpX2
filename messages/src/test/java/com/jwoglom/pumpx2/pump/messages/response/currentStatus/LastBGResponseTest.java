package com.jwoglom.pumpx2.pump.messages.response.currentStatus;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;

import static org.junit.Assert.assertEquals;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.messages.bluetooth.CharacteristicUUID;
import com.jwoglom.pumpx2.pump.messages.helpers.Dates;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

import java.time.Instant;

public class LastBGResponseTest {
    @Test
    public void testLastBGResponseEmpty() throws DecoderException {
        LastBGResponse expected = new LastBGResponse(
            // long bgTimestamp, int bgValue, int bgSource
            0L, 0, 0
        );

        LastBGResponse parsedRes = (LastBGResponse) MessageTester.test(
                "0003330307000000000000003117",
                3,
                1,
                CharacteristicUUID.CURRENT_STATUS_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }

    @Test
    public void testLastBGResponse_cgm() throws DecoderException {
        LastBGResponse expected = new LastBGResponse(
                // long bgTimestamp, int bgValue, int bgSource
                458530870L, 206, 1
        );

        LastBGResponse parsedRes = (LastBGResponse) MessageTester.test(
                "000e330e0736a0541bce0001d9e4",
                14,
                1,
                CharacteristicUUID.CURRENT_STATUS_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
        assertEquals(Instant.parse("2022-07-13T01:41:10Z"), parsedRes.getBgTimestampInstant());
        assertEquals(LastBGResponse.BgSource.CGM, expected.getBgSource());
    }


    @Test
    public void testLastBGResponse_cgmCalibration() throws DecoderException {
        LastBGResponse expected = new LastBGResponse(
                // long bgTimestamp, int bgValue, int bgSource
                542807828L, 169, 0
        );

        LastBGResponse parsedRes = (LastBGResponse) MessageTester.test(
                "00d733d70714975a20a90000cc8f",
                -41,
                1,
                CharacteristicUUID.CURRENT_STATUS_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
        assertEquals(Instant.parse("2025-03-14T11:57:08Z"), parsedRes.getBgTimestampInstant());
        assertEquals(LastBGResponse.BgSource.MANUAL, expected.getBgSource());
    }
}