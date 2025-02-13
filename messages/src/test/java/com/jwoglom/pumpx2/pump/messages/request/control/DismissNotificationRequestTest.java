package com.jwoglom.pumpx2.pump.messages.request.control;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;
import static com.jwoglom.pumpx2.pump.messages.MessageTester.initPumpState;

import static org.junit.Assert.assertEquals;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.messages.PacketArrayList;
import com.jwoglom.pumpx2.pump.messages.bluetooth.CharacteristicUUID;
import com.jwoglom.pumpx2.pump.messages.request.control.DismissNotificationRequest;
import com.jwoglom.pumpx2.pump.messages.response.currentStatus.AlertStatusResponse;
import com.jwoglom.pumpx2.pump.messages.response.currentStatus.ReminderStatusResponse;

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
        assertEquals(ReminderStatusResponse.ReminderType.SITE_CHANGE_REMINDER.id(), parsedReq.getNotificationId());
        assertEquals(DismissNotificationRequest.NotificationType.REMINDER, parsedReq.getNotificationType());
    }


    @Test
    public void testDismissNotificationRequest_alert_CGM_GRAPH_REMOVED() throws DecoderException {
        initPumpState(PacketArrayList.IGNORE_INVALID_HMAC, 0L);

        DismissNotificationRequest expected = new DismissNotificationRequest(
                new byte[]{25,0,0,0,1,0}
        );

        DismissNotificationRequest parsedReq = (DismissNotificationRequest) MessageTester.test(
                "01ddb8dd1e1900000001001b92f41f4ebdd42d94",
                -35,
                1,
                CharacteristicUUID.CONTROL_CHARACTERISTICS,
                expected,
                "00dd71575252fcc1476112db4196041031cf27"
        );

        assertHexEquals(expected.getCargo(), parsedReq.getCargo());
        assertEquals(AlertStatusResponse.AlertResponseType.CGM_GRAPH_REMOVED.bitmask(), parsedReq.getNotificationId());
        assertEquals(DismissNotificationRequest.NotificationType.ALERT, parsedReq.getNotificationType());
    }



    @Test
    public void testDismissNotificationRequest_alert_INVALID_TRANSMITTER_ID() throws DecoderException {
        initPumpState(PacketArrayList.IGNORE_INVALID_HMAC, 0L);

        DismissNotificationRequest expected = new DismissNotificationRequest(
                new byte[]{29,0,0,0,1,0}
        );

        DismissNotificationRequest parsedReq = (DismissNotificationRequest) MessageTester.test(
                "0112b8121e1d00000001008190fd1fd4f0eef6a2",
                18,
                1,
                CharacteristicUUID.CONTROL_CHARACTERISTICS,
                expected,
                "0012398a829fd5358e41f168f0b82619ad9976"
        );

        assertHexEquals(expected.getCargo(), parsedReq.getCargo());
        assertEquals(AlertStatusResponse.AlertResponseType.INVALID_TRANSMITTER_ID.bitmask(), parsedReq.getNotificationId());
        assertEquals(DismissNotificationRequest.NotificationType.ALERT, parsedReq.getNotificationType());
    }
}