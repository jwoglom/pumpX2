package com.jwoglom.pumpx2.pump.messages.request.control;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.messages.bluetooth.CharacteristicUUID;
import com.jwoglom.pumpx2.pump.messages.request.control.SuspendPumpingRequest;

import org.apache.commons.codec.DecoderException;
import org.junit.Ignore;
import org.junit.Test;

public class SuspendPumpingRequestTest {
    //  writing <00049c04185611841be28b90732ce0a7744781765d51771f6ef3ebc64c7f5c> to characteristic <7b83fffc-9f77-4e5c-8064-aae2c24838b9> failed, status 'ERROR'
    @Test
    @Ignore("needs full-control pump")
    public void testSuspendPumpingRequest() throws DecoderException {
        // empty cargo
        SuspendPumpingRequest expected = new SuspendPumpingRequest();

        SuspendPumpingRequest parsedReq = (SuspendPumpingRequest) MessageTester.test(
                "xxxx",
                3,
                1,
                CharacteristicUUID.CURRENT_STATUS_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedReq.getCargo());
    }
}