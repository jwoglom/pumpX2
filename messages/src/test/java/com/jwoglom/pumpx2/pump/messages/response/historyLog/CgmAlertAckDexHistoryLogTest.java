package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class CgmAlertAckDexHistoryLogTest {
    @Test
    public void testCgmAlertAckDexHistoryLog1() throws DecoderException {
        CgmAlertAckDexHistoryLog expected = (CgmAlertAckDexHistoryLog) new CgmAlertAckDexHistoryLog(
            // long pumpTimeSec, long sequenceNum, int alertId, int sensorType, long ackSource
            580750504L, 489859L, 2, 3, 0L
        ).withHeaderHighNibble(1);

        CgmAlertAckDexHistoryLog parsedRes = (CgmAlertAckDexHistoryLog) HistoryLogMessageTester.testSingle(
                "7311a88c9d228379070002030000000000000000000000000000",
                expected
        );
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }

    @Test
    public void testCgmAlertAckDexHistoryLog2() throws DecoderException {
        // this sample's ackSource=1 (raw byte 14 = 0x01) reflects the alert being acknowledged
        // from a source other than the default
        CgmAlertAckDexHistoryLog expected = (CgmAlertAckDexHistoryLog) new CgmAlertAckDexHistoryLog(
            // long pumpTimeSec, long sequenceNum, int alertId, int sensorType, long ackSource
            579789268L, 449784L, 32, 3, 1L
        ).withHeaderHighNibble(1);

        CgmAlertAckDexHistoryLog parsedRes = (CgmAlertAckDexHistoryLog) HistoryLogMessageTester.testSingle(
                "7311d4e18e22f8dc060020030000010000000000000000000000",
                expected
        );
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }
}
