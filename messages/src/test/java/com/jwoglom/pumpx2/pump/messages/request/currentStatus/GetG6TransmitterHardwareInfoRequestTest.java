package com.jwoglom.pumpx2.pump.messages.request.currentStatus;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.messages.bluetooth.CharacteristicUUID;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class GetG6TransmitterHardwareInfoRequestTest {
    @Test
    public void testUnknownMobiOpcodeNeg60Request() throws DecoderException {
        // empty cargo
        GetG6TransmitterHardwareInfoRequest expected = new GetG6TransmitterHardwareInfoRequest();

        GetG6TransmitterHardwareInfoRequest parsedReq = (GetG6TransmitterHardwareInfoRequest) MessageTester.test(
                "001ac41a0013da",
                26,
                1,
                CharacteristicUUID.CURRENT_STATUS_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedReq.getCargo());
    }
}