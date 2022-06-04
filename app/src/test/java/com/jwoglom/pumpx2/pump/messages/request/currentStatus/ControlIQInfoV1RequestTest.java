package com.jwoglom.pumpx2.pump.messages.request.currentStatus;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.bluetooth.CharacteristicUUID;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class ControlIQInfoV1RequestTest {
    @Test
    public void testControlIQInfoRequest() throws DecoderException {
        // empty cargo
        ControlIQInfoV1Request expected = new ControlIQInfoV1Request();

        ControlIQInfoV1Request parsedReq = (ControlIQInfoV1Request) MessageTester.test(
                "000368030005ab",
                3,
                1,
                CharacteristicUUID.CURRENT_STATUS_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedReq.getCargo());
    }
}