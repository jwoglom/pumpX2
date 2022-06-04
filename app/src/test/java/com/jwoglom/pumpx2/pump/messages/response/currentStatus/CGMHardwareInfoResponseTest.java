package com.jwoglom.pumpx2.pump.messages.response.currentStatus;

import static org.junit.Assert.assertEquals;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.bluetooth.CharacteristicUUID;
import com.jwoglom.pumpx2.pump.messages.response.currentStatus.CGMHardwareInfoResponse;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class CGMHardwareInfoResponseTest {
    @Test
    public void testCGMHardwareInfo() throws DecoderException {
        CGMHardwareInfoResponse expected = new CGMHardwareInfoResponse("8RR239", 0);

        CGMHardwareInfoResponse parsedRes = (CGMHardwareInfoResponse) MessageTester.test(
                "00036103113852523233390000000000000000000000a2a3",
                3,
                2,
                CharacteristicUUID.CURRENT_STATUS_CHARACTERISTICS,
                expected
        );

        assertEquals(expected.getHardwareInfoString(), parsedRes.getHardwareInfoString());
        assertEquals(expected.getLastByte(), parsedRes.getLastByte());
    }
}
