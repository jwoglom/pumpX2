package com.jwoglom.pumpx2.pump.messages.response.control;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;
import static com.jwoglom.pumpx2.pump.messages.MessageTester.initPumpState;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.messages.PacketArrayList;
import com.jwoglom.pumpx2.pump.messages.bluetooth.CharacteristicUUID;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class SetG7PairingCodeResponseTest {
    @Test
    public void testSetG7PairingCodeResponse_success() throws DecoderException {
        initPumpState(PacketArrayList.IGNORE_INVALID_HMAC, 0L);
        
        SetG7PairingCodeResponse expected = new SetG7PairingCodeResponse(
            0
        );

        SetG7PairingCodeResponse parsedRes = (SetG7PairingCodeResponse) MessageTester.test(
                "00f9fdf91a00009346fd1f7a5eccab7fe004d6d23a4472643416ebd8b9fe8ff2bf",
                -7,
                1,
                CharacteristicUUID.CONTROL_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }
}