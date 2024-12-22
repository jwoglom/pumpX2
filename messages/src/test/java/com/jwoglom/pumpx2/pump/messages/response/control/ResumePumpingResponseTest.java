package com.jwoglom.pumpx2.pump.messages.response.control;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;
import static com.jwoglom.pumpx2.pump.messages.MessageTester.initPumpState;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.messages.bluetooth.CharacteristicUUID;
import com.jwoglom.pumpx2.shared.Hex;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class ResumePumpingResponseTest {
    @Test
    public void testResumePumpingResponse() throws DecoderException { 
        initPumpState("authenticationKey", 0L);
        
        ResumePumpingResponse expected = new ResumePumpingResponse(
                new byte[]{0,-75,-93,-18,31,22,-60,30,-83,-18,-104,71,-85,-122,28,-46,70,4,-95,30,74,104,-48,-89,-35}
        );

        ResumePumpingResponse parsedRes = (ResumePumpingResponse) MessageTester.test(
                "003d9b3d1900b5a3ee1f16c41eadee9847ab861cd24604a11e4a68d0a7dd2abe",
                61,
                1,
                CharacteristicUUID.CONTROL_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }
}