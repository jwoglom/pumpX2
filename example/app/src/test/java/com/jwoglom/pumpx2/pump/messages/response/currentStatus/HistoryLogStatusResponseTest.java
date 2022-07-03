package com.jwoglom.pumpx2.pump.messages.response.currentStatus;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.bluetooth.CharacteristicUUID;
import com.jwoglom.pumpx2.pump.messages.response.currentStatus.HistoryLogStatusResponse;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class HistoryLogStatusResponseTest {
    @Test
    public void testHistoryLogStatusResponse() throws DecoderException {
        HistoryLogStatusResponse expected = new HistoryLogStatusResponse(
            // long numEntries, long firstSequenceNum, long lastSequenceNum
                2756, 0, 2755
        );

        HistoryLogStatusResponse parsedRes = (HistoryLogStatusResponse) MessageTester.test(
                "00033b030cc40a000000000000c30a0000d84d",
                3,
                1,
                CharacteristicUUID.CURRENT_STATUS_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }
}