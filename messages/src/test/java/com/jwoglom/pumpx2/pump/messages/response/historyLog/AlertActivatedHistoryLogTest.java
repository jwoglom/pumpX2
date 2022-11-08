package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;

import static org.junit.Assert.assertEquals;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.messages.bluetooth.CharacteristicUUID;
import com.jwoglom.pumpx2.pump.messages.response.currentStatus.AlertStatusResponse;
import com.jwoglom.pumpx2.pump.messages.response.historyLog.AlertActivatedHistoryLog;

import com.google.common.collect.ImmutableList;
import org.apache.commons.codec.DecoderException;
import org.junit.Ignore;
import org.junit.Test;
public class AlertActivatedHistoryLogTest {
    @Test
    public void testAlertActivatedHistoryLog1() throws DecoderException {
        AlertActivatedHistoryLog expected = new AlertActivatedHistoryLog(
            // long alertId
                51
        );

        AlertActivatedHistoryLog parsedRes = (AlertActivatedHistoryLog) HistoryLogMessageTester.testSingle(
                "040024c0981aadde020033000000000000000000000000800044",
                expected
        );
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
        assertEquals(AlertStatusResponse.AlertResponseType.DEFAULT_ALERT_51, parsedRes.getAlertResponseType());
    }

    @Test
    public void testAlertActivatedHistoryLog2() throws DecoderException {
        AlertActivatedHistoryLog expected = new AlertActivatedHistoryLog(
                // long alertId
                48
        );

        AlertActivatedHistoryLog parsedRes = (AlertActivatedHistoryLog) HistoryLogMessageTester.testSingle(
                "040022df971aced8020030000000af2100000000000000000000",
                expected
        );
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
        assertEquals(AlertStatusResponse.AlertResponseType.CGM_UNAVAILABLE, parsedRes.getAlertResponseType());
    }

    @Test
    public void testAlertActivatedHistoryLog3() throws DecoderException {
        AlertActivatedHistoryLog expected = new AlertActivatedHistoryLog(
                // long alertId
                11
        );

        AlertActivatedHistoryLog parsedRes = (AlertActivatedHistoryLog) HistoryLogMessageTester.testSingle(
                "0400b5f4981a66e002000b000000ba2000000000000000000000",
                expected
        );
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
        assertEquals(AlertStatusResponse.AlertResponseType.INCOMPLETE_BOLUS_ALERT, parsedRes.getAlertResponseType());
    }

    @Test
    public void testAlertActivatedHistoryLog4() throws DecoderException {
        AlertActivatedHistoryLog expected = new AlertActivatedHistoryLog(
                // long alertId
                0
        );

        AlertActivatedHistoryLog parsedRes = (AlertActivatedHistoryLog) HistoryLogMessageTester.testSingle(
                "0400304e951a90c5020000000000322000006600000098c6ad43",
                expected
        );
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
        assertEquals(AlertStatusResponse.AlertResponseType.LOW_INSULIN_ALERT, parsedRes.getAlertResponseType());
    }
}