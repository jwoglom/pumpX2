package com.jwoglom.pumpx2.pump.messages.request.currentStatus;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.bluetooth.CharacteristicUUID;
import com.jwoglom.pumpx2.pump.messages.request.currentStatus.HistoryLogRequest;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class HistoryLogRequestTest {
    @Test
    public void testHistoryLogRequest() throws DecoderException {
        // empty cargo
        HistoryLogRequest expected = new HistoryLogRequest();

        HistoryLogRequest parsedReq = (HistoryLogRequest) MessageTester.test(
                "00033c030500000000fa83b3",
                3,
                1,
                CharacteristicUUID.CURRENT_STATUS_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedReq.getCargo());
    }
}