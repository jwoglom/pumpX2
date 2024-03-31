package com.jwoglom.pumpx2.pump.messages.request.currentStatus;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.messages.bluetooth.CharacteristicUUID;
import com.jwoglom.pumpx2.pump.messages.request.currentStatus.UnknownMobiOpcodeNeg66Request;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class UnknownMobiOpcodeNeg66RequestTest {
    @Test
    public void testUnknownMobiOpcodeNeg66Request() throws DecoderException {
        // empty cargo
        UnknownMobiOpcodeNeg66Request expected = new UnknownMobiOpcodeNeg66Request();

        UnknownMobiOpcodeNeg66Request parsedReq = (UnknownMobiOpcodeNeg66Request) MessageTester.test(
                // Untitled_1_Live_-_Humans_iPhone_non-decoded
                "00eabeea001bd6",
                -22,
                1,
                CharacteristicUUID.CURRENT_STATUS_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedReq.getCargo());
    }
}