package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import com.jwoglom.pumpx2.shared.Hex;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

/**
 * Round-trip coverage for the five bolus history logs as emitted by a Tandem Mobi, whose logs
 * carry a log generation nibble of 1 in the high bits of the first two cargo bytes (so opCode 55
 * is transmitted as {@code 37 10}, not {@code 37 00}).
 *
 * <p>Every existing fixture in this package comes from a t:slim X2 and carries a generation of 0,
 * so nothing else in the suite exercises that nibble: neither the dispatch path in
 * {@link HistoryLogParser} nor the ability of {@code buildCargo} to reproduce such a record.
 *
 * <p><b>The field values in these fixtures are invented.</b> Only their structure is replicated
 * from a real capture: the generation nibble, one record of each opCode per bolus with a shared
 * bolusId, consecutive sequence numbers across the msg1/msg2/msg3 triple, and the msg3 identity
 * foodBolusSize + correctionBolusSize == totalBolusSize. These fixtures verify that parsing and
 * serialization agree and that the byte offsets do not move. They are not evidence for what any
 * individual field means.
 */
public class BolusMobiGenerationHistoryLogTest {
    private static final int MOBI = 1;

    // Bolus 2001: BLE-commanded standard bolus, food only.
    private static final String B1_MSG1 = "40100065cd1da0bb0d00d1070300000000000000000000000000";
    private static final String B1_MSG2 = "41100065cd1da1bb0d00d1070464000000000000000001000100";
    private static final String B1_MSG3 = "42100065cd1da2bb0d00d1070000000060400000000000006040";
    private static final String B1_ACTIVATED = "37100265cd1da8bb0d00d1070100000010400000604000000000";
    private static final String B1_COMPLETED = "14107865cd1db2bb0d000300d1070000b8400000604000006040";

    // Bolus 2002: BLE-commanded standard bolus, food plus a nonzero correction component.
    private static final String B2_MSG1 = "4010e868cd1d04bc0d00d2070301000000000000000000000000";
    private static final String B2_MSG2 = "4110e868cd1d05bc0d00d2070464000000000000000001000000";
    private static final String B2_MSG3 = "4210e868cd1d06bc0d00d2070000000088400000403f0000a040";
    private static final String B2_ACTIVATED = "3710ea68cd1d0cbc0d00d20700000000c03f0000a04000000000";
    private static final String B2_COMPLETED = "14106069cd1d16bc0d000300d2070000d0400000a0400000a040";

    // Bolus 2003: pump-entered carb bolus, exercising carbAmount/bg/isf/targetBG/carbRatio.
    private static final String B3_MSG1 = "4010d06ccd1d68bc0d00d30701012d008c000000c03f70170000";
    private static final String B3_MSG2 = "4110d06ccd1d69bc0d00d3070064000000001e006e0000000100";
    private static final String B3_MSG3 = "4210d06ccd1d6abc0d00d30700000000f0400000803e0000f840";
    private static final String B3_ACTIVATED = "3710d26ccd1d70bc0d00d30701000000403f0000f84000000000";
    private static final String B3_COMPLETED = "1410486dcd1d7abc0d000300d307000008410000f8400000f840";

    @Test
    public void testBolusRequestedMsg1Mobi() throws DecoderException {
        // long pumpTimeSec, long sequenceNum, int bolusId, int bolusType, boolean correctionBolusIncluded, int carbAmount, int bg, float iob, long carbRatio, int logGeneration
        assertRoundTrip(B1_MSG1, new BolusRequestedMsg1HistoryLog(
                500000000L, 900000L, 2001, 3, false, 0, 0, 0.0F, 0, MOBI));
        assertRoundTrip(B2_MSG1, new BolusRequestedMsg1HistoryLog(
                500001000L, 900100L, 2002, 3, true, 0, 0, 0.0F, 0, MOBI));
        assertRoundTrip(B3_MSG1, new BolusRequestedMsg1HistoryLog(
                500002000L, 900200L, 2003, 1, true, 45, 140, 1.5F, 6000, MOBI));
    }

