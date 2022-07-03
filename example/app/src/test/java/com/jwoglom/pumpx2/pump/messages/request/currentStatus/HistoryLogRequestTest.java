package com.jwoglom.pumpx2.pump.messages.request.currentStatus;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.bluetooth.CharacteristicUUID;
import com.jwoglom.pumpx2.pump.messages.request.currentStatus.HistoryLogRequest;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class HistoryLogRequestTest {
    @Test
    public void testHistoryLogRequest10Messages() throws DecoderException {
        HistoryLogRequest expected = new HistoryLogRequest(3950, 10);

        HistoryLogRequest parsedReq = (HistoryLogRequest) MessageTester.test(
                "00043c04056e0f00000a4247",
                4,
                1,
                CharacteristicUUID.CURRENT_STATUS_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedReq.getCargo());
    }
}