package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;
import static org.junit.Assert.assertEquals;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class CgmCalibrationGxHistoryLogTest {
    @Test
    public void testCgmCalibrationGxHistoryLog_roundTrip() throws DecoderException {
        // value chosen > 0xFFFF to confirm the field is read as a 4-byte uint32,
        // not a 2-byte short (which would truncate it). Matches Mobi's
        // CgmCalibrationModel and pumpX2's CgmCalibrationG7HistoryLog (typeId 438).
        long value = 0x00012345L; // 74565

        CgmCalibrationGxHistoryLog expected = new CgmCalibrationGxHistoryLog(1000L, 5L, value);

        CgmCalibrationGxHistoryLog parsedRes = new CgmCalibrationGxHistoryLog();
        parsedRes.parse(expected.getCargo());

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
        assertEquals(26, parsedRes.getCargo().length);
        assertEquals(value, parsedRes.getValue());
        assertEquals(1000L, parsedRes.getPumpTimeSec());
        assertEquals(5L, parsedRes.getSequenceNum());
    }
}
