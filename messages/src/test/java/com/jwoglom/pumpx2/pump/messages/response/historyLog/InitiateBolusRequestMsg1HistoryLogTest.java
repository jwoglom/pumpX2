package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;
import static org.junit.Assert.assertEquals;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class InitiateBolusRequestMsg1HistoryLogTest {
    // Mobi driven by Trio, 0.2 U bolus 2904. TandemKit sends foodVolume = totalVolume.
    @Test
    public void testMobiTandemKitBolus() throws DecoderException {
        InitiateBolusRequestMsg1HistoryLog expected = new InitiateBolusRequestMsg1HistoryLog(
                // long pumpTimeSec, long sequenceNum, long requestTimestamp, int transactionId, int bolusTypeBitmask, int bolusId, long totalVolume, long foodVolume, int headerHighNibble
                589529231L, 640849L, 195084L, 139, 8, 2904, 200L, 200L, 1
        );

        InitiateBolusRequestMsg1HistoryLog parsedRes = (InitiateBolusRequestMsg1HistoryLog) HistoryLogMessageTester.testSingle(
                "24118f80232351c709000cfa02008b08580bc8000000c8000000",
                expected
        );
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
        assertEquals(139, parsedRes.getTransactionId());
        assertEquals(8, parsedRes.getBolusTypeBitmask());
        assertEquals(2904, parsedRes.getBolusId());
        assertEquals(200L, parsedRes.getTotalVolume());
    }

    // t:slim X2 with Tandem's Android app. The captured InitiateBolusRequest for bolus 10677 was
    // txId 171, bolusTypeBitmask 3, totalVolume 770, foodVolume 50.
    @Test
    public void testX2OfficialAppFoodAndCorrectionBolus() throws DecoderException {
        InitiateBolusRequestMsg1HistoryLog expected = new InitiateBolusRequestMsg1HistoryLog(
                461710176L, 28137L, 461710145L, 171, 3, 10677, 770L, 50L
        );

        InitiateBolusRequestMsg1HistoryLog parsedRes = (InitiateBolusRequestMsg1HistoryLog) HistoryLogMessageTester.testSingle(
                "24016023851be96d00004123851bab03b5290203000032000000",
                expected
        );
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
        assertEquals(171, parsedRes.getTransactionId());
        assertEquals(3, parsedRes.getBolusTypeBitmask());
        assertEquals(770L, parsedRes.getTotalVolume());
        assertEquals(50L, parsedRes.getFoodVolume());
    }
}
