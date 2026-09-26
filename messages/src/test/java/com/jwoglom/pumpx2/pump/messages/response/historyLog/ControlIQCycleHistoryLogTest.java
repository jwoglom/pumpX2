package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;
import static org.junit.Assert.assertEquals;

import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.shared.Hex;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class ControlIQCycleHistoryLogTest {

    // floor(200 / (1 + 2.5 * r)) with r = Kovatchev low-BG risk of the predicted glucose; 0 below 70 mg/dL.
    private static int expectedHypoBrake(double predictedGlucoseMgdl) {
        if (predictedGlucoseMgdl < 70) {
            return 0;
        }
        double f = 1.509 * (Math.pow(Math.log(predictedGlucoseMgdl), 1.084) - 5.381);
        double risk = f < 0 ? 10 * f * f : 0;
        return (int) Math.floor(200 / (1 + 2.5 * risk) + 1e-9);
    }

    private static BasalDeliveryHistoryLog basalDelivery(String hex) throws DecoderException {
        return (BasalDeliveryHistoryLog) HistoryLogParser.parse(Hex.decodeHex(hex));
    }

    @Test
    public void testControlIQCycle_x2AutoBolusCycle() throws DecoderException {
        // Maintainer's t:slim X2 (2024), Control-IQ on with CGM. Header high nibble 0.
        ControlIQCycleHistoryLog expected = new ControlIQCycleHistoryLog(
                // long pumpTimeSec, long sequenceNum, int basalIncreaseMilliunits, int cycleInsulinMilliunits, int predictedGlucoseX100, int programmedBasalRate, int netIobMilliunits, int glucoseEstimate, int correctionFactor, int tdiEstimate, int hypoBrake, int sleepRamp, int upperTarget
                534192321L, 1050055L, 74, 913, 22517, 800, 2553, 180, 21, 82, 200, 0, 160
        );

        ControlIQCycleHistoryLog parsedRes = (ControlIQCycleHistoryLog) HistoryLogMessageTester.testSingle(
                "e300c120d71fc70510004a009103f5572003f909b41552c800a0",
                expected
        );
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());

        // BasalDelivery 91 s later: algorithm rate 1697, profile 800 mU/hr.
        BasalDeliveryHistoryLog basal = basalDelivery("17011c21d71fcb05100003000100a1062003a106ffff00000000");
        assertEquals(1697, basal.getAlgorithmRate());
        assertEquals(basal.getProfileBasalRate(), parsedRes.getProgrammedBasalRate());
        assertEquals((1697 - 800) / 12, parsedRes.getBasalIncreaseMilliunits());

        // Control-IQ automatic correction bolus started 119 s later, requesting 772 mU.
        BolusDeliveryHistoryLog bolus = (BolusDeliveryHistoryLog) HistoryLogParser.parse(
                Hex.decodeHex("18013821d71fd8051000110e0109071104030000040300000000"));
        assertEquals(7, bolus.getBolusSourceId());
        assertEquals(772, bolus.getRequestedNow());
        assertEquals(1697 / 12 + 772, parsedRes.getCycleInsulinMilliunits());

        assertEquals(225.17, parsedRes.getPredictedGlucoseMgdl(), 1e-9);
        assertEquals(expectedHypoBrake(parsedRes.getPredictedGlucoseMgdl()), parsedRes.getHypoBrake());
        assertEquals(1.0, parsedRes.getHypoBrakeFraction(), 1e-9);
    }

    @Test
    public void testControlIQCycle_x2SleepRampAndBrake() throws DecoderException {
        // Maintainer's t:slim X2 (2023), Control-IQ on with CGM, about 80 minutes into sleep mode. Header high nibble 0.
        ControlIQCycleHistoryLog expected = new ControlIQCycleHistoryLog(
                497757999L, 1904228L, 0, 38, 10275, 800, 158, 119, 30, 56, 116, 83, 120
        );

        ControlIQCycleHistoryLog parsedRes = (ControlIQCycleHistoryLog) HistoryLogMessageTester.testSingle(
                "e3002f2fab1d640e1d0000002600232820039e00771e38745378",
                expected
        );
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());

        assertEquals(102.75, parsedRes.getPredictedGlucoseMgdl(), 1e-9);
        assertEquals(expectedHypoBrake(parsedRes.getPredictedGlucoseMgdl()), parsedRes.getHypoBrake());
        assertEquals(0.58, parsedRes.getHypoBrakeFraction(), 1e-9);

        // BasalDelivery 65 s later: Control-IQ cut the 800 mU/hr profile rate to 467.
        BasalDeliveryHistoryLog basal = basalDelivery("1701702fab1d680e1d0003000000d3012003d301ffff00000000");
        assertEquals(467, basal.getAlgorithmRate());
        assertEquals(basal.getProfileBasalRate(), parsedRes.getProgrammedBasalRate());
        assertEquals(467 / 12, parsedRes.getCycleInsulinMilliunits());
        assertEquals(0, parsedRes.getBasalIncreaseMilliunits());

        assertEquals(83, parsedRes.getSleepRamp());
        assertEquals(120, parsedRes.getUpperTarget());
    }

    @Test
    public void testControlIQCycle_mobiControlIqOn() throws DecoderException {
        // Maintainer's Tandem Mobi (Control-IQ v7.7.0.1), Control-IQ on with CGM, just after sleep mode started. Header high nibble 1.
        ControlIQCycleHistoryLog expected = (ControlIQCycleHistoryLog) new ControlIQCycleHistoryLog(
                539480071L, 106155L, 0, 65, 11101, 800, 793, 136, 25, 71, 196, 16, 159
        ).withHeaderHighNibble(1);

        ControlIQCycleHistoryLog parsedRes = (ControlIQCycleHistoryLog) HistoryLogMessageTester.testSingle(
                "e31007d02720ab9e0100000041005d2b20031903881947c4109f",
                expected
        );
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());

        assertEquals(expectedHypoBrake(parsedRes.getPredictedGlucoseMgdl()), parsedRes.getHypoBrake());

        // BasalDelivery 30 s later.
        BasalDeliveryHistoryLog basal = basalDelivery("171125d02720ae9e010003000000140320031403ffff00000000");
        assertEquals(788, basal.getAlgorithmRate());
        assertEquals(basal.getProfileBasalRate(), parsedRes.getProgrammedBasalRate());
        assertEquals(788 / 12, parsedRes.getCycleInsulinMilliunits());

        assertEquals(16, parsedRes.getSleepRamp());
        assertEquals(159, parsedRes.getUpperTarget());
    }

    @Test
    public void testControlIQCycle_mobiControlIqOffNoCgm() throws DecoderException {
        // Maintainer's Tandem Mobi (7.9.0.2), Control-IQ off, no CGM paired to the pump, sleep mode, temp rate running. Header high nibble 1.
        ControlIQCycleHistoryLog expected = (ControlIQCycleHistoryLog) new ControlIQCycleHistoryLog(
                590115570L, 671012L, 0, 19, 8065, 2500, 3956, 89, 29, 60, 18, 200, 120
        ).withHeaderHighNibble(1);

        ControlIQCycleHistoryLog parsedRes = (ControlIQCycleHistoryLog) HistoryLogMessageTester.testSingle(
                "e310f2722c23243d0a0000001300811fc409740f591d3c12c878",
                expected
        );
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());

        // BasalDelivery 30 s later: temp rate 2500 mU/hr on a 1000 mU/hr profile.
        BasalDeliveryHistoryLog basal = basalDelivery("171110732c23273d0a0002000300c409e803ffffc40900000000");
        assertEquals(2500, basal.getTempRate());
        assertEquals(basal.getTempRate(), parsedRes.getProgrammedBasalRate());
        assertEquals(0, parsedRes.getBasalIncreaseMilliunits());

        assertEquals(80.65, parsedRes.getPredictedGlucoseMgdl(), 1e-9);
        assertEquals(expectedHypoBrake(parsedRes.getPredictedGlucoseMgdl()), parsedRes.getHypoBrake());
        double f = 1.509 * (Math.pow(Math.log(80.65), 1.084) - 5.381);
        double brake = 1 / (1 + 2.5 * 10 * f * f);
        assertEquals((int) Math.floor(2500 / 12.0 * brake), parsedRes.getCycleInsulinMilliunits());

        // AATdiEstChange (opcode 332) written in the same second just before it, estimate 60.109 U/day.
        byte[] tdiEstChange = Hex.decodeHex("4c11f2722c23233d0a00956f7042698f70423c00000000000000");
        float tdi = Bytes.readFloat(tdiEstChange, 10);
        assertEquals((int) Math.floor(tdi), parsedRes.getTdiEstimate());
        assertEquals((int) Math.floor(1800 / tdi), parsedRes.getCorrectionFactor());

        assertEquals(200, parsedRes.getSleepRamp());
        assertEquals(120, parsedRes.getUpperTarget());
    }

    @Test
    public void testControlIQCycle_mobiBrakeZeroBelow70() throws DecoderException {
        // Maintainer's other Tandem Mobi (7.9.0.1), Control-IQ off, no CGM paired to the pump, normal mode. Header high nibble 1.
        ControlIQCycleHistoryLog expected = (ControlIQCycleHistoryLog) new ControlIQCycleHistoryLog(
                585032868L, 623097L, 0, 0, 3096, 1500, 439, 42, 21, 82, 0, 0, 160
        ).withHeaderHighNibble(1);

        ControlIQCycleHistoryLog parsedRes = (ControlIQCycleHistoryLog) HistoryLogMessageTester.testSingle(
                "e310a4e4de22f981090000000000180cdc05b7012a15520000a0",
                expected
        );
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());

        // BasalDelivery 30 s later: temp rate 1500 mU/hr.
        BasalDeliveryHistoryLog basal = basalDelivery("1711c2e4de22fb81090002000300dc05e803ffffdc0500000000");
        assertEquals(basal.getTempRate(), parsedRes.getProgrammedBasalRate());

        // Predicted 30.96 mg/dL is below 70, so the brake is fully on and the cycle's insulin is 0.
        assertEquals(30.96, parsedRes.getPredictedGlucoseMgdl(), 1e-9);
        assertEquals(0, expectedHypoBrake(parsedRes.getPredictedGlucoseMgdl()));
        assertEquals(0, parsedRes.getHypoBrake());
        assertEquals(0.0, parsedRes.getHypoBrakeFraction(), 1e-9);
        assertEquals(0, parsedRes.getCycleInsulinMilliunits());

        assertEquals(0, parsedRes.getSleepRamp());
        assertEquals(160, parsedRes.getUpperTarget());
    }
}
