package com.jwoglom.pumpx2.pump.messages.request.control;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;
import static com.jwoglom.pumpx2.pump.messages.MessageTester.initPumpState;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.messages.PacketArrayList;
import com.jwoglom.pumpx2.pump.messages.bluetooth.CharacteristicUUID;
import com.jwoglom.pumpx2.pump.messages.request.control.PlaySoundRequest;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class PlaySoundRequestTest {
    @Test
    public void testPlaySoundRequest() throws DecoderException {
        initPumpState(PacketArrayList.IGNORE_INVALID_HMAC, 0L);

        PlaySoundRequest expected = new PlaySoundRequest();

        PlaySoundRequest parsedReq = (PlaySoundRequest) MessageTester.test(
                "01cef4ce182337f31f36da5eea5ed250773df9b0",
                -50,
                1,
                CharacteristicUUID.CONTROL_CHARACTERISTICS,
                expected,
                "00ce91daca790983cb56d5fff1"
        );

        assertHexEquals(expected.getCargo(), parsedReq.getCargo());
    }
}