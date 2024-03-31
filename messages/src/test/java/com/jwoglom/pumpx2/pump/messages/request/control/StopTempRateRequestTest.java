package com.jwoglom.pumpx2.pump.messages.request.control;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;
import static com.jwoglom.pumpx2.pump.messages.MessageTester.initPumpState;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.messages.PacketArrayList;
import com.jwoglom.pumpx2.pump.messages.bluetooth.CharacteristicUUID;
import com.jwoglom.pumpx2.pump.messages.request.control.StopTempRateRequest;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class StopTempRateRequestTest {
    @Test
    public void testStopTempRateRequest() throws DecoderException {
        // TimeSinceResetResponse[currentTime=512443560,pumpTimeSinceReset=1906112,cargo={-88,68,-117,30,-64,21,29,0}]
        initPumpState(PacketArrayList.IGNORE_INVALID_HMAC, 1906112L);

        // empty cargo
        StopTempRateRequest expected = new StopTempRateRequest();

        StopTempRateRequest parsedReq = (StopTempRateRequest) MessageTester.test(
                "012ca62c182c998b1e92605660ce66863620411d",
                44,
                2,
                CharacteristicUUID.CONTROL_CHARACTERISTICS,
                expected,
                "002c0d420615066cb0f317766c"
        );

        assertHexEquals(expected.getCargo(), parsedReq.getCargo());
    }
}