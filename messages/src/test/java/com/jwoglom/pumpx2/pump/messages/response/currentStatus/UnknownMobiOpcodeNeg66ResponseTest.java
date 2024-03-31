package com.jwoglom.pumpx2.pump.messages.response.currentStatus;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.messages.bluetooth.CharacteristicUUID;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class UnknownMobiOpcodeNeg66ResponseTest {
    @Test
    public void testUnknownMobiOpcodeNeg66Response_a() throws DecoderException {
        UnknownMobiOpcodeNeg66Response expected = new UnknownMobiOpcodeNeg66Response(
                new byte[]{2,0,0,0,0,14,-82,-118,30,3,0,0,0,0,0,0,0,0,1,0}
        );

        UnknownMobiOpcodeNeg66Response parsedRes = (UnknownMobiOpcodeNeg66Response) MessageTester.test(
                // Untitled_1_Live_-_Humans_iPhone_non-decoded
                "00d5bfd51402000000000eae8a1e0300000000000000000100d42f",
                -43,
                2,
                CharacteristicUUID.CURRENT_STATUS_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }
    @Test
    public void testUnknownMobiOpcodeNeg66Response_b() throws DecoderException {
        UnknownMobiOpcodeNeg66Response expected = new UnknownMobiOpcodeNeg66Response(
            new byte[]{2,0,0,0,0,13,-82,-118,30,4,0,0,0,0,0,0,0,0,1,0}
        );

        UnknownMobiOpcodeNeg66Response parsedRes = (UnknownMobiOpcodeNeg66Response) MessageTester.test(
                // Untitled_1_Live_-_Humans_iPhone_non-decoded
                "00eabfea1402000000000dae8a1e04000000000000000001002d7d",
                -22,
                2,
                CharacteristicUUID.CURRENT_STATUS_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }
}