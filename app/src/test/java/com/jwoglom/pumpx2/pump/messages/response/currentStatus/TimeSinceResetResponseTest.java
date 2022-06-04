package com.jwoglom.pumpx2.pump.messages.response.currentStatus;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;

import static org.junit.Assert.assertEquals;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.bluetooth.CharacteristicUUID;
import com.jwoglom.pumpx2.pump.messages.response.currentStatus.TimeSinceResetResponse;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

import java.time.Instant;
import java.util.Date;

public class TimeSinceResetResponseTest {
    @Test
    public void testTimeSinceResetResponse1() throws DecoderException {
        // Pump time: Feb 28, 2022 5:50:49pm EST
        TimeSinceResetResponse expected = new TimeSinceResetResponse(
            // long timeSinceReset, long pumpTime
                446925049L, 10601L
        );

        TimeSinceResetResponse parsedRes = (TimeSinceResetResponse) MessageTester.test(
                "0003370308f988a31a69290000db69",
                3,
                1,
                CharacteristicUUID.CURRENT_STATUS_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
        assertEquals(Instant.parse("2022-02-28T17:50:49.00Z"), parsedRes.getTimeSinceReset());
    }

    @Test
    public void testTimeSinceResetResponse2() throws DecoderException {
        TimeSinceResetResponse expected = new TimeSinceResetResponse(
                // long timeSinceReset, long pumpTime
                // diff: 388 seconds
                446925437L, 10989L
        );

        TimeSinceResetResponse parsedRes = (TimeSinceResetResponse) MessageTester.test(
                "00043704087d8aa31aed2a0000b7a9",
                4,
                1,
                CharacteristicUUID.CURRENT_STATUS_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }
}