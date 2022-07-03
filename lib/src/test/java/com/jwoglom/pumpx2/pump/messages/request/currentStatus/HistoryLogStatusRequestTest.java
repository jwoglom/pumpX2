package com.jwoglom.pumpx2.pump.messages.request.currentStatus;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.bluetooth.CharacteristicUUID;
import com.jwoglom.pumpx2.pump.messages.request.currentStatus.HistoryLogStatusRequest;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class HistoryLogStatusRequestTest {
    @Test
    public void testHistoryLogStatusRequest() throws DecoderException {
        // empty cargo
        HistoryLogStatusRequest expected = new HistoryLogStatusRequest();

        HistoryLogStatusRequest parsedReq = (HistoryLogStatusRequest) MessageTester.test(
                "00033a0300ab9b",
                3,
                1,
                CharacteristicUUID.CURRENT_STATUS_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedReq.getCargo());
    }
}