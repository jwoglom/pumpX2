package com.jwoglom.pumpx2.pump.messages.response.currentStatus;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.messages.bluetooth.CharacteristicUUID;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class UnknownMobiOpcodeNeg124ResponseTest {
    @Test
    public void testUnknownMobiOpcodeNeg124Response_a() throws DecoderException {
        UnknownMobiOpcodeNeg124Response expected = new UnknownMobiOpcodeNeg124Response(
            new byte[]{0,-4,64,-117,30,-95,53,74,102,85,-112,-61,24,41,27,-127,12,-51,-71,101,-119,108,-100,-9,75}
        );

        UnknownMobiOpcodeNeg124Response parsedRes = (UnknownMobiOpcodeNeg124Response) MessageTester.test(
                // Untitled_1_Live_-_Humans_iPhone_non-decoded
                "00ae85ae1900fc408b1ea1354a665590c318291b810ccdb965896c9cf74b3206",
                -82,
                2,
                CharacteristicUUID.CURRENT_STATUS_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }


    @Test
    public void testUnknownMobiOpcodeNeg124Response_b() throws DecoderException {
        UnknownMobiOpcodeNeg124Response expected = new UnknownMobiOpcodeNeg124Response(
                new byte[]{0,-125,65,-117,30,-28,68,-86,47,-11,21,92,79,103,100,83,46,23,-84,-36,56,93,-68,-3,76}
        );

        UnknownMobiOpcodeNeg124Response parsedRes = (UnknownMobiOpcodeNeg124Response) MessageTester.test(
                // Untitled_1_Live_-_Humans_iPhone_non-decoded
                "00c585c5190083418b1ee444aa2ff5155c4f6764532e17acdc385dbcfd4c27e7",
                -59,
                2,
                CharacteristicUUID.CURRENT_STATUS_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }
}