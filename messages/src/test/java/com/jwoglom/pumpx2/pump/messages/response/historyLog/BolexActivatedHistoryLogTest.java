package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.messages.bluetooth.CharacteristicUUID;
import com.jwoglom.pumpx2.pump.messages.response.historyLog.BolexActivatedHistoryLog;

import java.util.Arrays;
import org.apache.commons.codec.DecoderException;
import org.junit.Ignore;
import org.junit.Test;
@Ignore("needs historyLog sample")
public class BolexActivatedHistoryLogTest {
    @Test
    public void testBolexActivatedHistoryLog() throws DecoderException {
        BolexActivatedHistoryLog expected = new BolexActivatedHistoryLog(
            // int bolusId, float iob, float bolexSize
        );

        BolexActivatedHistoryLog parsedRes = (BolexActivatedHistoryLog) HistoryLogMessageTester.testSingle(
                "xxxx",
                expected
        );
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }
}