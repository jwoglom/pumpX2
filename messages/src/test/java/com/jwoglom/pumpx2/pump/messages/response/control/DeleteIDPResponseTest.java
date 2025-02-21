package com.jwoglom.pumpx2.pump.messages.response.control;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;
import static com.jwoglom.pumpx2.pump.messages.MessageTester.initPumpState;
import com.jwoglom.pumpx2.pump.messages.PacketArrayList;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.messages.bluetooth.CharacteristicUUID;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class DeleteIDPResponseTest {
    @Test
    public void testDeleteIDPResponse_deleteIdpId2() throws DecoderException {
        initPumpState(PacketArrayList.IGNORE_INVALID_HMAC, 0L);
        
        DeleteIDPResponse expected = new DeleteIDPResponse(
            0
        );

        DeleteIDPResponse parsedRes = (DeleteIDPResponse) MessageTester.test(
                "004daf4d1a000291033e208345bb76cb9e5327ede6e87a0e590223f1ab75c1f93d",
                77,
                1,
                CharacteristicUUID.CONTROL_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }


    @Test
    public void testDeleteIDPResponse_deleteIdpId1() throws DecoderException {
        initPumpState(PacketArrayList.IGNORE_INVALID_HMAC, 0L);

        DeleteIDPResponse expected = new DeleteIDPResponse(
                0
        );

        DeleteIDPResponse parsedRes = (DeleteIDPResponse) MessageTester.test(
                "0053af531a0002a0033e20d85eaa5d3690b58e62edeac041a420ef93de1bdda8bf",
                83,
                1,
                CharacteristicUUID.CONTROL_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }
}