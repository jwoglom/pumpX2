package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.messages.bluetooth.CharacteristicUUID;
import com.jwoglom.pumpx2.pump.messages.response.historyLog.CartridgeFilledHistoryLog;

import com.google.common.collect.ImmutableList;
import org.apache.commons.codec.DecoderException;
import org.junit.Ignore;
import org.junit.Test;
@Ignore("needs historyLog sample")
public class CartridgeFilledHistoryLogTest {
    @Test
    public void testCartridgeFilledHistoryLog() throws DecoderException {
        CartridgeFilledHistoryLog expected = new CartridgeFilledHistoryLog(
            // long insulinDisplay, float insulinActual
        );

        CartridgeFilledHistoryLog parsedRes = (CartridgeFilledHistoryLog) HistoryLogMessageTester.testSingle(
                "xxxx",
                expected
        );
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }
}