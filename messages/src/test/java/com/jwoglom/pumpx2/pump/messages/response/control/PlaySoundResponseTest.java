package com.jwoglom.pumpx2.pump.messages.response.control;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;
import static com.jwoglom.pumpx2.pump.messages.MessageTester.initPumpState;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.messages.bluetooth.CharacteristicUUID;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class PlaySoundResponseTest {
    @Test
    public void testPlaySoundResponse() throws DecoderException { 
        initPumpState("authenticationKey", 0L);
        
        PlaySoundResponse expected = new PlaySoundResponse(
            new byte[]{0}
        );

        PlaySoundResponse parsedRes = (PlaySoundResponse) MessageTester.test(
                "00cef5ce1900cef0f21fbf00d7c6462ba60db8d201b730849ce4458962818856",
                -50,
                1,
                CharacteristicUUID.CONTROL_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }
}