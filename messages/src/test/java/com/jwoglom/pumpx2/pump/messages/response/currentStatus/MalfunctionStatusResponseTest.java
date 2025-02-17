package com.jwoglom.pumpx2.pump.messages.response.currentStatus;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;

import static org.junit.Assert.assertEquals;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.messages.bluetooth.CharacteristicUUID;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class MalfunctionStatusResponseTest {
    // 12-0x2071 - stuck vibrator motor
    @Test
    public void testMalfunction2StatusResponse_12_0x2071_stuckVibratorMotor() throws DecoderException {
        MalfunctionStatusResponse expected = new MalfunctionStatusResponse(
            12, 0x2071, new byte[]{4, -1, -1}
        );

        MalfunctionStatusResponse parsedRes = (MalfunctionStatusResponse) MessageTester.test(
                "000779070b0c0000007120000004ffffd6a6",
                7,
                1,
                CharacteristicUUID.CURRENT_STATUS_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
        assertEquals(12, parsedRes.getCodeA());
        assertEquals(0x2071, parsedRes.getCodeB());
        assertEquals("12-0x2071", parsedRes.getErrorString());
    }

    // 3-0x2026 - unknown
    @Test
    public void testMalfunction2StatusResponse_3_0x2026_unknown() throws DecoderException {
        MalfunctionStatusResponse expected = new MalfunctionStatusResponse(
                3, 8230, new byte[]{2,-1,-1}
        );

        MalfunctionStatusResponse parsedRes = (MalfunctionStatusResponse) MessageTester.test(
                "001079100b030000002620000002ffff2ab0",
                16,
                1,
                CharacteristicUUID.CURRENT_STATUS_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
        assertEquals(3, parsedRes.getCodeA());
        assertEquals(0x2026, parsedRes.getCodeB());
        assertEquals("3-0x2026", parsedRes.getErrorString());
    }
}