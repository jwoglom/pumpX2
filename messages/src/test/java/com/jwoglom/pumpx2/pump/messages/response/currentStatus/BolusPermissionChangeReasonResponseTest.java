package com.jwoglom.pumpx2.pump.messages.response.currentStatus;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.messages.bluetooth.CharacteristicUUID;
import com.jwoglom.pumpx2.pump.messages.response.currentStatus.BolusPermissionChangeReasonResponse;

import org.apache.commons.codec.DecoderException;
import org.junit.Ignore;
import org.junit.Test;

public class BolusPermissionChangeReasonResponseTest {
    @Test
    @Ignore("TODO: request causes an ErrorResponse!")
    public void testBolusPermissionChangeReasonResponse() throws DecoderException {
        BolusPermissionChangeReasonResponse expected = new BolusPermissionChangeReasonResponse(
            // int bolusId, boolean isAcked, int lastChangeReason, boolean currentPermissionHolder
        );

        BolusPermissionChangeReasonResponse parsedRes = (BolusPermissionChangeReasonResponse) MessageTester.test(
                "xxxx",
                3,
                1,
                CharacteristicUUID.CURRENT_STATUS_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }
}