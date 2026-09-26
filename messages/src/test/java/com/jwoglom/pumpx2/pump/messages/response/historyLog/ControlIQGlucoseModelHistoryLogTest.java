package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.junit.Test;

public class ControlIQGlucoseModelHistoryLogTest {

    // The paired 227 record: same pump second, sequence number one lower.
    private static void assertPairedWith227(String hex227, ControlIQGlucoseModelHistoryLog log) throws DecoderException {
        byte[] raw227 = Hex.decodeHex(hex227);
        assertEquals(227, Bytes.readShort(raw227, 0) & 0x0FFF);
        assertEquals(log.getPumpTimeSec(), Bytes.readUint32(raw227, 2));
        assertEquals(log.getSequenceNum() - 1, Bytes.readUint32(raw227, 6));
        assertEquals(Math.min(log.getFilteredGlucose(), 255), raw227[20] & 0xFF);
        assertEquals(log.getTdiEstimate(), raw227[22] & 0xFF);
    }

    @Test
    public void testControlIQGlucoseModel_x2ControlIQWithCgm() throws DecoderException {
        // Maintainer's t:slim X2 (2023), Control-IQ closed loop with a CGM. Last CGM reading 145
        // mg/dL; the two previous records had filtered glucose 133 and 139.
        ControlIQGlucoseModelHistoryLog expected = new ControlIQGlucoseModelHistoryLog(
                // long pumpTimeSec, long sequenceNum, int tdiEstimate, int unknown11, int unknown12, int shortTermPredictedGlucoseRaw, int filteredGlucose, int unknown18, int correctionFactor, int unknown21, int unknown22, int unknown24
                497792406L, 1905835L, 50, 0, 0, 15580, 144, 0, 0, 0, 0, 0
        );

        ControlIQGlucoseModelHistoryLog parsedRes = (ControlIQGlucoseModelHistoryLog) HistoryLogMessageTester.testSingle(
                "470196b5ab1dab141d0032000000dc3c90000000000000000000",
                expected
        );
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
        assertEquals(50, parsedRes.getTdiEstimate());
        assertEquals(144, parsedRes.getFilteredGlucose());
        assertEquals(155.80, parsedRes.getShortTermPredictedGlucoseMgdl(), 0.001);
        assertEquals(0, parsedRes.getCorrectionFactor());
        assertPairedWith227("e30096b5ab1daa141d0022007500e940e8030000901e32c800a0", parsedRes);
    }

    @Test
    public void testControlIQGlucoseModel_x2FilteredGlucoseAbove255() throws DecoderException {
        // Maintainer's t:slim X2 (2024), Control-IQ with a CGM. Last CGM reading 337 mg/dL. The
        // paired 227 caps its 8-bit copy of the filtered glucose at 255.
        ControlIQGlucoseModelHistoryLog expected = new ControlIQGlucoseModelHistoryLog(
                534198333L, 1050496L, 83, 0, 0, 33263, 333, 0, 0, 0, 0, 0
        );

        ControlIQGlucoseModelHistoryLog parsedRes = (ControlIQGlucoseModelHistoryLog) HistoryLogMessageTester.testSingle(
                "47013d38d71f8007100053000000ef814d010000000000000000",
                expected
        );
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
        assertEquals(333, parsedRes.getFilteredGlucose());
        assertEquals(332.63, parsedRes.getShortTermPredictedGlucoseMgdl(), 0.001);
        assertEquals(83, parsedRes.getTdiEstimate());
        assertPairedWith227("e3003d38d71f7f07100000004200328020034716ff1553c800a0", parsedRes);
    }

    @Test
    public void testControlIQGlucoseModel_mobiControlIQOffCorrectionFactor() throws DecoderException {
        // Maintainer's Tandem Mobi (7.9.0.2), Control-IQ disabled, no CGM. Header high nibble 1.
        // The 332 record in the same second has a new TDI estimate of 74.05 units:
        // floor(74.05) = 74 and floor(1800 / 74.05) = 24.
        ControlIQGlucoseModelHistoryLog expected = (ControlIQGlucoseModelHistoryLog) new ControlIQGlucoseModelHistoryLog(
                591098983L, 723618L, 74, 0, 0, 8907, 89, 0, 24, 0, 0, 0
        ).withHeaderHighNibble(1);

        ControlIQGlucoseModelHistoryLog parsedRes = (ControlIQGlucoseModelHistoryLog) HistoryLogMessageTester.testSingle(
                "471167743b23a20a0b004a000000cb2259000000180000000000",
                expected
        );
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
        assertEquals(74, parsedRes.getTdiEstimate());
        assertEquals(24, parsedRes.getCorrectionFactor());
        assertEquals((int) Math.floor(1800 / 74.05), parsedRes.getCorrectionFactor());
        assertEquals(89, parsedRes.getFilteredGlucose());
        assertEquals(89.07, parsedRes.getShortTermPredictedGlucoseMgdl(), 0.001);
        assertPairedWith227("e31067743b23a10a0b00000013007e22a0056a0259184a20c878", parsedRes);
        byte[] raw227 = Hex.decodeHex("e31067743b23a10a0b00000013007e22a0056a0259184a20c878");
        assertEquals(parsedRes.getCorrectionFactor(), raw227[21] & 0xFF);
    }

    @Test
    public void testControlIQGlucoseModel_mobiCorrectionFactorCapped() throws DecoderException {
        // Maintainer's Tandem Mobi (7.9.0.2), Control-IQ disabled, no CGM. Header high nibble 1.
        // The 332 record in the same second has a new TDI estimate of 55.79 units, so
        // 1800 / 55.79 = 32.3, but in these weeks this pump stayed at 30 (its profile ISF) whenever
        // 1800 / TDI was above 30.
        ControlIQGlucoseModelHistoryLog expected = (ControlIQGlucoseModelHistoryLog) new ControlIQGlucoseModelHistoryLog(
                590556271L, 694564L, 55, 0, 0, 8965, 89, 0, 30, 0, 0, 0
        ).withHeaderHighNibble(1);

        ControlIQGlucoseModelHistoryLog parsedRes = (ControlIQGlucoseModelHistoryLog) HistoryLogMessageTester.testSingle(
                "47116f2c332324990a00370000000523590000001e0000000000",
                expected
        );
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
        assertEquals(55, parsedRes.getTdiEstimate());
        assertEquals(30, parsedRes.getCorrectionFactor());
        assertTrue(parsedRes.getShortTermPredictedGlucoseMgdl() > 89.0);
    }

    @Test
    public void testControlIQGlucoseModel_x2AfterReboot() throws DecoderException {
        // Maintainer's t:slim X2 (2023), first record after a pump reboot, before any CGM reading:
        // the TDI estimate restarts from the Control-IQ TDI setting (75), filtered glucose is 31 and
        // the prediction sits at its 20.00 mg/dL floor.
        ControlIQGlucoseModelHistoryLog expected = new ControlIQGlucoseModelHistoryLog(
                503758975L, 2107929L, 75, 0, 0, 2000, 31, 0, 0, 0, 0, 0
        );

        ControlIQGlucoseModelHistoryLog parsedRes = (ControlIQGlucoseModelHistoryLog) HistoryLogMessageTester.testSingle(
                "47017fc0061e192a20004b000000d0071f000000000000000000",
                expected
        );
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
        assertEquals(75, parsedRes.getTdiEstimate());
        assertEquals(31, parsedRes.getFilteredGlucose());
        assertEquals(2000, parsedRes.getShortTermPredictedGlucoseRaw());
        assertEquals(20.0, parsedRes.getShortTermPredictedGlucoseMgdl(), 0.001);
    }
}
