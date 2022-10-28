package com.jwoglom.pumpx2.pump.messages.request.control;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;
import static com.jwoglom.pumpx2.pump.messages.MessageTester.initPumpState;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.messages.bluetooth.CharacteristicUUID;
import com.jwoglom.pumpx2.pump.messages.response.control.BolusPermissionReleaseResponse;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class BolusPermissionReleaseRequestTest {

    // test for unknown opcode -16
    @Test
    public void testBolusPermissionReleaseRequest_ID10676() throws DecoderException {
        // TimeSinceResetResponse[pumpTime=1200173,timeSinceReset=461710079]
        initPumpState("6VeDeRAL5DCigGw2", 461710079L);

        BolusPermissionReleaseRequest expected = new BolusPermissionReleaseRequest(10676);

        BolusPermissionReleaseRequest parsedReq = (BolusPermissionReleaseRequest) MessageTester.test(
                "013af03a1cb42900000023851b3c39b657fe391e",
                58,
                2,
                CharacteristicUUID.CONTROL_CHARACTERISTICS,
                expected,
                "003ac14d83666a2599ae79a30e5d9b459a"
        );

        assertHexEquals(expected.getCargo(), parsedReq.getCargo());
    }
}