    @Test
    public void testBolusRequestedMsg2Mobi() throws DecoderException {
        // long pumpTimeSec, long sequenceNum, int bolusId, int options, int standardPercent, int duration, int spare1, int isf, int targetBG, boolean userOverride, boolean declinedCorrection, int selectedIOB, int spare2, int logGeneration
        assertRoundTrip(B1_MSG2, new BolusRequestedMsg2HistoryLog(
                500000000L, 900001L, 2001, 4, 100, 0, 0, 0, 0, true, false, 1, 0, MOBI));
        assertRoundTrip(B2_MSG2, new BolusRequestedMsg2HistoryLog(
                500001000L, 900101L, 2002, 4, 100, 0, 0, 0, 0, true, false, 0, 0, MOBI));
        assertRoundTrip(B3_MSG2, new BolusRequestedMsg2HistoryLog(
                500002000L, 900201L, 2003, 0, 100, 0, 0, 30, 110, false, false, 1, 0, MOBI));
    }

    @Test
    public void testBolusRequestedMsg3Mobi() throws DecoderException {
        // long pumpTimeSec, long sequenceNum, int bolusId, int spare, float foodBolusSize, float correctionBolusSize, float totalBolusSize, int logGeneration
        assertRoundTrip(B1_MSG3, new BolusRequestedMsg3HistoryLog(
                500000000L, 900002L, 2001, 0, 3.5F, 0.0F, 3.5F, MOBI));
        assertRoundTrip(B2_MSG3, new BolusRequestedMsg3HistoryLog(
                500001000L, 900102L, 2002, 0, 4.25F, 0.75F, 5.0F, MOBI));
        assertRoundTrip(B3_MSG3, new BolusRequestedMsg3HistoryLog(
                500002000L, 900202L, 2003, 0, 7.5F, 0.25F, 7.75F, MOBI));
    }

    @Test
    public void testBolusActivatedMobi() throws DecoderException {
        // long pumpTimeSec, long sequenceNum, int bolusId, int selectedIob, float iob, float bolusSize, int logGeneration
        assertRoundTrip(B1_ACTIVATED, new BolusActivatedHistoryLog(
                500000002L, 900008L, 2001, 1, 2.25F, 3.5F, MOBI));
        assertRoundTrip(B2_ACTIVATED, new BolusActivatedHistoryLog(
                500001002L, 900108L, 2002, 0, 1.5F, 5.0F, MOBI));
        assertRoundTrip(B3_ACTIVATED, new BolusActivatedHistoryLog(
                500002002L, 900208L, 2003, 1, 0.75F, 7.75F, MOBI));
    }

    @Test
    public void testBolusCompletedMobi() throws DecoderException {
        // long pumpTimeSec, long sequenceNum, int completionStatus, int bolusId, float iob, float insulinDelivered, float insulinRequested, int logGeneration
        assertRoundTrip(B1_COMPLETED, new BolusCompletedHistoryLog(
                500000120L, 900018L, 3, 2001, 5.75F, 3.5F, 3.5F, MOBI));
        assertRoundTrip(B2_COMPLETED, new BolusCompletedHistoryLog(
                500001120L, 900118L, 3, 2002, 6.5F, 5.0F, 5.0F, MOBI));
        assertRoundTrip(B3_COMPLETED, new BolusCompletedHistoryLog(
                500002120L, 900218L, 3, 2003, 8.5F, 7.75F, 7.75F, MOBI));
    }

