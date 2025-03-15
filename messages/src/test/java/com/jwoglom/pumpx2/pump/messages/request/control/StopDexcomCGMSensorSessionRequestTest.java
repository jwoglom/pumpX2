package com.jwoglom.pumpx2.pump.messages.request.control;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;
import static com.jwoglom.pumpx2.pump.messages.MessageTester.initPumpState;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.messages.PacketArrayList;
import com.jwoglom.pumpx2.pump.messages.bluetooth.CharacteristicUUID;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class StopDexcomCGMSensorSessionRequestTest {
    @Test
    public void testStopG6SensorSessionRequest_G6Sensor() throws DecoderException {
        initPumpState(PacketArrayList.IGNORE_INVALID_HMAC, 1L);

        StopDexcomCGMSensorSessionRequest expected = new StopDexcomCGMSensorSessionRequest();

        StopDexcomCGMSensorSessionRequest parsedReq = (StopDexcomCGMSensorSessionRequest) MessageTester.test(
                "015ab45a18810df31fafbfcb967e2fa032a42788",
                90,
                1,
                CharacteristicUUID.CONTROL_CHARACTERISTICS,
                expected,
                "005a3bfb226afd6e404f933dd7"
        );

        assertHexEquals(expected.getCargo(), parsedReq.getCargo());
    }


    @Test
    public void testStopG6SensorSessionRequest_G7Sensor() throws DecoderException {
        initPumpState(PacketArrayList.IGNORE_INVALID_HMAC, 1L);

        StopDexcomCGMSensorSessionRequest expected = new StopDexcomCGMSensorSessionRequest();

        StopDexcomCGMSensorSessionRequest parsedReq = (StopDexcomCGMSensorSessionRequest) MessageTester.test(
                "01ecb4ec1892cf5a2085735db9a25e5ae65bf61d",
                -20,
                1,
                CharacteristicUUID.CONTROL_CHARACTERISTICS,
                expected,
                "00ece4eb695f3d2116251ef498"
        );

        assertHexEquals(expected.getCargo(), parsedReq.getCargo());
    }
}