package com.jwoglom.pumpx2.pump.messages.request.currentStatus;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.messages.bluetooth.CharacteristicUUID;
import com.jwoglom.pumpx2.pump.messages.request.currentStatus.UnknownMobiOpcode111Request;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class UnknownMobiOpcode111RequestTest {
    @Test
    public void testUnknownMobiOpcode111Request() throws DecoderException {
        // empty cargo
        UnknownMobiOpcode111Request expected = new UnknownMobiOpcode111Request();

        UnknownMobiOpcode111Request parsedReq = (UnknownMobiOpcode111Request) MessageTester.test(
                "xxxx",
                3,
                1,
                CharacteristicUUID.CURRENT_STATUS_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedReq.getCargo());
    }
}