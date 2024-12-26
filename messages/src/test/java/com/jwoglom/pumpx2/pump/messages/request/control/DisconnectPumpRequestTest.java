package com.jwoglom.pumpx2.pump.messages.request.control;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;
import static com.jwoglom.pumpx2.pump.messages.MessageTester.initPumpState;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.messages.bluetooth.CharacteristicUUID;
import com.jwoglom.pumpx2.pump.messages.request.control.DisconnectPumpRequest;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class DisconnectPumpRequestTest {
    @Test
    public void testDisconnectPumpRequest() throws DecoderException {
        initPumpState("authenticationKey", 0L);

        // empty cargo
        DisconnectPumpRequest expected = new DisconnectPumpRequest();

        DisconnectPumpRequest parsedReq = (DisconnectPumpRequest) MessageTester.test(
                "01debede18da38f31f38413194c36c036e110c25",
                -34,
                1,
                CharacteristicUUID.CONTROL_CHARACTERISTICS,
                expected,
                "00deff81b3f10f7af28dcfb8fc"
        );

        assertHexEquals(expected.getCargo(), parsedReq.getCargo());
    }
}