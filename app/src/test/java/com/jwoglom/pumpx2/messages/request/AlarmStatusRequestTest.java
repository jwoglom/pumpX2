package com.jwoglom.pumpx2.messages.request;

import static com.jwoglom.pumpx2.messages.MessageTester.assertHexEquals;

import com.jwoglom.pumpx2.messages.MessageTester;
import com.jwoglom.pumpx2.pump.bluetooth.CharacteristicUUID;
import com.jwoglom.pumpx2.pump.messages.request.AlarmStatusRequest;
import com.jwoglom.pumpx2.pump.messages.request.ApiVersionRequest;
import com.jwoglom.pumpx2.pump.messages.response.AlarmStatusResponse;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class AlarmStatusRequestTest {
    @Test
    public void testAlarmStatusRequest() throws DecoderException {
        // empty cargo
        AlarmStatusRequest expected = new AlarmStatusRequest();

        AlarmStatusRequest parsedReq = (AlarmStatusRequest) MessageTester.test(
                "0003460300c236",
                3,
                1,
                CharacteristicUUID.CURRENT_STATUS_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedReq.getCargo());
    }
}
