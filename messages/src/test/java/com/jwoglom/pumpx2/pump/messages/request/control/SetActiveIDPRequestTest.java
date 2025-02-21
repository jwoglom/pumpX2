package com.jwoglom.pumpx2.pump.messages.request.control;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;
import static com.jwoglom.pumpx2.pump.messages.MessageTester.initPumpState;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.messages.PacketArrayList;
import com.jwoglom.pumpx2.pump.messages.bluetooth.CharacteristicUUID;
import com.jwoglom.pumpx2.pump.messages.request.control.SetActiveIDPRequest;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class SetActiveIDPRequestTest {
    @Test
    public void testSetActiveIDPRequest_profileWithId1() throws DecoderException {
        initPumpState(PacketArrayList.IGNORE_INVALID_HMAC, 1L);

        SetActiveIDPRequest expected = new SetActiveIDPRequest(
                new byte[]{1, 1}
        );

        SetActiveIDPRequest parsedReq = (SetActiveIDPRequest) MessageTester.test(
                "0119ec191a010165493e2009a33736acb3713492",
                25,
                1,
                CharacteristicUUID.CONTROL_CHARACTERISTICS,
                expected,
                "00199d7598653853bd78abd443ec3b"
        );

        assertHexEquals(expected.getCargo(), parsedReq.getCargo());
    }
    @Test
    public void testSetActiveIDPRequest_profileWithId0() throws DecoderException {
        initPumpState(PacketArrayList.IGNORE_INVALID_HMAC, 1L);

        SetActiveIDPRequest expected = new SetActiveIDPRequest(
                new byte[]{0, 1}
        );

        SetActiveIDPRequest parsedReq = (SetActiveIDPRequest) MessageTester.test(
                "0124ec241a00017c493e2027b0811608d99b2431",
                36,
                1,
                CharacteristicUUID.CONTROL_CHARACTERISTICS,
                expected,
                "00249dec87124ebe4216766ea86133"
        );

        assertHexEquals(expected.getCargo(), parsedReq.getCargo());
    }
}