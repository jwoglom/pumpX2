package com.jwoglom.pumpx2.pump.messages.response.currentStatus;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.messages.bluetooth.CharacteristicUUID;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

import java.math.BigInteger;

public class ReminderStatusResponseTest {
    @Test
    public void testReminderStatusResponseEmpty() throws DecoderException {
        ReminderStatusResponse expected = new ReminderStatusResponse(
            BigInteger.ZERO
        );

        ReminderStatusResponse parsedRes = (ReminderStatusResponse) MessageTester.test(
                "000349030800000000000000002d72",
                3,
                1,
                CharacteristicUUID.CURRENT_STATUS_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }


    @Test
    public void testReminderStatusResponse_SiteChangeReminder() throws DecoderException {
        ReminderStatusResponse expected = new ReminderStatusResponse(
                ReminderStatusResponse.ReminderType.toBitmask(
                        ReminderStatusResponse.ReminderType.SITE_CHANGE_REMINDER
                )
        );

        ReminderStatusResponse parsedRes = (ReminderStatusResponse) MessageTester.test(
                "00194919080400000000000000177d",
                25,
                1,
                CharacteristicUUID.CURRENT_STATUS_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }
}