package com.jwoglom.pumpx2.pump.messages.request.currentStatus;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.messages.bluetooth.CharacteristicUUID;
import com.jwoglom.pumpx2.pump.messages.request.currentStatus.UnknownMobiOpcode20Request;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class UnknownMobiOpcode20RequestTest {
    @Test
    public void testUnknownMobiOpcode20Request() throws DecoderException {
        // empty cargo
        UnknownMobiOpcode20Request expected = new UnknownMobiOpcode20Request();

        UnknownMobiOpcode20Request parsedReq = (UnknownMobiOpcode20Request) MessageTester.test(
                "0082148200c52e",
                -126,
                1,
                CharacteristicUUID.CURRENT_STATUS_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedReq.getCargo());
    }
}