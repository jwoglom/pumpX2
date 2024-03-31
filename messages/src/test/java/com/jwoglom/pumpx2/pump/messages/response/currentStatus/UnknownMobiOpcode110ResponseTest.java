package com.jwoglom.pumpx2.pump.messages.response.currentStatus;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.messages.bluetooth.CharacteristicUUID;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class UnknownMobiOpcode110ResponseTest {
    @Test
    public void testUnknownMobiOpcode110Response_a() throws DecoderException {
        UnknownMobiOpcode110Response expected = new UnknownMobiOpcode110Response(
            new byte[]{-47,-64,0,0}
        );

        UnknownMobiOpcode110Response parsedRes = (UnknownMobiOpcode110Response) MessageTester.test(
                // HobbyBill/Untitled_1_Live_-_Humans_iPhone_non-decoded.btsnoop
                "00e36fe304d1c00000a3fc",
                -29,
                1,
                CharacteristicUUID.CURRENT_STATUS_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }

    @Test
    public void testUnknownMobiOpcode110Response_b() throws DecoderException {
        UnknownMobiOpcode110Response expected = new UnknownMobiOpcode110Response(
                new byte[]{-47,-64,0,0}
        );

        UnknownMobiOpcode110Response parsedRes = (UnknownMobiOpcode110Response) MessageTester.test(
                // HobbyBill/Untitled_1_Live_-_Humans_iPhone_non-decoded.btsnoop
                "00e46fe404d1c00000e234",
                -28,
                1,
                CharacteristicUUID.CURRENT_STATUS_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }

    @Test
    public void testUnknownMobiOpcode110Response_c() throws DecoderException {
        UnknownMobiOpcode110Response expected = new UnknownMobiOpcode110Response(
                new byte[]{57,-62,0,0}
        );

        UnknownMobiOpcode110Response parsedRes = (UnknownMobiOpcode110Response) MessageTester.test(
                // HobbyBill/Untitled_1_Live_-_Humans_iPhone_non-decoded.btsnoop
                "00fa6ffa0439c200008cc1",
                -6,
                1,
                CharacteristicUUID.CURRENT_STATUS_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }
}