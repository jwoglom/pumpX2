package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.messages.bluetooth.CharacteristicUUID;
import com.jwoglom.pumpx2.pump.messages.response.historyLog.IdpTimeDependentSegmentHistoryLog;

import java.util.Arrays;
import org.apache.commons.codec.DecoderException;
import org.junit.Ignore;
import org.junit.Test;
@Ignore("needs historyLog sample")
public class IdpTimeDependentSegmentHistoryLogTest {
    @Test
    public void testIdpTimeDependentSegmentHistoryLog() throws DecoderException {
        IdpTimeDependentSegmentHistoryLog expected = new IdpTimeDependentSegmentHistoryLog(
            // int idp, int status, int segmentIndex, int modificationType, int startTime, int basalRate, int isf, long targetBg, int carbRatio
        );

        IdpTimeDependentSegmentHistoryLog parsedRes = (IdpTimeDependentSegmentHistoryLog) HistoryLogMessageTester.testSingle(
                "xxxx",
                expected
        );
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }
}