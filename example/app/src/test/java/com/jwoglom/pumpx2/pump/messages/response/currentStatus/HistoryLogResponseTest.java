package com.jwoglom.pumpx2.pump.messages.response.currentStatus;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.bluetooth.CharacteristicUUID;
import com.jwoglom.pumpx2.pump.messages.response.currentStatus.HistoryLogResponse;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class HistoryLogResponseTest {
    @Test
    public void testHistoryLogResponse() throws DecoderException {
        HistoryLogResponse expected = new HistoryLogResponse(
            // int status, int streamId
                0, 1
        );

        HistoryLogResponse parsedRes = (HistoryLogResponse) MessageTester.test(
                "00033d0302000105d9",
                3,
                1,
                CharacteristicUUID.CURRENT_STATUS_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }
}