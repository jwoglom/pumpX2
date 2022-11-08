package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;

import static org.junit.Assert.assertEquals;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.messages.bluetooth.CharacteristicUUID;
import com.jwoglom.pumpx2.pump.messages.response.historyLog.PumpingSuspendedHistoryLog;

import com.google.common.collect.ImmutableList;
import org.apache.commons.codec.DecoderException;
import org.junit.Ignore;
import org.junit.Test;
public class PumpingSuspendedHistoryLogTest {
    @Test
    public void testPumpingSuspendedHistoryLog1() throws DecoderException {
        // 0.031 IOB
        PumpingSuspendedHistoryLog expected = new PumpingSuspendedHistoryLog(
            // int insulinAmount, int reason
                31, 0
        );

        PumpingSuspendedHistoryLog parsedRes = (PumpingSuspendedHistoryLog) HistoryLogMessageTester.testSingle(
                "0b002b58951a06c602006a0000001f0000000000000000000000",
                expected
        );
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
        assertEquals(PumpingSuspendedHistoryLog.SuspendReason.USER_ABORTED, parsedRes.getReason());
    }

    @Test
    public void testPumpingSuspendedHistoryLog2() throws DecoderException {
        // 0.180u IOB
        PumpingSuspendedHistoryLog expected = new PumpingSuspendedHistoryLog(
                // int insulinAmount, int reason
                180, 0
        );

        PumpingSuspendedHistoryLog parsedRes = (PumpingSuspendedHistoryLog) HistoryLogMessageTester.testSingle(
                "0b002fe5951a6dc902006a000000b40000000000000000000000",
                expected
        );
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
        assertEquals(PumpingSuspendedHistoryLog.SuspendReason.USER_ABORTED, parsedRes.getReason());
    }

    @Test
    public void testPumpingSuspendedHistoryLog3() throws DecoderException {
        // 0.180u IOB
        PumpingSuspendedHistoryLog expected = new PumpingSuspendedHistoryLog(
                // int insulinAmount, int reason
                180, 0
        );

        PumpingSuspendedHistoryLog parsedRes = (PumpingSuspendedHistoryLog) HistoryLogMessageTester.testSingle(
                "0b00bb40971ae3d202006a000000b40000000000000000000000",
                expected
        );
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
        assertEquals(PumpingSuspendedHistoryLog.SuspendReason.USER_ABORTED, parsedRes.getReason());
    }
}