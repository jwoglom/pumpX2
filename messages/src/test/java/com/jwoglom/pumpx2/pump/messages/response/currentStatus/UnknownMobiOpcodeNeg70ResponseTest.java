package com.jwoglom.pumpx2.pump.messages.response.currentStatus;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.messages.bluetooth.CharacteristicUUID;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class UnknownMobiOpcodeNeg70ResponseTest {
    @Test
    public void testUnknownMobiOpcodeNeg70Response_a() throws DecoderException {
        UnknownMobiOpcodeNeg70Response expected = new UnknownMobiOpcodeNeg70Response(
                new byte[]{1,4,-13,1,0,0,-2,53,-117,30,-48,7,0,0,3,8,9,-48,7,0,0,40,7,29,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,-1,-1,-1,-1,0,0,0,0}
        );

        UnknownMobiOpcodeNeg70Response parsedRes = (UnknownMobiOpcodeNeg70Response) MessageTester.test(
                // HobbyBill/Untitled_1_Live_-_Humans_iPhone_non-decoded.btsnoop
                "00e7bbe7350104f3010000fe358b1ed0070000030809d007000028071d000000000000000000000000000000000000000000ffffffff000000004106",
                -25,
                4,
                CharacteristicUUID.CURRENT_STATUS_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }

    @Test
    public void testUnknownMobiOpcodeNeg70Response_b() throws DecoderException {
        UnknownMobiOpcodeNeg70Response expected = new UnknownMobiOpcodeNeg70Response(
                new byte[]{1,4,-11,1,0,0,100,66,-117,30,-6,0,0,0,3,8,8,-6,0,0,0,-113,19,29,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,-1,-1,-1,-1,0,0,0,0}
        );

        UnknownMobiOpcodeNeg70Response parsedRes = (UnknownMobiOpcodeNeg70Response) MessageTester.test(
                // HobbyBill/Untitled_1_Live_-_Humans_iPhone_non-decoded.btsnoop
                "0028bb28350104f501000064428b1efa000000030808fa0000008f131d000000000000000000000000000000000000000000ffffffff000000001127",
                40,
                4,
                CharacteristicUUID.CURRENT_STATUS_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }
}