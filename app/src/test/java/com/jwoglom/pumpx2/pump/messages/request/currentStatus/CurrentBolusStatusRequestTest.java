package com.jwoglom.pumpx2.pump.messages.request.currentStatus;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.bluetooth.CharacteristicUUID;
import com.jwoglom.pumpx2.pump.messages.request.currentStatus.CurrentBolusStatusRequest;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class CurrentBolusStatusRequestTest {
    @Test
    public void testCurrentBolusStatusRequest() throws DecoderException {
        // empty cargo
        CurrentBolusStatusRequest expected = new CurrentBolusStatusRequest();

        CurrentBolusStatusRequest parsedReq = (CurrentBolusStatusRequest) MessageTester.test(
                "00032c0300686a",
                3,
                1,
                CharacteristicUUID.CURRENT_STATUS_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedReq.getCargo());
    }
}