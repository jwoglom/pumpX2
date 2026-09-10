package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;
import static org.junit.Assert.assertEquals;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class PumpingSuspendedHistoryLogTest {
    @Test
    public void testPumpingSuspendedHistoryLog1() throws DecoderException {
        PumpingSuspendedHistoryLog expected = new PumpingSuspendedHistoryLog(
            // long preSuspendState, int insulinAmount, int reason, int rpaTimeout
                106, 31, 0, 0
        );

        PumpingSuspendedHistoryLog parsedRes = (PumpingSuspendedHistoryLog) HistoryLogMessageTester.testSingleIgnoringBaseFields(
                "0b002b58951a06c602006a0000001f0000000000000000000000",
                expected
        );
        assertEquals(PumpingSuspendedHistoryLog.SuspendReason.USER_ABORTED, parsedRes.getReason());
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }

    @Test
    public void testPumpingSuspendedHistoryLog2() throws DecoderException {
        PumpingSuspendedHistoryLog expected = new PumpingSuspendedHistoryLog(
                // long preSuspendState, int insulinAmount, int reason, int rpaTimeout
                106, 180, 0, 0
        );

        PumpingSuspendedHistoryLog parsedRes = (PumpingSuspendedHistoryLog) HistoryLogMessageTester.testSingleIgnoringBaseFields(
                "0b002fe5951a6dc902006a000000b40000000000000000000000",
                expected
        );
        assertEquals(PumpingSuspendedHistoryLog.SuspendReason.USER_ABORTED, parsedRes.getReason());
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }

    @Test
    public void testPumpingSuspendedHistoryLog3() throws DecoderException {
        PumpingSuspendedHistoryLog expected = new PumpingSuspendedHistoryLog(
                // long preSuspendState, int insulinAmount, int reason, int rpaTimeout
                106, 180, 0, 0
        );

        PumpingSuspendedHistoryLog parsedRes = (PumpingSuspendedHistoryLog) HistoryLogMessageTester.testSingleIgnoringBaseFields(
                "0b00bb40971ae3d202006a000000b40000000000000000000000",
                expected
        );
        assertEquals(PumpingSuspendedHistoryLog.SuspendReason.USER_ABORTED, parsedRes.getReason());
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }

    @Test
    public void testPumpingSuspendedHistoryLogAlarmReasonWithRpaTimeout() throws DecoderException {
        PumpingSuspendedHistoryLog expected = new PumpingSuspendedHistoryLog(
                // long pumpTimeSec, long sequenceNum, long preSuspendState, int insulinAmount, int reason, int rpaTimeout
                587315465L, 744310L, 106, 150, 1, 15
        );

        PumpingSuspendedHistoryLog parsedRes = (PumpingSuspendedHistoryLog) HistoryLogMessageTester.testSingle(
                "0b1009b90123765b0b006a0000009600010f0000000000000000",
                expected
        );
        assertEquals(587315465L, parsedRes.getPumpTimeSec());
        assertEquals(744310L, parsedRes.getSequenceNum());
        assertEquals(106L, parsedRes.getPreSuspendState());
        assertEquals(150, parsedRes.getInsulinAmount());
        assertEquals(PumpingSuspendedHistoryLog.SuspendReason.ALARM, parsedRes.getReason());
        assertEquals(15, parsedRes.getRpaTimeout());
        assertHexEquals(expected.getCargo(), withoutSourceNibble(parsedRes.getCargo()));
    }

    @Test
    public void testPumpingSuspendedHistoryLogUserAbortedWithRpaTimeout() throws DecoderException {
        PumpingSuspendedHistoryLog expected = new PumpingSuspendedHistoryLog(
                // long pumpTimeSec, long sequenceNum, long preSuspendState, int insulinAmount, int reason, int rpaTimeout
                587387440L, 748755L, 106, 105, 0, 15
        );

        PumpingSuspendedHistoryLog parsedRes = (PumpingSuspendedHistoryLog) HistoryLogMessageTester.testSingle(
                "0b1030d20223d36c0b006a0000006900000f0000000000000000",
                expected
        );
        assertEquals(587387440L, parsedRes.getPumpTimeSec());
        assertEquals(748755L, parsedRes.getSequenceNum());
        assertEquals(106L, parsedRes.getPreSuspendState());
        assertEquals(105, parsedRes.getInsulinAmount());
        assertEquals(PumpingSuspendedHistoryLog.SuspendReason.USER_ABORTED, parsedRes.getReason());
        assertEquals(15, parsedRes.getRpaTimeout());
        assertHexEquals(expected.getCargo(), withoutSourceNibble(parsedRes.getCargo()));
    }

    // Observed on a Tandem Mobi driven by Trio (Control-IQ off, no CGM paired), Sept 2026 BLE capture.
    // InsulinStatusResponse.currentInsulinAmount polled within two seconds of each record returned
    // the same insulinAmount (150 and 8), and the suspension that followed the first record ran
    // past 15 minutes and raised RESUME_PUMP_ALARM / RESUME_PUMP_ALARM2 at 15 min 1 s.
    @Test
    public void testPumpingSuspendedHistoryLogMobiReservoir150() throws DecoderException {
        PumpingSuspendedHistoryLog expected = (PumpingSuspendedHistoryLog) new PumpingSuspendedHistoryLog(
                // long pumpTimeSec, long sequenceNum, long preSuspendState, int insulinAmount, int reason, int rpaTimeout
                589553316L, 641936L, 106, 150, 0, 15
        ).withHeaderHighNibble(1);

        PumpingSuspendedHistoryLog parsedRes = (PumpingSuspendedHistoryLog) HistoryLogMessageTester.testSingle(
                "0b10a4de232390cb09006a0000009600000f0000000000000000",
                expected
        );
        assertEquals(589553316L, parsedRes.getPumpTimeSec());
        assertEquals(641936L, parsedRes.getSequenceNum());
        assertEquals(106L, parsedRes.getPreSuspendState());
        assertEquals(150, parsedRes.getInsulinAmount());
        assertEquals(0, parsedRes.getReasonId());
        assertEquals(PumpingSuspendedHistoryLog.SuspendReason.USER_ABORTED, parsedRes.getReason());
        assertEquals(15, parsedRes.getRpaTimeout());
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }

    @Test
    public void testPumpingSuspendedHistoryLogMobiReservoir8() throws DecoderException {
        PumpingSuspendedHistoryLog expected = (PumpingSuspendedHistoryLog) new PumpingSuspendedHistoryLog(
                // long pumpTimeSec, long sequenceNum, long preSuspendState, int insulinAmount, int reason, int rpaTimeout
                589745484L, 651659L, 106, 8, 0, 15
        ).withHeaderHighNibble(1);

        PumpingSuspendedHistoryLog parsedRes = (PumpingSuspendedHistoryLog) HistoryLogMessageTester.testSingle(
                "0b104ccd26238bf109006a0000000800000f0000000000000000",
                expected
        );
        assertEquals(589745484L, parsedRes.getPumpTimeSec());
        assertEquals(651659L, parsedRes.getSequenceNum());
        assertEquals(106L, parsedRes.getPreSuspendState());
        assertEquals(8, parsedRes.getInsulinAmount());
        assertEquals(0, parsedRes.getReasonId());
        assertEquals(PumpingSuspendedHistoryLog.SuspendReason.USER_ABORTED, parsedRes.getReason());
        assertEquals(15, parsedRes.getRpaTimeout());
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }

    /**
     * The upper nibble of the second typeId byte identifies the source of the log entry and is
     * not part of the typeId itself (parseBase masks it off with & 4095). buildCargo() has no
     * source to emit, so it is masked out of the parsed cargo before comparing the two.
     */
    static byte[] withoutSourceNibble(byte[] cargo) {
        byte[] out = cargo.clone();
        out[1] = (byte) (out[1] & 0x0F);
        return out;
    }
}
