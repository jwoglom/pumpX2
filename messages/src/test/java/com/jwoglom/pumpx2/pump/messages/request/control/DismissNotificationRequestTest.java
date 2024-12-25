package com.jwoglom.pumpx2.pump.messages.request.control;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;
import static com.jwoglom.pumpx2.pump.messages.MessageTester.initPumpState;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.messages.PacketArrayList;
import com.jwoglom.pumpx2.pump.messages.bluetooth.CharacteristicUUID;
import com.jwoglom.pumpx2.pump.messages.request.control.DismissNotificationRequest;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class DismissNotificationRequestTest {
    @Test
    public void testDismissNotificationRequest_SiteChangeNotification() throws DecoderException {
        initPumpState(PacketArrayList.IGNORE_INVALID_HMAC, 0L);

        DismissNotificationRequest expected = new DismissNotificationRequest(
                new byte[]{2,0,0,0,0,0}
        );

        DismissNotificationRequest parsedReq = (DismissNotificationRequest) MessageTester.test(
                "011eb81e1e02000000000091eef21fb2f51e9b10",
                30,
                1,
                CharacteristicUUID.CONTROL_CHARACTERISTICS,
                expected,
                "001e1923326c7764e514a7cd6e702210fbe0f0"
        );

        assertHexEquals(expected.getCargo(), parsedReq.getCargo());
    }
}