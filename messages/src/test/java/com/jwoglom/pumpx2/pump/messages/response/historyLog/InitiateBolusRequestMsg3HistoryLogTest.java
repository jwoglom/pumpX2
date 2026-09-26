package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;
import static org.junit.Assert.assertEquals;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class InitiateBolusRequestMsg3HistoryLogTest {
    @Test
    public void testMobiTandemKitBolus() throws DecoderException {
        InitiateBolusRequestMsg3HistoryLog expected = new InitiateBolusRequestMsg3HistoryLog(
                // long pumpTimeSec, long sequenceNum, int transactionId, int bolusId, long bolusIOB, long extendedSeconds, long extended3, int headerHighNibble
                589529231L, 640851L, 139, 2904, 0L, 0L, 0L, 1
        );

        InitiateBolusRequestMsg3HistoryLog parsedRes = (InitiateBolusRequestMsg3HistoryLog) HistoryLogMessageTester.testSingle(
                "57118f80232353c709008b00580b000000000000000000000000",
                expected
        );
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
        assertEquals(139, parsedRes.getTransactionId());
    }

    // t:slim X2 with Tandem's Android app. The captured InitiateBolusRequest for bolus 10653 was
    // txId 57 with bolusIOB 130.
    @Test
    public void testX2OfficialAppBolusIob() throws DecoderException {
        InitiateBolusRequestMsg3HistoryLog expected = new InitiateBolusRequestMsg3HistoryLog(
                461589433L, 25684L, 57, 10653, 130L, 0L, 0L
        );

        InitiateBolusRequestMsg3HistoryLog parsedRes = (InitiateBolusRequestMsg3HistoryLog) HistoryLogMessageTester.testSingle(
                "5701b94b831b5464000039009d29820000000000000000000000",
                expected
        );
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
        assertEquals(57, parsedRes.getTransactionId());
        assertEquals(10653, parsedRes.getBolusId());
        assertEquals(130L, parsedRes.getBolusIOB());
    }

    // Synthetic: no maintainer capture holds an extended BLE bolus, so this only checks the
    // extendedSeconds offset round-trips.
    @Test
    public void testSyntheticExtendedSeconds() {
        InitiateBolusRequestMsg3HistoryLog built = new InitiateBolusRequestMsg3HistoryLog(
                1L, 2L, 32, 248, 0L, 28800L, 0L
        );
        InitiateBolusRequestMsg3HistoryLog parsed = (InitiateBolusRequestMsg3HistoryLog) HistoryLogParser.parse(built.getCargo());
        assertEquals(28800L, parsed.getExtendedSeconds());
    }
}
