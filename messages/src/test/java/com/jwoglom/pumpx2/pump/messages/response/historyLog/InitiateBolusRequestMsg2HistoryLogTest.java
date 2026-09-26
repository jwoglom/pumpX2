package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;
import static org.junit.Assert.assertEquals;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class InitiateBolusRequestMsg2HistoryLogTest {
    // Mobi driven by Trio: TandemKit sends 0 for correctionVolume, carbs, BG and the extended fields.
    @Test
    public void testMobiTandemKitBolus() throws DecoderException {
        InitiateBolusRequestMsg2HistoryLog expected = new InitiateBolusRequestMsg2HistoryLog(
                // long pumpTimeSec, long sequenceNum, int transactionId, int bolusId, long correctionVolume, long extendedVolume, int bolusCarbs, int bolusBG, int headerHighNibble
                589529231L, 640850L, 139, 2904, 0L, 0L, 0, 0, 1
        );

        InitiateBolusRequestMsg2HistoryLog parsedRes = (InitiateBolusRequestMsg2HistoryLog) HistoryLogMessageTester.testSingle(
                "25118f80232352c709008b00580b000000000000000000000000",
                expected
        );
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
        assertEquals(139, parsedRes.getTransactionId());
        assertEquals(2904, parsedRes.getBolusId());
    }

    // t:slim X2 with Tandem's Android app. The captured InitiateBolusRequest for bolus 10677 was
    // txId 171, correctionVolume 720, bolusCarbs 5, bolusBG 185.
    @Test
    public void testX2OfficialAppFoodAndCorrectionBolus() throws DecoderException {
        InitiateBolusRequestMsg2HistoryLog expected = new InitiateBolusRequestMsg2HistoryLog(
                461710176L, 28138L, 171, 10677, 720L, 0L, 5, 185
        );

        InitiateBolusRequestMsg2HistoryLog parsedRes = (InitiateBolusRequestMsg2HistoryLog) HistoryLogMessageTester.testSingle(
                "25016023851bea6d0000ab00b529d0020000000000000500b900",
                expected
        );
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
        assertEquals(720L, parsedRes.getCorrectionVolume());
        assertEquals(5, parsedRes.getBolusCarbs());
        assertEquals(185, parsedRes.getBolusBG());
    }

    // Synthetic: no maintainer capture holds an extended BLE bolus, so this only checks the
    // extendedVolume offset round-trips.
    @Test
    public void testSyntheticExtendedVolume() {
        InitiateBolusRequestMsg2HistoryLog built = new InitiateBolusRequestMsg2HistoryLog(
                1L, 2L, 32, 248, 0L, 2000L, 0, 0
        );
        InitiateBolusRequestMsg2HistoryLog parsed = (InitiateBolusRequestMsg2HistoryLog) HistoryLogParser.parse(built.getCargo());
        assertEquals(2000L, parsed.getExtendedVolume());
        assertEquals(248, parsed.getBolusId());
    }
}
