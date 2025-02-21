package com.jwoglom.pumpx2.pump.messages.response.control;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;
import static com.jwoglom.pumpx2.pump.messages.MessageTester.initPumpState;
import com.jwoglom.pumpx2.pump.messages.PacketArrayList;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.messages.bluetooth.CharacteristicUUID;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class SetIDPSegmentResponseTest {
    @Test
    public void testSetIDPSegmentResponse() throws DecoderException { 
        initPumpState(PacketArrayList.IGNORE_INVALID_HMAC, 0L);
        
        SetIDPSegmentResponse expected = new SetIDPSegmentResponse(
            new byte[]{0, 2}
        );

        SetIDPSegmentResponse parsedRes = (SetIDPSegmentResponse) MessageTester.test(
                "002dab2d1a000260033e205bc7dccaad0b38c91c4f40c451c67c31f90c26c1ccb4",
                45,
                1,
                CharacteristicUUID.CONTROL_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }
}