package com.jwoglom.pumpx2.pump.messages.response.control;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;
import static com.jwoglom.pumpx2.pump.messages.MessageTester.initPumpState;
import com.jwoglom.pumpx2.pump.messages.PacketArrayList;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.messages.bluetooth.CharacteristicUUID;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class CreateIDPResponseTest {
    @Test
    public void testCreateNewIDPResponse() throws DecoderException { 
        initPumpState(PacketArrayList.IGNORE_INVALID_HMAC, 0L);
        
        CreateIDPResponse expected = new CreateIDPResponse(
            0, 2
        );

        CreateIDPResponse parsedRes = (CreateIDPResponse) MessageTester.test(
                "00c9e7c91a000286023e203400da47812a7ab10adc5f006d21fda44a7d7afa907b",
                -55,
                1,
                CharacteristicUUID.CONTROL_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }


    @Test
    public void testCreateIDPResponse_duplicate1() throws DecoderException {
        initPumpState(PacketArrayList.IGNORE_INVALID_HMAC, 0L);

        CreateIDPResponse expected = new CreateIDPResponse(
                new byte[]{0, 2}
        );

        CreateIDPResponse parsedRes = (CreateIDPResponse) MessageTester.test(
                "0037e7371a000281033e20e6b4f3960cd57ac9d1d4d84e18cd1571588aa0cf4281",
                55,
                1,
                CharacteristicUUID.CONTROL_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }
}