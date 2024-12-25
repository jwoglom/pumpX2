package com.jwoglom.pumpx2.pump.messages.request.control;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;
import static com.jwoglom.pumpx2.pump.messages.MessageTester.initPumpState;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.messages.PacketArrayList;
import com.jwoglom.pumpx2.pump.messages.bluetooth.CharacteristicUUID;
import com.jwoglom.pumpx2.pump.messages.request.control.StopG6SensorSessionRequest;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class StopG6SensorSessionRequestTest {
    @Test
    public void testStopG6SensorSessionRequest() throws DecoderException {
        initPumpState(PacketArrayList.IGNORE_INVALID_HMAC, 1L);

        StopG6SensorSessionRequest expected = new StopG6SensorSessionRequest();

        StopG6SensorSessionRequest parsedReq = (StopG6SensorSessionRequest) MessageTester.test(
                "015ab45a18810df31fafbfcb967e2fa032a42788",
                90,
                1,
                CharacteristicUUID.CONTROL_CHARACTERISTICS,
                expected,
                "005a3bfb226afd6e404f933dd7"
        );

        assertHexEquals(expected.getCargo(), parsedReq.getCargo());
    }
}