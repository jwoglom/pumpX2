package com.jwoglom.pumpx2.pump.messages.response.currentStatus;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.bluetooth.CharacteristicUUID;
import com.jwoglom.pumpx2.pump.messages.response.currentStatus.HistoryLogStreamResponse;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.junit.Ignore;
import org.junit.Test;

public class HistoryLogStreamResponseTest {
    @Test
    public void testHistoryLogStreamResponse() throws DecoderException {
        HistoryLogStreamResponse expected = new HistoryLogStreamResponse(
            // int numberOfHistoryLogs, byte[] streamBytes
                1, new byte[]{4,90,0,-128,13,-108,26,0,0,0,0,0,0,0,0,-102,-35,32,2,0,0,0,0,0,0,0,0}
        );

        HistoryLogStreamResponse parsedRes = (HistoryLogStreamResponse) MessageTester.test(
                "000081001c01045a00800d941a00000000000000009add200200000000000000004834",
                0,
                2,
                CharacteristicUUID.CURRENT_STATUS_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }
}