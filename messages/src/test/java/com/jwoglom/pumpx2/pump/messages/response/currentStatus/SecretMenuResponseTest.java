package com.jwoglom.pumpx2.pump.messages.response.currentStatus;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;
import static org.junit.Assert.assertEquals;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.messages.bluetooth.CharacteristicUUID;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class SecretMenuResponseTest {
    @Test
    public void testSecretMenuResponseCapturedPayload() throws DecoderException {
        SecretMenuResponse expected = new SecretMenuResponse(
            // bytes 0..3: timeOfLastConnection (uint32 LE)
            // bytes 4..7: reserved/unknown value
            573876764L, 1966080L
        );

        SecretMenuResponse parsedRes = (SecretMenuResponse) MessageTester.test(
            "0017bd17081caa342200001e00d577",
            23,
            1,
            CharacteristicUUID.CURRENT_STATUS_CHARACTERISTICS,
            expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
        assertEquals(573876764L, parsedRes.getTimeOfLastConnectionTimestampSeconds());
        assertEquals(1966080L, parsedRes.getReservedValue());
    }
}
