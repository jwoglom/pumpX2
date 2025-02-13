package com.jwoglom.pumpx2.pump.messages.request.control;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;
import static com.jwoglom.pumpx2.pump.messages.MessageTester.initPumpState;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.messages.PacketArrayList;
import com.jwoglom.pumpx2.pump.messages.bluetooth.CharacteristicUUID;
import com.jwoglom.pumpx2.pump.messages.request.control.FillCannulaRequest;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class FillCannulaRequestTest {
    @Test
    public void testFillCannulaRequest() throws DecoderException {
        initPumpState(PacketArrayList.IGNORE_INVALID_HMAC, 0L);

        FillCannulaRequest expected = new FillCannulaRequest(300);

        FillCannulaRequest parsedReq = (FillCannulaRequest) MessageTester.test(
                "016d986d1a2c010f152820b9273d0fb99c1241f1",
                109,
                1,
                CharacteristicUUID.CONTROL_CHARACTERISTICS,
                expected,
                "006db9da764426a0aa99bb708571f4"
        );

        assertHexEquals(expected.getCargo(), parsedReq.getCargo());
    }
}