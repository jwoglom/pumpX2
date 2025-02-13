package com.jwoglom.pumpx2.pump.messages.response.control;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;
import static com.jwoglom.pumpx2.pump.messages.MessageTester.initPumpState;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.messages.PacketArrayList;
import com.jwoglom.pumpx2.pump.messages.bluetooth.CharacteristicUUID;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class FillCannulaResponseTest {
    @Test
    public void testFillCannulaResponse_ok() throws DecoderException {
        initPumpState(PacketArrayList.IGNORE_INVALID_HMAC, 0L);
        
        FillCannulaResponse expected = new FillCannulaResponse(
            0
        );

        FillCannulaResponse parsedRes = (FillCannulaResponse) MessageTester.test(
                "006d996d1900d4ce2720d38b10afb8f57ab650c6682172ef1f0af1e8d4e59e0c",
                109,
                1,
                CharacteristicUUID.CONTROL_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }
}