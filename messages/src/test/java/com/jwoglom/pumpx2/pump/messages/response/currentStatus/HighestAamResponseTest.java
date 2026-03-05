package com.jwoglom.pumpx2.pump.messages.response.currentStatus;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.messages.bluetooth.CharacteristicUUID;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class HighestAamResponseTest {
    // 12-0x2071 - stuck vibrator motor
    @Test
    public void testHighestAamResponse_12_0x2071_stuckVibratorMotor() throws DecoderException {
        HighestAamResponse expected = new HighestAamResponse(
            12, 0x2071, new byte[]{4, -1, -1}
        );

        HighestAamResponse parsedRes = (HighestAamResponse) MessageTester.test(
                "000779070b0c0000007120000004ffffd6a6",
                7,
                1,
                CharacteristicUUID.CURRENT_STATUS_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
        assertEquals(12, parsedRes.getAamId());
        assertEquals(0x2071, parsedRes.getFaultId());
        assertEquals("2071", parsedRes.getFaultIdAsHex());
        assertEquals("12-0x2071", parsedRes.getErrorString());
        assertTrue(parsedRes.hasMalfunction());
    }

    // 3-0x2026 - unknown and seems to be ignorable. Likely concurrent with PumpResetAlarm
    @Test
    public void testHighestAamResponse_3_0x2026_pumpResetAlarmIgnored() throws DecoderException {
        HighestAamResponse expected = new HighestAamResponse(
                3, 8230, new byte[]{2,-1,-1}
        );

        HighestAamResponse parsedRes = (HighestAamResponse) MessageTester.test(
                "001079100b030000002620000002ffff2ab0",
                16,
                1,
                CharacteristicUUID.CURRENT_STATUS_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
        assertEquals(3, parsedRes.getAamId());
        assertEquals(0x2026, parsedRes.getFaultId());

        assertFalse(parsedRes.hasMalfunction(
                new AlarmStatusResponse(AlarmStatusResponse.AlarmResponseType.PUMP_RESET_ALARM)
        ));
    }

    // 18-0x2077 -- appears on new pump. likely RESUME_PUMP_ALARM
    @Test
    public void testHighestAamResponse_18_0x2077_resumePumpAlarmIgnored() throws DecoderException {
        HighestAamResponse expected = new HighestAamResponse(
                18, 8311, new byte[]{2, 3, 10}
        );

        HighestAamResponse parsedRes = (HighestAamResponse) MessageTester.test(
                "005979590b120000007720000002030a5792",
                89,
                1,
                CharacteristicUUID.CURRENT_STATUS_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
        assertEquals(18, parsedRes.getAamId());
        assertEquals(0x2077, parsedRes.getFaultId());

        assertFalse(parsedRes.hasMalfunction(
                new AlarmStatusResponse(AlarmStatusResponse.AlarmResponseType.RESUME_PUMP_ALARM)
        ));
    }

    @Test
    public void testHighestAamResponse_parse10BytePayload() {
        byte[] raw10 = HighestAamResponse.buildCargo(12, 0x2071, new byte[]{4, -1});
        HighestAamResponse parsed = new HighestAamResponse();
        parsed.parse(raw10);

        assertEquals(12, parsed.getAamId());
        assertEquals(0x2071, parsed.getFaultId());
        assertEquals("2071", parsed.getFaultIdAsHex());
        assertArrayEquals(new byte[]{4, -1}, parsed.getRemaining());
        assertEquals("12-0x2071", parsed.getErrorString());
    }
}
