package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.jwoglom.pumpx2.pump.messages.response.currentStatus.LastBolusStatusAbstractResponse;

import org.apache.commons.codec.DecoderException;
import com.jwoglom.pumpx2.shared.Hex;
import org.junit.Test;

import java.time.Instant;

public class BolusCompletedHistoryLogTest {
    @Test
    public void testBolusCompletedHistoryLog() throws DecoderException {
        BolusCompletedHistoryLog expected = new BolusCompletedHistoryLog(
            // long pumpTimeSec, long sequenceNum, int completionStatus, int bolusId, float iob, float insulinDelivered, float insulinRequested
            446158750L, 186480L, 3, 1057, 3.652852F, 1.7869551F, 1.7869551F
        );

        BolusCompletedHistoryLog parsedRes = (BolusCompletedHistoryLog) HistoryLogMessageTester.testSingle(
                "14009ed7971a70d802000300210454c86940f2bae43ff2bae43f",
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
        assertEquals(Instant.parse("2022-02-19T20:59:10Z"), parsedRes.getPumpTimeSecInstant());
    }

    @Test
    public void testBolusCompletedHistoryLog_mismatchingFloats() throws DecoderException {
        // BolusCompletedHistoryLog[bolusId=1047,completionStatus=3,insulinDelivered=0.89000005,insulinRequested=0.89,iob=1.0332867,cargo={20,0,0,91,-105,26,-7,-45,2,0,3,0,23,4,-67,66,-124,63,11,-41,99,63,10,-41,99,63},pumpTimeSec=446126848,sequenceNum=185337]
        BolusCompletedHistoryLog expected = new BolusCompletedHistoryLog(
                // long pumpTimeSec, long sequenceNum, int completionStatus, int bolusId, float iob, float insulinDelivered, float insulinRequested
                446126848L, 185337L, 3, 1047, 1.0332867F, 0.89000005F, 0.89F
        );

        BolusCompletedHistoryLog parsedRes = (BolusCompletedHistoryLog) HistoryLogMessageTester.testSingle(
                "1400005b971af9d3020003001704bd42843f0bd7633f0ad7633f",
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
        assertEquals(Instant.parse("2022-02-19T12:07:28Z"), parsedRes.getPumpTimeSecInstant());
    }

    @Test
    public void testBolusCompletedHistoryLog_mobiRemoteBolus() throws DecoderException {
        // Observed on a Tandem Mobi driven by Trio (Control-IQ off, no CGM paired), Sept 2026 BLE capture.
        // Remote (BLE) bolus id 2903, 0.15u requested and delivered in full. Header high nibble is 1.
        // In 156/156 records from this capture, completionStatus@10 was 3 (COMPLETE) and bolusId@12
        // matched the BolusActivated/BolusDelivery records for the same bolus, confirming this layout.
        BolusCompletedHistoryLog expected = new BolusCompletedHistoryLog(
                // long pumpTimeSec, long sequenceNum, int completionStatus, int bolusId, float iob, float insulinDelivered, float insulinRequested, int headerHighNibble
                589526849L, 640762L, 3, 2903,
                Float.intBitsToFloat(0x3fb21f75), // ~1.3916
                Float.intBitsToFloat(0x3e19999a), // 0.15
                Float.intBitsToFloat(0x3e19999a), // 0.15
                1
        );

        BolusCompletedHistoryLog parsedRes = (BolusCompletedHistoryLog) HistoryLogMessageTester.testSingle(
                "141041772323fac609000300570b751fb23f9a99193e9a99193e",
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
        assertEquals(Instant.parse("2026-09-06T05:27:29Z"), parsedRes.getPumpTimeSecInstant());
        assertEquals(3, parsedRes.getCompletionStatusId());
        assertEquals(LastBolusStatusAbstractResponse.BolusStatus.COMPLETE, parsedRes.getCompletionStatus());
        assertEquals(2903, parsedRes.getBolusId());
        assertEquals(Float.intBitsToFloat(0x3fb21f75), parsedRes.getIob(), 0.0F);
        assertEquals(Float.intBitsToFloat(0x3e19999a), parsedRes.getInsulinRequested(), 0.0F);
        // delivered == requested bit-for-bit in all 156 observed records
        assertEquals(parsedRes.getInsulinRequested(), parsedRes.getInsulinDelivered(), 0.0F);
        assertTrue(Math.abs(parsedRes.getInsulinDelivered() - parsedRes.getInsulinRequested()) <= HistoryLog.INSULIN_FLOAT_EPSILON);
    }

    @Test
    public void testBolusCompletedHistoryLog_iobIsUnits() throws DecoderException {
        // Bolus 2903 from the same Mobi capture: BolusActivated logged iob ~1.2416u and a 0.15u
        // bolus; BolusCompleted 4 s later logs ~1.3916u, i.e. the IOB in units including the bolus.
        BolusActivatedHistoryLog activated = (BolusActivatedHistoryLog) HistoryLogParser.parse(
                Hex.decodeHex("37103d772323f2c60900570b000042ec9e3f9a99193e00000000"));
        BolusCompletedHistoryLog completed = (BolusCompletedHistoryLog) HistoryLogParser.parse(
                Hex.decodeHex("141041772323fac609000300570b751fb23f9a99193e9a99193e"));

        assertEquals(activated.getBolusId(), completed.getBolusId());
        assertEquals(1.3916F, completed.getIob(), 0.0001F);
        assertEquals(activated.getIob() + activated.getBolusSize(), completed.getIob(), 0.0001F);
    }

    @Test
    public void testBolusCompletedHistoryLog_cancelledOverBluetooth() throws DecoderException {
        // t:slim X2, official Tandem app, Aug 2022 BLE capture. The app sent CancelBolusRequest for
        // bolus 10678 (response: success) partway through a 0.76u bolus. The pump logged
        // completionStatus 0 (user aborted), not 4, with 0.36375u delivered.
        BolusCompletedHistoryLog expected = new BolusCompletedHistoryLog(
                // long pumpTimeSec, long sequenceNum, int completionStatus, int bolusId, float iob, float insulinDelivered, float insulinRequested
                461710238L, 28178L, 0, 10678,
                Float.intBitsToFloat(0x3eba3d71), Float.intBitsToFloat(0x3eba3d71), Float.intBitsToFloat(0x3f428f5c)
        );

        BolusCompletedHistoryLog parsedRes = (BolusCompletedHistoryLog) HistoryLogMessageTester.testSingle(
                "14009e23851b126e00000000b629713dba3e713dba3e5c8f423f",
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
        assertEquals(LastBolusStatusAbstractResponse.BolusStatus.STOPPED_USER_TERMINATED, parsedRes.getCompletionStatus());
        assertEquals(10678, parsedRes.getBolusId());
        assertEquals(0.36375F, parsedRes.getInsulinDelivered(), 0.00001F);
        assertEquals(0.76F, parsedRes.getInsulinRequested(), 0.00001F);
    }

    @Test
    public void testBolusCompletedHistoryLog_terminatedByAlarm() throws DecoderException {
        // Tandem Mobi, March 2025 BLE capture. The pump suspended with reason ALARM in the same
        // second and an occlusion alarm was acknowledged 22 s later; 1.89u of a 3u bolus had been
        // delivered. Header high nibble is 1.
        BolusCompletedHistoryLog expected = new BolusCompletedHistoryLog(
                // long pumpTimeSec, long sequenceNum, int completionStatus, int bolusId, float iob, float insulinDelivered, float insulinRequested, int headerHighNibble
                542918085L, 236019L, 1, 980,
                Float.intBitsToFloat(0x40fcb858), Float.intBitsToFloat(0x3ff264ca), 3.0F,
                1
        );

        BolusCompletedHistoryLog parsedRes = (BolusCompletedHistoryLog) HistoryLogMessageTester.testSingle(
                "1410c5455c20f39903000100d40358b8fc40ca64f23f00004040",
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
        assertEquals(LastBolusStatusAbstractResponse.BolusStatus.STOPPED_ALARM, parsedRes.getCompletionStatus());
        assertEquals(980, parsedRes.getBolusId());
        assertTrue(parsedRes.getInsulinDelivered() < parsedRes.getInsulinRequested());
    }
}
