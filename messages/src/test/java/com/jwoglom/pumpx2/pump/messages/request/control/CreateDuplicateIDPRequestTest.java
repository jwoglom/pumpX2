package com.jwoglom.pumpx2.pump.messages.request.control;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;
import static com.jwoglom.pumpx2.pump.messages.MessageTester.initPumpState;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.messages.PacketArrayList;
import com.jwoglom.pumpx2.pump.messages.bluetooth.CharacteristicUUID;
import com.jwoglom.pumpx2.pump.messages.request.control.CreateDuplicateIDPRequest;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class CreateDuplicateIDPRequestTest {
    @Test
    public void testCreateDuplicateIDPRequest() throws DecoderException {
        initPumpState(PacketArrayList.IGNORE_INVALID_HMAC, 0L);

        CreateDuplicateIDPRequest expected = new CreateDuplicateIDPRequest("dup", 1);

        CreateDuplicateIDPRequest parsedReq = (CreateDuplicateIDPRequest) MessageTester.test(
                "0337e6373b647570000000000000000000000000",
                55,
                1,
                CharacteristicUUID.CONTROL_CHARACTERISTICS,
                expected,
                "0237000000000000000000000000000000000000",
                "01370100b0493e20b795d5f162b92bbd30cd8823",
                "00372bc754ba29764a5b4765"
        );

        assertHexEquals(expected.getCargo(), parsedReq.getCargo());
    }
}