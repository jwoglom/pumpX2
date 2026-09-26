package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;
import static org.junit.Assert.assertEquals;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class BatteryEstimateUpdateHistoryLogTest {
    // Maintainer's Tandem Mobi: a one-step sequence (step 1, then step 0 thirty seconds later).

    @Test
    public void testOneStepSequence_start() throws DecoderException {
        BatteryEstimateUpdateHistoryLog expected = (BatteryEstimateUpdateHistoryLog) new BatteryEstimateUpdateHistoryLog(
                // long pumpTimeSec, long sequenceNum, int unknown10, int unknown11, int currentBatteryAbc, int unknown13, int unknown14, int lipoMv, int currentBatteryIbc, long elapsedMs
                589535285L, 641089L, 1, 0, 85, 136, 0, 4043, 85, 0L
        ).withHeaderHighNibble(1);

        BatteryEstimateUpdateHistoryLog parsedRes = (BatteryEstimateUpdateHistoryLog) HistoryLogMessageTester.testSingle(
                "4a113598232341c80900010055880000cb0f5500000000000000",
                expected
        );
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
        assertEquals(136, parsedRes.getUnknown13());
    }

    @Test
    public void testOneStepSequence_end() throws DecoderException {
        BatteryEstimateUpdateHistoryLog expected = (BatteryEstimateUpdateHistoryLog) new BatteryEstimateUpdateHistoryLog(
                589535315L, 641092L, 0, 0, 85, 85, 0, 4043, 85, 0L
        ).withHeaderHighNibble(1);

        BatteryEstimateUpdateHistoryLog parsedRes = (BatteryEstimateUpdateHistoryLog) HistoryLogMessageTester.testSingle(
                "4a115398232344c80900000055550000cb0f5500000000000000",
                expected
        );
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }

    // Maintainer's Tandem Mobi: a three-cycle sequence in which currentBatteryAbc went from 54 to 61.
    // The CurrentBatteryV2Response read 1 second after the final record reported abc 61 (54 before).

    @Test
    public void testThreeCycleSequence_start() throws DecoderException {
        BatteryEstimateUpdateHistoryLog expected = (BatteryEstimateUpdateHistoryLog) new BatteryEstimateUpdateHistoryLog(
                590938802L, 715272L, 1, 0, 54, 61, 0, 3864, 45, 0L
        ).withHeaderHighNibble(1);

        BatteryEstimateUpdateHistoryLog parsedRes = (BatteryEstimateUpdateHistoryLog) HistoryLogMessageTester.testSingle(
                "4a11b202392308ea0a000100363d0000180f2d00000000000000",
                expected
        );
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
        assertEquals(54, parsedRes.getCurrentBatteryAbc());
        assertEquals(61, parsedRes.getUnknown13());
    }

    @Test
    public void testThreeCycleSequence_step3() throws DecoderException {
        BatteryEstimateUpdateHistoryLog expected = (BatteryEstimateUpdateHistoryLog) new BatteryEstimateUpdateHistoryLog(
                590938862L, 715276L, 3, 1, 54, 61, 0, 3864, 45, 60630L
        ).withHeaderHighNibble(1);

        BatteryEstimateUpdateHistoryLog parsedRes = (BatteryEstimateUpdateHistoryLog) HistoryLogMessageTester.testSingle(
                "4a11ee0239230cea0a000301363d0000180f2d000000d6ec0000",
                expected
        );
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
        assertEquals(60630L, parsedRes.getElapsedMs());
        assertEquals(1, parsedRes.getUnknown11());
    }

    @Test
    public void testThreeCycleSequence_end() throws DecoderException {
        BatteryEstimateUpdateHistoryLog expected = (BatteryEstimateUpdateHistoryLog) new BatteryEstimateUpdateHistoryLog(
                590938990L, 715287L, 0, 0, 61, 61, 0, 3864, 45, 0L
        ).withHeaderHighNibble(1);

        BatteryEstimateUpdateHistoryLog parsedRes = (BatteryEstimateUpdateHistoryLog) HistoryLogMessageTester.testSingle(
                "4a116e03392317ea0a0000003d3d0000180f2d00000000000000",
                expected
        );
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
        assertEquals(61, parsedRes.getCurrentBatteryAbc());
    }

    @Test
    public void testTslimX2_step2() throws DecoderException {
        // Maintainer's t:slim X2: elapsedMs is exactly 30000 on the step written 30 seconds after the start.
        BatteryEstimateUpdateHistoryLog expected = new BatteryEstimateUpdateHistoryLog(
                461579488L, 25461L, 2, 0, 99, 99, 0, 4166, 100, 30000L
        );

        BatteryEstimateUpdateHistoryLog parsedRes = (BatteryEstimateUpdateHistoryLog) HistoryLogMessageTester.testSingle(
                "4a01e024831b7563000002006363000046106400000030750000",
                expected
        );
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
        assertEquals(30000L, parsedRes.getElapsedMs());
    }
}
