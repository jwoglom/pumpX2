package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.jwoglom.pumpx2.pump.messages.response.currentStatus.LastBGResponse;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

import java.time.Instant;

public class BGHistoryLogTest {
    @Test
    public void testBGHistoryLog() throws DecoderException {
        BGHistoryLog expected = new BGHistoryLog(
            // int bg, int cgmCalibration, int bgSource, float iob, int targetBG, int isf, int selectedIOB, int bgSourceType, int spare
                446061064L,
                183533L,
                162, 0, 1, 10.91F, 110, 30, 1, 0, 0
        );

        BGHistoryLog parsedRes = (BGHistoryLog) HistoryLogMessageTester.testSingle(
                "1000085a961aedcc0200a20000015c8f2e416e001e0001000000",
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
        assertEquals(Instant.parse("2022-02-18T17:51:04Z"), parsedRes.getPumpTimeSecInstant());
        assertEquals(162, parsedRes.getBg());
        assertEquals(0, parsedRes.getCgmCalibration());
        assertFalse(parsedRes.isUsedForCgmCalibration());
        assertEquals(LastBGResponse.BgSource.CGM, parsedRes.getBgSource());
        assertEquals(110, parsedRes.getTargetBG());
        assertEquals(30, parsedRes.getIsf());
        assertEquals(BolusRequestedMsg2HistoryLog.SelectedIOBType.SWAN_IOB_MEAL, parsedRes.getSelectedIOBType());
        assertEquals(BGHistoryLog.BgSourceType.LOCAL_PUMP_ENTRY, parsedRes.getBgSourceTypeEnum());
    }

    @Test
    public void testBGHistoryLog_mobiCalibrationRemoteEntry() throws DecoderException {
        // Tandem Mobi, official Tandem app, March 2025 BLE capture. A fingerstick BG of 170 entered
        // in the app and used to calibrate the CGM: the pump wrote a CGM calibration log with the
        // same value in the same second. Header high nibble is 1.
        BGHistoryLog expected = (BGHistoryLog) new BGHistoryLog(
                // long pumpTimeSec, long sequenceNum, int bg, int cgmCalibration, int bgSource, float iob, int targetBG, int isf, int selectedIOB, int bgSourceType, int spare
                542807876L, 231542L,
                170, 1, 0, Float.intBitsToFloat(0x415c4199), 110, 30, 1, 1, 0
        ).withHeaderHighNibble(1);

        BGHistoryLog parsedRes = (BGHistoryLog) HistoryLogMessageTester.testSingle(
                "101044975a2076880300aa00010099415c416e001e0001010000",
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
        assertEquals(170, parsedRes.getBg());
        assertEquals(1, parsedRes.getCgmCalibration());
        assertTrue(parsedRes.isUsedForCgmCalibration());
        assertEquals(LastBGResponse.BgSource.MANUAL, parsedRes.getBgSource());
        assertEquals(BolusRequestedMsg2HistoryLog.SelectedIOBType.SWAN_IOB_MEAL, parsedRes.getSelectedIOBType());
        assertEquals(BGHistoryLog.BgSourceType.REMOTE_ENTRY, parsedRes.getBgSourceTypeEnum());
    }

    @Test
    public void testBgSourceTypeEnum() {
        assertEquals(BGHistoryLog.BgSourceType.LOCAL_PUMP_ENTRY, BGHistoryLog.BgSourceType.fromId(0));
        assertEquals(BGHistoryLog.BgSourceType.REMOTE_ENTRY, BGHistoryLog.BgSourceType.fromId(1));
        assertEquals(null, BGHistoryLog.BgSourceType.fromId(2));
    }
}
