package com.jwoglom.pumpx2.pump.messages.response.currentStatus;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.messages.bluetooth.CharacteristicUUID;

import org.apache.commons.codec.DecoderException;
import org.junit.Ignore;
import org.junit.Test;

public class MalfunctionStatusResponseTest {
    // button pressed down?? 23-0x20a7 02:02:05.041 CX2:MUA:L:BTResponseParser          com.jwoglom.controlx2                I  PARSED-MESSAGE(txId=124, CURRENT_STATUS):	MalfunctionStatusResponse[codeA=23,codeB=8359,errorString=23-0x20a7,remaining={2,-1,-1},cargo={23,0,0,0,-89,32,0,0,2,-1,-1}]


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

    // 3-0x2026 - unknown and seems to be ignorable
    @Test
    @Ignore("ignored in code")
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

    // 18-0x2077 -- appears on new pump
    @Test
    public void testMalfunction_ignorable_18_0x2077() throws DecoderException {
        MalfunctionStatusResponse expected = new MalfunctionStatusResponse(
                18, 8311, new byte[]{2, 3, 10}
        );

        // MalfunctionStatusResponse[codeA=18,codeB=8311,errorString=18-0x2077,remaining={2,3,10},cargo={18,0,0,0,119,32,0,0,2,3,10}]
        MalfunctionStatusResponse parsedRes = (MalfunctionStatusResponse) MessageTester.test(
                "005979590b120000007720000002030a5792",
                89,
                1,
                CharacteristicUUID.CURRENT_STATUS_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
        assertEquals(18, parsedRes.getCodeA());
        assertEquals(0x2077, parsedRes.getCodeB());
        assertFalse(parsedRes.hasMalfunction());
    }
}