    /**
     * A Mobi record must reach its class through the primary dispatch path. Before the log
     * generation nibble was masked off in {@link HistoryLogParser}, opCode 55 with a generation of
     * 1 was computed as typeId 4151, missed the registry, and was only recovered by the retry
     * ladder, which logged a warning for every history log a Mobi user ever downloaded.
     */
    @Test
    public void testMobiRecordsDispatchToTheCorrectClass() throws DecoderException {
        assertEquals(MOBI, HistoryLogParser.parse(Hex.decodeHex(B1_MSG1)).getLogGeneration());

        // The typeId the dispatcher looks up must already be the real opCode, so that these
        // records resolve on the first attempt rather than by falling through the retry ladder.
        assertEquals(64, HistoryLogParser.typeIdOf(Hex.decodeHex(B1_MSG1)));
        assertEquals(65, HistoryLogParser.typeIdOf(Hex.decodeHex(B1_MSG2)));
        assertEquals(66, HistoryLogParser.typeIdOf(Hex.decodeHex(B1_MSG3)));
        assertEquals(55, HistoryLogParser.typeIdOf(Hex.decodeHex(B1_ACTIVATED)));
        assertEquals(20, HistoryLogParser.typeIdOf(Hex.decodeHex(B1_COMPLETED)));

        assertTrue(HistoryLogParser.parse(Hex.decodeHex(B1_MSG1)) instanceof BolusRequestedMsg1HistoryLog);
        assertTrue(HistoryLogParser.parse(Hex.decodeHex(B1_MSG2)) instanceof BolusRequestedMsg2HistoryLog);
        assertTrue(HistoryLogParser.parse(Hex.decodeHex(B1_MSG3)) instanceof BolusRequestedMsg3HistoryLog);
        assertTrue(HistoryLogParser.parse(Hex.decodeHex(B1_ACTIVATED)) instanceof BolusActivatedHistoryLog);
        assertTrue(HistoryLogParser.parse(Hex.decodeHex(B1_COMPLETED)) instanceof BolusCompletedHistoryLog);
    }

    /**
     * The five logs emitted for a single bolus must agree on the bolusId. This is the property
     * which distinguishes the correct byte offsets from the alternatives: reading bolusId from the
     * wrong offset makes at least one of the five disagree with the others.
     */
    @Test
    public void testBolusIdAgreesAcrossAllFiveLogs() throws DecoderException {
        assertBolusIdsAgree(2001, B1_MSG1, B1_MSG2, B1_MSG3, B1_ACTIVATED, B1_COMPLETED);
        assertBolusIdsAgree(2002, B2_MSG1, B2_MSG2, B2_MSG3, B2_ACTIVATED, B2_COMPLETED);
        assertBolusIdsAgree(2003, B3_MSG1, B3_MSG2, B3_MSG3, B3_ACTIVATED, B3_COMPLETED);
    }

    /**
     * msg3 reports the food and correction components alongside the total. Compared with a
     * tolerance, since the pump recomputes the total in float rather than copying the sum.
     */
    @Test
    public void testMsg3ComponentsSumToTotal() throws DecoderException {
        for (String hex : new String[]{B1_MSG3, B2_MSG3, B3_MSG3}) {
            BolusRequestedMsg3HistoryLog msg3 =
                    (BolusRequestedMsg3HistoryLog) HistoryLogParser.parse(Hex.decodeHex(hex));
            assertEquals(msg3.getTotalBolusSize(),
                    msg3.getFoodBolusSize() + msg3.getCorrectionBolusSize(),
                    HistoryLog.INSULIN_FLOAT_EPSILON);
        }
    }

    /**
     * The dose reported by msg3, by the activated log and by the completed log must agree.
     */
    @Test
    public void testDoseAgreesAcrossLogs() throws DecoderException {
        assertDoseAgrees(B1_MSG3, B1_ACTIVATED, B1_COMPLETED);
        assertDoseAgrees(B2_MSG3, B2_ACTIVATED, B2_COMPLETED);
        assertDoseAgrees(B3_MSG3, B3_ACTIVATED, B3_COMPLETED);
    }

    /**
     * byte 12 of the activated log is a field, not padding: it varies independently of the bytes
     * around it and matches the selectedIOB reported by msg2 for the same bolus.
     */
    @Test
    public void testActivatedSelectedIobIsAField() throws DecoderException {
        BolusActivatedHistoryLog first =
                (BolusActivatedHistoryLog) HistoryLogParser.parse(Hex.decodeHex(B1_ACTIVATED));
        BolusActivatedHistoryLog second =
                (BolusActivatedHistoryLog) HistoryLogParser.parse(Hex.decodeHex(B2_ACTIVATED));
        assertNotEquals(first.getSelectedIob(), second.getSelectedIob());

        assertEquals(msg2Of(B1_MSG2).getSelectedIOB(), first.getSelectedIob());
        assertEquals(msg2Of(B2_MSG2).getSelectedIOB(), second.getSelectedIob());
    }

