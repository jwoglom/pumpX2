package com.jwoglom.pumpx2.pump.messages.request.control;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.messages.bluetooth.CharacteristicUUID;
import com.jwoglom.pumpx2.pump.messages.request.control.RemoteCarbEntryRequest;

import org.apache.commons.codec.DecoderException;
import org.junit.Ignore;
import org.junit.Test;

public class RemoteCarbEntryRequestTest {
    @Test
    @Ignore("todo")
    public void testRemoteCarbEntryRequest() throws DecoderException {
        // empty cargo
        RemoteCarbEntryRequest expected = new RemoteCarbEntryRequest();

        RemoteCarbEntryRequest parsedReq = (RemoteCarbEntryRequest) MessageTester.test(
                "xxxx",
                3,
                1,
                CharacteristicUUID.CURRENT_STATUS_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedReq.getCargo());
    }
}