package com.jwoglom.pumpx2.pump.messages.request.currentStatus;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.bluetooth.CharacteristicUUID;
import com.jwoglom.pumpx2.pump.messages.request.currentStatus.RemindersRequest;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class RemindersRequestTest {
    @Test
    public void testRemindersRequest() throws DecoderException {
        // empty cargo
        RemindersRequest expected = new RemindersRequest();

        RemindersRequest parsedReq = (RemindersRequest) MessageTester.test(
                "0003580300a06e",
                3,
                1,
                CharacteristicUUID.CURRENT_STATUS_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedReq.getCargo());
    }
}