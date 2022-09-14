package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.messages.bluetooth.CharacteristicUUID;
import com.jwoglom.pumpx2.pump.messages.response.historyLog.UsbDisconnectedHistoryLog;

import com.google.common.collect.ImmutableList;
import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class UsbDisconnectedHistoryLogTest {
    @Test
    public void testUsbDisconnectedHistoryLog() throws DecoderException {
        UsbDisconnectedHistoryLog expected = new UsbDisconnectedHistoryLog(
            // float negotiatedCurrentMilliAmps
        );

        UsbDisconnectedHistoryLog parsedRes = (UsbDisconnectedHistoryLog) HistoryLogMessageTester.testSingle(
                "xxxx",
                expected
        );
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }
}