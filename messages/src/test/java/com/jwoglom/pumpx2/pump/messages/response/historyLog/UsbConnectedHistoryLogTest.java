package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.messages.bluetooth.CharacteristicUUID;
import com.jwoglom.pumpx2.pump.messages.response.historyLog.UsbConnectedHistoryLog;

import com.google.common.collect.ImmutableList;
import org.apache.commons.codec.DecoderException;
import org.junit.Ignore;
import org.junit.Test;
public class UsbConnectedHistoryLogTest {
    @Test
    public void testUsbConnectedHistoryLog1() throws DecoderException {
        UsbConnectedHistoryLog expected = new UsbConnectedHistoryLog(
            // float negotiatedCurrentmA
                100.0F
        );

        UsbConnectedHistoryLog parsedRes = (UsbConnectedHistoryLog) HistoryLogMessageTester.testSingleIgnoringBaseFields(
                "240045e5951a71c902000000c842000000000000000000000000",
                expected
        );
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }
}