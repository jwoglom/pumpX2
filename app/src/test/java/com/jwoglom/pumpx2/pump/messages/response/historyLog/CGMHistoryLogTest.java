package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.bluetooth.CharacteristicUUID;
import com.jwoglom.pumpx2.pump.messages.response.historyLog.CGMHistoryLog;

import com.google.common.collect.ImmutableList;
import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class CGMHistoryLogTest {
    @Test
    public void testCGMHistoryLog1() throws DecoderException {
        CGMHistoryLog expected = new CGMHistoryLog(
            // int glucoseValueStatus, int cgmDataType, int rate, int algorithmState, int rssi, int currentGlucoseDisplayValue, long timeStampSeconds, int egvInfoBitmask, int interval
                0, 1, -2, 6, -89, 112, 446191545L, 481, 0
        );

        CGMHistoryLog parsedRes = (CGMHistoryLog) HistoryLogMessageTester.testSingle(
                "0001b957981a35dc0200000001fe06a77000b957981ae1010001",
                expected
        );
    }

    @Test
    public void testCGMHistoryLog2() throws DecoderException {
        CGMHistoryLog expected = new CGMHistoryLog(
                // int glucoseValueStatus, int cgmDataType, int rate, int algorithmState, int rssi, int currentGlucoseDisplayValue, long timeStampSeconds, int egvInfoBitmask, int interval
                0, 1, -3, 6, -67, 81, 446115345L, 481, 0
        );

        CGMHistoryLog parsedRes = (CGMHistoryLog) HistoryLogMessageTester.testSingle(
                "0001112e971a5ed20200000001fd06bd5100112e971ae1010001",
                expected
        );
    }

    @Test
    public void testCGMHistoryLog3() throws DecoderException {
        CGMHistoryLog expected = new CGMHistoryLog(
                // int glucoseValueStatus, int cgmDataType, int rate, int algorithmState, int rssi, int currentGlucoseDisplayValue, long timeStampSeconds, int egvInfoBitmask, int interval
                0, 1, -12, 6, -82, 78, 446090145L, 481, 0
        );

        CGMHistoryLog parsedRes = (CGMHistoryLog) HistoryLogMessageTester.testSingle(
                "0001a1cb961a07d00200000001f406ae4e00a1cb961ae1010001",
                expected
        );
    }

    @Test
    public void testCGMHistoryLog4() throws DecoderException {
        CGMHistoryLog expected = new CGMHistoryLog(
                // int glucoseValueStatus, int cgmDataType, int rate, int algorithmState, int rssi, int currentGlucoseDisplayValue, long timeStampSeconds, int egvInfoBitmask, int interval
                0, 1, 13, 6, -87, 145, 445997146L, 481, 0
        );

        CGMHistoryLog parsedRes = (CGMHistoryLog) HistoryLogMessageTester.testSingle(
                "00015a60951a71c602000000010d06a991005a60951ae1010001",
                expected
        );
    }
}