    @Test
    public void testEnumsDecodeMobiValues() throws DecoderException {
        assertEquals(BolusRequestedMsg1HistoryLog.BolusType.REMOTE,
                ((BolusRequestedMsg1HistoryLog) HistoryLogParser.parse(Hex.decodeHex(B1_MSG1))).getBolusType());
        assertEquals(BolusRequestedMsg1HistoryLog.BolusType.CARB,
                ((BolusRequestedMsg1HistoryLog) HistoryLogParser.parse(Hex.decodeHex(B3_MSG1))).getBolusType());

        assertEquals(BolusRequestedMsg2HistoryLog.BolusOption.BLE_STANDARD, msg2Of(B1_MSG2).getBolusOption());
        assertEquals(BolusRequestedMsg2HistoryLog.BolusOption.STANDARD, msg2Of(B3_MSG2).getBolusOption());

        assertEquals(BolusRequestedMsg2HistoryLog.SelectedIOBType.SWAN_IOB_MEAL, msg2Of(B1_MSG2).getSelectedIOBType());
        assertEquals(BolusRequestedMsg2HistoryLog.SelectedIOBType.MUDALIAR_IOB, msg2Of(B2_MSG2).getSelectedIOBType());
    }

    private static BolusRequestedMsg2HistoryLog msg2Of(String hex) throws DecoderException {
        return (BolusRequestedMsg2HistoryLog) HistoryLogParser.parse(Hex.decodeHex(hex));
    }

    private static void assertDoseAgrees(String msg3Hex, String activatedHex, String completedHex)
            throws DecoderException {
        float total = ((BolusRequestedMsg3HistoryLog) HistoryLogParser.parse(Hex.decodeHex(msg3Hex)))
                .getTotalBolusSize();
        float activated = ((BolusActivatedHistoryLog) HistoryLogParser.parse(Hex.decodeHex(activatedHex)))
                .getBolusSize();
        float requested = ((BolusCompletedHistoryLog) HistoryLogParser.parse(Hex.decodeHex(completedHex)))
                .getInsulinRequested();

        assertEquals(total, activated, HistoryLog.INSULIN_FLOAT_EPSILON);
        assertEquals(total, requested, HistoryLog.INSULIN_FLOAT_EPSILON);
    }

    private static void assertBolusIdsAgree(int expectedBolusId, String msg1Hex, String msg2Hex,
                                            String msg3Hex, String activatedHex, String completedHex)
            throws DecoderException {
        assertEquals(expectedBolusId,
                ((BolusRequestedMsg1HistoryLog) HistoryLogParser.parse(Hex.decodeHex(msg1Hex))).getBolusId());
        assertEquals(expectedBolusId, msg2Of(msg2Hex).getBolusId());
        assertEquals(expectedBolusId,
                ((BolusRequestedMsg3HistoryLog) HistoryLogParser.parse(Hex.decodeHex(msg3Hex))).getBolusId());
        assertEquals(expectedBolusId,
                ((BolusActivatedHistoryLog) HistoryLogParser.parse(Hex.decodeHex(activatedHex))).getBolusId());
        assertEquals(expectedBolusId,
                ((BolusCompletedHistoryLog) HistoryLogParser.parse(Hex.decodeHex(completedHex))).getBolusId());
    }

    private static void assertRoundTrip(String rawHex, HistoryLog expected) throws DecoderException {
        HistoryLog parsed = HistoryLogMessageTester.testSingle(rawHex, expected);
        assertEquals(MOBI, parsed.getLogGeneration());
        assertHexEquals(expected.getCargo(), parsed.getCargo());
    }
}
