package com.jwoglom.pumpx2.pump.messages.response.currentStatus;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.bluetooth.CharacteristicUUID;
import com.jwoglom.pumpx2.pump.messages.response.currentStatus.BasalIQAlertInfoResponse;

import org.apache.commons.codec.DecoderException;
import org.junit.Ignore;
import org.junit.Test;

public class BasalIQAlertInfoResponseTest {
    @Test
    @Ignore("needs to be run on a BasalIQ pump")
    public void testBasalIQAlertInfoResponse() throws DecoderException {
        BasalIQAlertInfoResponse expected = new BasalIQAlertInfoResponse(
            // long alertId
        );

        BasalIQAlertInfoResponse parsedRes = (BasalIQAlertInfoResponse) MessageTester.test(
                "xxxx",
                3,
                1,
                CharacteristicUUID.CURRENT_STATUS_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }
}