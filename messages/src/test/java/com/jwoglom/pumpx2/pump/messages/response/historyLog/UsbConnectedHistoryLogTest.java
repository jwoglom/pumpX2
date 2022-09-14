package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.messages.bluetooth.CharacteristicUUID;
import com.jwoglom.pumpx2.pump.messages.response.historyLog.UsbConnectedHistoryLog;

import com.google.common.collect.ImmutableList;
import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class UsbConnectedHistoryLogTest {
    @Test
    public void testUsbConnectedHistoryLog() throws DecoderException {
        UsbConnectedHistoryLog expected = new UsbConnectedHistoryLog(
            // float negotiatedCurrentmA
        );

        UsbConnectedHistoryLog parsedRes = (UsbConnectedHistoryLog) HistoryLogMessageTester.testSingle(
                "xxxx",
                expected
        );
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }
}