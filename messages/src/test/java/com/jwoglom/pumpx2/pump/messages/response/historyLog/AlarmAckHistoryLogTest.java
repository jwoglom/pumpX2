package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class AlarmAckHistoryLogTest {
    @Test
    public void testAlarmAckHistoryLog1() throws DecoderException {
        AlarmAckHistoryLog expected = (AlarmAckHistoryLog) new AlarmAckHistoryLog(
            // long pumpTimeSec, long sequenceNum, long alarmId
            580777655L, 491076L, 18
        ).withHeaderHighNibble(1);

        AlarmAckHistoryLog parsedRes = (AlarmAckHistoryLog) HistoryLogMessageTester.testSingle(
                "0810b7f69d22447e070012000000000000000000000000000000",
                expected
        );
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }

    @Test
    public void testAlarmAckHistoryLog2() throws DecoderException {
        AlarmAckHistoryLog expected = (AlarmAckHistoryLog) new AlarmAckHistoryLog(
            // long pumpTimeSec, long sequenceNum, long alarmId
            579744825L, 448164L, 18
        ).withHeaderHighNibble(1);

        AlarmAckHistoryLog parsedRes = (AlarmAckHistoryLog) HistoryLogMessageTester.testSingle(
                "081039348e22a4d6060012000000000000000000000000000000",
                expected
        );
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }
}
