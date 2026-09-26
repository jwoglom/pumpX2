package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;
import static org.junit.Assert.assertEquals;

import com.jwoglom.pumpx2.shared.Hex;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class IobSnapshotPostBolusHistoryLogTest {
    // Mobi driven by Trio, 0.2 U BLE bolus 2904; BolusCompleted.iob is the Mudaliar value.
    @Test
    public void testMobiMudaliarDisplayed() throws DecoderException {
        IobSnapshotPostBolusHistoryLog expected = new IobSnapshotPostBolusHistoryLog(
                // long pumpTimeSec, long sequenceNum, int bolusId, int unknownU8At12, int selectedIob, float mudaliarIob, float swan6hrIob, float unknownFloatAt22, int headerHighNibble
                589529251L, 640860L, 2904, 1, 0,
                Float.intBitsToFloat(0x3f731a58), // ~0.9496
                Float.intBitsToFloat(0x3f53a249), // ~0.8267
                Float.intBitsToFloat(0x3f17aebd), // ~0.5925
                1
        );

        IobSnapshotPostBolusHistoryLog parsedRes = (IobSnapshotPostBolusHistoryLog) HistoryLogMessageTester.testSingle(
                "cf10a38023235cc70900580b0100581a733f49a2533fbdae173f",
                expected
        );
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());

        BolusCompletedHistoryLog completed = (BolusCompletedHistoryLog) HistoryLogParser.parse(
                Hex.decodeHex("1410a38023235dc709000300580b581a733fcdcc4c3ecdcc4c3e"));
        assertEquals(Float.floatToIntBits(completed.getIob()), Float.floatToIntBits(parsedRes.getMudaliarIob()));
    }

    // t:slim X2 with Control-IQ, bolus 10673; BolusCompleted.iob is the Swan value.
    @Test
    public void testX2SwanDisplayed() throws DecoderException {
        IobSnapshotPostBolusHistoryLog expected = new IobSnapshotPostBolusHistoryLog(
                503759781L, 2108126L, 10673, 3, 1,
                Float.intBitsToFloat(0x40233334), // ~2.55
                Float.intBitsToFloat(0x4020c758), // ~2.5122
                Float.intBitsToFloat(0x401f5437)  // ~2.4895
        );

        IobSnapshotPostBolusHistoryLog parsedRes = (IobSnapshotPostBolusHistoryLog) HistoryLogMessageTester.testSingle(
                "cf00a5c3061ede2a2000b12903013433234058c7204037541f40",
                expected
        );
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());

        BolusCompletedHistoryLog completed = (BolusCompletedHistoryLog) HistoryLogParser.parse(
                Hex.decodeHex("1400a5c3061edf2a20000300b12958c72040cdcc4c3dcdcc4c3d"));
        assertEquals(Float.floatToIntBits(completed.getIob()), Float.floatToIntBits(parsedRes.getSwan6hrIob()));
    }
}
