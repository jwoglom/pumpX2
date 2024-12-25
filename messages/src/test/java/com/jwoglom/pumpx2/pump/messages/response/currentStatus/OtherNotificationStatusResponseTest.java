package com.jwoglom.pumpx2.pump.messages.response.currentStatus;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.messages.bluetooth.CharacteristicUUID;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class OtherNotificationStatusResponseTest {
    @Test
    public void testOtherNotificationStatusResponse_Unknown() throws DecoderException {
        OtherNotificationStatusResponse expected = new OtherNotificationStatusResponse(
                new byte[]{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2}
        );

        OtherNotificationStatusResponse parsedRes = (OtherNotificationStatusResponse) MessageTester.test(
                "002d932d110000000000000000000000000000000002cded",
                45,
                2,
                CharacteristicUUID.CURRENT_STATUS_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }
    @Test
    public void testOtherNotificationStatusResponse_SiteChangeNotification() throws DecoderException {
        OtherNotificationStatusResponse expected = new OtherNotificationStatusResponse(
            new byte[]{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2}
        );

        OtherNotificationStatusResponse parsedRes = (OtherNotificationStatusResponse) MessageTester.test(
                "001893181100000000000000000000000000000000022a2b",
                24,
                2,
                CharacteristicUUID.CURRENT_STATUS_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }
}