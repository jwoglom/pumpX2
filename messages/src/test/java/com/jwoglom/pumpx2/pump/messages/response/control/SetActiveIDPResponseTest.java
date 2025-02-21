package com.jwoglom.pumpx2.pump.messages.response.control;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;
import static com.jwoglom.pumpx2.pump.messages.MessageTester.initPumpState;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.messages.PacketArrayList;
import com.jwoglom.pumpx2.pump.messages.bluetooth.CharacteristicUUID;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class SetActiveIDPResponseTest {
    @Test
    public void testSetActiveIDPResponse_success() throws DecoderException {
        initPumpState(PacketArrayList.IGNORE_INVALID_HMAC, 0L);
        
        SetActiveIDPResponse expected = new SetActiveIDPResponse(
            0
        );

        SetActiveIDPResponse parsedRes = (SetActiveIDPResponse) MessageTester.test(
                "0019ed19190036033e20eb7b1d7dc0f2aad134ccab6029516c36a4d910b55778",
                25,
                1,
                CharacteristicUUID.CONTROL_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }
}