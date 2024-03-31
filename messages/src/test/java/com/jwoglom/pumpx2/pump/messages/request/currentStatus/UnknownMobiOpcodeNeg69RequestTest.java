package com.jwoglom.pumpx2.pump.messages.request.currentStatus;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.messages.bluetooth.CharacteristicUUID;
import com.jwoglom.pumpx2.pump.messages.request.currentStatus.UnknownMobiOpcodeNeg69Request;

import org.apache.commons.codec.DecoderException;
import org.junit.Ignore;
import org.junit.Test;

public class UnknownMobiOpcodeNeg69RequestTest {
    @Test
    @Ignore("no testdata")
    public void testUnknownMobiOpcodeNeg69Request() throws DecoderException {
        // empty cargo
        UnknownMobiOpcodeNeg69Request expected = new UnknownMobiOpcodeNeg69Request();

        UnknownMobiOpcodeNeg69Request parsedReq = (UnknownMobiOpcodeNeg69Request) MessageTester.test(
                "xxxx",
                3,
                1,
                CharacteristicUUID.CURRENT_STATUS_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedReq.getCargo());
    }
}