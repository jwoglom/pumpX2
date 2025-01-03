package com.jwoglom.pumpx2.pump.messages.request.currentStatus;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.messages.bluetooth.CharacteristicUUID;
import com.jwoglom.pumpx2.pump.messages.request.currentStatus.UnknownMobiOpcodeNeg70Request;

import org.apache.commons.codec.DecoderException;
import org.junit.Ignore;
import org.junit.Test;

public class UnknownMobiOpcodeNeg70RequestTest {
    @Test
    public void testUnknownMobiOpcodeNeg70Request() throws DecoderException {
        // empty cargo
        UnknownMobiOpcodeNeg70Request expected = new UnknownMobiOpcodeNeg70Request();

        UnknownMobiOpcodeNeg70Request parsedReq = (UnknownMobiOpcodeNeg70Request) MessageTester.test(
                "00e7bae700877c",
                -25,
                1,
                CharacteristicUUID.CURRENT_STATUS_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedReq.getCargo());
    }
}