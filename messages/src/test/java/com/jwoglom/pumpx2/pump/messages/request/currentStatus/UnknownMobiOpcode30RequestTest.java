package com.jwoglom.pumpx2.pump.messages.request.currentStatus;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.messages.bluetooth.CharacteristicUUID;
import com.jwoglom.pumpx2.pump.messages.request.currentStatus.UnknownMobiOpcode30Request;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class UnknownMobiOpcode30RequestTest {
    @Test
    public void testUnknownMobiOpcode30Request() throws DecoderException {
        // empty cargo
        UnknownMobiOpcode30Request expected = new UnknownMobiOpcode30Request();

        UnknownMobiOpcode30Request parsedReq = (UnknownMobiOpcode30Request) MessageTester.test(
                "00311e31005aa2",
                49,
                1,
                CharacteristicUUID.CURRENT_STATUS_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedReq.getCargo());
    }
}