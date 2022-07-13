package com.jwoglom.pumpx2.pump.messages.request.currentStatus;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.messages.bluetooth.CharacteristicUUID;
import com.jwoglom.pumpx2.pump.messages.request.currentStatus.BolusPermissionChangeReasonRequest;

import org.apache.commons.codec.DecoderException;
import org.junit.Ignore;
import org.junit.Test;

public class BolusPermissionChangeReasonRequestTest {
    @Test
    @Ignore("TODO: causes an ErrorResponse!")
    public void testBolusPermissionChangeReasonRequest() throws DecoderException {
        // empty cargo
        BolusPermissionChangeReasonRequest expected = new BolusPermissionChangeReasonRequest();

        BolusPermissionChangeReasonRequest parsedReq = (BolusPermissionChangeReasonRequest) MessageTester.test(
                "xxxx",
                3,
                1,
                CharacteristicUUID.CURRENT_STATUS_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedReq.getCargo());
    }
}