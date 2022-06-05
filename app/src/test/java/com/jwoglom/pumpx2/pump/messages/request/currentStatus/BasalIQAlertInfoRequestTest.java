package com.jwoglom.pumpx2.pump.messages.request.currentStatus;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.bluetooth.CharacteristicUUID;
import com.jwoglom.pumpx2.pump.messages.request.currentStatus.BasalIQAlertInfoRequest;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class BasalIQAlertInfoRequestTest {
    @Test
    public void testBasalIQAlertInfoRequest() throws DecoderException {
        // empty cargo
        BasalIQAlertInfoRequest expected = new BasalIQAlertInfoRequest();

        BasalIQAlertInfoRequest parsedReq = (BasalIQAlertInfoRequest) MessageTester.test(
                "000366030004b0",
                3,
                1,
                CharacteristicUUID.CURRENT_STATUS_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedReq.getCargo());
    }
}