package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.bluetooth.CharacteristicUUID;
import com.jwoglom.pumpx2.pump.messages.response.historyLog.DateChangeResponse;

import com.google.common.collect.ImmutableList;
import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class DateChangeResponseTest {
    @Test
    public void testDateChangeResponse() throws DecoderException {
        DateChangeResponse expected = new DateChangeResponse(
            // long datePrior, long dateAfter, long rawRTCTime
        );

        HistoryLogStreamResponse parsedRes = HistoryLogMessageTester.test(
                "xxxx",
                0,
                ImmutableList.of(expected)
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());

    }
}