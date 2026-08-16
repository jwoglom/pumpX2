package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class AlarmClearedHistoryLogTest {
    @Test
    public void testAlarmClearedHistoryLog1() throws DecoderException {
        AlarmClearedHistoryLog expected = (AlarmClearedHistoryLog) new AlarmClearedHistoryLog(
            // long pumpTimeSec, long sequenceNum, long alarmId
            580777655L, 491078L, 18
        ).withHeaderHighNibble(1);

        AlarmClearedHistoryLog parsedRes = (AlarmClearedHistoryLog) HistoryLogMessageTester.testSingle(
                "1c10b7f69d22467e070012000000000000000000000000000000",
                expected
        );
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }

    @Test
    public void testAlarmClearedHistoryLog2() throws DecoderException {
        AlarmClearedHistoryLog expected = (AlarmClearedHistoryLog) new AlarmClearedHistoryLog(
            // long pumpTimeSec, long sequenceNum, long alarmId
            580777655L, 491077L, 23
        ).withHeaderHighNibble(1);

        AlarmClearedHistoryLog parsedRes = (AlarmClearedHistoryLog) HistoryLogMessageTester.testSingle(
                "1c10b7f69d22457e070017000000000000000000000000000000",
                expected
        );
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }
}
