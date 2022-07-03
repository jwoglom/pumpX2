package com.jwoglom.pumpx2.pump.messages.response.currentStatus;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.bluetooth.CharacteristicUUID;
import com.jwoglom.pumpx2.pump.messages.response.currentStatus.ReminderStatusResponse;

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
}