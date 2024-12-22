package com.jwoglom.pumpx2.pump.messages.request.control;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;
import static com.jwoglom.pumpx2.pump.messages.MessageTester.initPumpState;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.messages.PacketArrayList;
import com.jwoglom.pumpx2.pump.messages.bluetooth.CharacteristicUUID;
import com.jwoglom.pumpx2.pump.messages.request.control.SuspendPumpingRequest;

import org.apache.commons.codec.DecoderException;
import org.junit.Ignore;
import org.junit.Test;

public class SuspendPumpingRequestTest {
    @Test
    public void testSuspendPumpingRequest() throws DecoderException {
        initPumpState(PacketArrayList.IGNORE_INVALID_HMAC, 0L);

        // empty cargo
        SuspendPumpingRequest expected = new SuspendPumpingRequest();

        SuspendPumpingRequest parsedReq = (SuspendPumpingRequest) MessageTester.test(
                "01659c651838eaee1f67bfcaf05f7c5e09412c65",
                101,
                1,
                CharacteristicUUID.CONTROL_CHARACTERISTICS,
                expected,
                "00651de503c71694d7e46bce00"
        );

        assertHexEquals(expected.getCargo(), parsedReq.getCargo());
    }
}