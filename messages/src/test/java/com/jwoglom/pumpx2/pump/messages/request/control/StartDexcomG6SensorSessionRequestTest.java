package com.jwoglom.pumpx2.pump.messages.request.control;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;
import static com.jwoglom.pumpx2.pump.messages.MessageTester.initPumpState;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.messages.PacketArrayList;
import com.jwoglom.pumpx2.pump.messages.bluetooth.CharacteristicUUID;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class StartDexcomG6SensorSessionRequestTest {
    @Test
    public void testStartG6SensorSessionRequest_code9311() throws DecoderException {
        initPumpState(PacketArrayList.IGNORE_INVALID_HMAC, 1L);

        StartDexcomG6SensorSessionRequest expected = new StartDexcomG6SensorSessionRequest(9311);

        StartDexcomG6SensorSessionRequest parsedReq = (StartDexcomG6SensorSessionRequest) MessageTester.test(
                "012eb22e1a5f24210af31f6806b4cc57d79bb047",
                46,
                1,
                CharacteristicUUID.CONTROL_CHARACTERISTICS,
                expected,
                "002e5f4261e734aaa05941d57f7901"
        );

        assertHexEquals(expected.getCargo(), parsedReq.getCargo());
    }
}