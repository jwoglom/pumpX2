package com.jwoglom.pumpx2.pump.messages.response.currentStatus;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;

import static org.junit.Assert.assertEquals;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.messages.bluetooth.CharacteristicUUID;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class OtherNotification2StatusResponseTest {
    @Test
    public void testMalfunctionStatusResponse_none() throws DecoderException {
        OtherNotification2StatusResponse expected = new OtherNotification2StatusResponse(
            0, 0
        );

        OtherNotification2StatusResponse parsedRes = (OtherNotification2StatusResponse) MessageTester.test(
                "00047704080000000000000000eac2",
                4,
                1,
                CharacteristicUUID.CURRENT_STATUS_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
        assertEquals(0, parsedRes.getCodeA());
        assertEquals(0, parsedRes.getCodeB());
    }


    @Test
    public void testMalfunctionStatusResponse_1() throws DecoderException {
        OtherNotification2StatusResponse expected = new OtherNotification2StatusResponse(
                4096, 0
        );

        OtherNotification2StatusResponse parsedRes = (OtherNotification2StatusResponse) MessageTester.test(
                "00ec77ec080010000000000000f910",
                -20,
                1,
                CharacteristicUUID.CURRENT_STATUS_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
        assertEquals(4096, parsedRes.getCodeA());
        assertEquals(0, parsedRes.getCodeB());
    }
}