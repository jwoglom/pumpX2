package com.jwoglom.pumpx2.pump.messages.request.currentStatus;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.messages.bluetooth.CharacteristicUUID;
import com.jwoglom.pumpx2.pump.messages.request.currentStatus.UnknownMobiOpcode110Request;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class UnknownMobiOpcode110RequestTest {
    @Test
    public void testUnknownMobiOpcode110Request_a() throws DecoderException {
        // empty cargo
        UnknownMobiOpcode110Request expected = new UnknownMobiOpcode110Request();

        UnknownMobiOpcode110Request parsedReq = (UnknownMobiOpcode110Request) MessageTester.test(
                "00e36ee3001709",
                -29,
                1,
                CharacteristicUUID.CURRENT_STATUS_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedReq.getCargo());
    }
}