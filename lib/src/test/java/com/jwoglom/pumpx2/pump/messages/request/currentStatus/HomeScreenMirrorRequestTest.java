package com.jwoglom.pumpx2.pump.messages.request.currentStatus;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.bluetooth.CharacteristicUUID;
import com.jwoglom.pumpx2.pump.messages.request.currentStatus.HomeScreenMirrorRequest;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class HomeScreenMirrorRequestTest {
    @Test
    public void testHomeScreenMirrorRequest() throws DecoderException {
        // empty cargo
        HomeScreenMirrorRequest expected = new HomeScreenMirrorRequest();

        HomeScreenMirrorRequest parsedReq = (HomeScreenMirrorRequest) MessageTester.test(
                "0003380300cbf5",
                3,
                1,
                CharacteristicUUID.CURRENT_STATUS_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedReq.getCargo());
    }
}