package com.jwoglom.pumpx2.pump.messages.response.control;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;
import static com.jwoglom.pumpx2.pump.messages.MessageTester.initPumpState;
import com.jwoglom.pumpx2.pump.messages.PacketArrayList;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.messages.bluetooth.CharacteristicUUID;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class RenameIDPResponseTest {
    @Test
    public void testRenameIDPResponse() throws DecoderException { 
        initPumpState(PacketArrayList.IGNORE_INVALID_HMAC, 0L);

        RenameIDPResponse expected = new RenameIDPResponse(
                0,
            2
        );

        RenameIDPResponse parsedRes = (RenameIDPResponse) MessageTester.test(
                "00d6a9d61a0002b5023e20a9f6db74a433fd5d0f2abc3091f359c4b379c92a4ff6",
                -42,
                1,
                CharacteristicUUID.CONTROL_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }
}