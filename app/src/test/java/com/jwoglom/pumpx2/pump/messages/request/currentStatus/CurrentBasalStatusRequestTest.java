package com.jwoglom.pumpx2.pump.messages.request.currentStatus;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.bluetooth.CharacteristicUUID;
import com.jwoglom.pumpx2.pump.messages.request.currentStatus.CurrentBasalStatusRequest;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class CurrentBasalStatusRequestTest {
    @Test
    public void testCurrentBasalStatusRequest() throws DecoderException {
        // empty cargo
        CurrentBasalStatusRequest expected = new CurrentBasalStatusRequest();

        CurrentBasalStatusRequest parsedReq = (CurrentBasalStatusRequest) MessageTester.test(
                "0003280300a8b6",
                3,
                1,
                CharacteristicUUID.CURRENT_STATUS_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedReq.getCargo());
    }
}