package com.jwoglom.pumpx2.messages.response;

import static org.junit.Assert.assertEquals;

import com.jwoglom.pumpx2.messages.MessageTester;
import com.jwoglom.pumpx2.pump.bluetooth.CharacteristicUUID;
import com.jwoglom.pumpx2.pump.messages.response.CGMHardwareInfoResponse;
import com.jwoglom.pumpx2.pump.messages.response.ControlIQIOBResponse;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class ControlIQIOBResponseTest {
    @Test
    public void testControlIQIOBResponse() throws DecoderException {
        ControlIQIOBResponse expected = new ControlIQIOBResponse(
                0L, 0L, 0L, 0L, ControlIQIOBResponse.IOBType.SWAN_6HR.id());

        ControlIQIOBResponse parsedRes = (ControlIQIOBResponse) MessageTester.test(
                "00036d031100000000000000000000000000000000015aa3",
                3,
                2,
                CharacteristicUUID.CURRENT_STATUS_CHARACTERISTICS,
                expected
        );

        assertEquals(expected.getMudaliarIOB(), parsedRes.getMudaliarIOB());
        assertEquals(expected.getTimeRemainingSeconds(), parsedRes.getTimeRemainingSeconds());
        assertEquals(expected.getMudaliarTotalIOB(), parsedRes.getMudaliarTotalIOB());
        assertEquals(expected.getSwan6hrIOB(), parsedRes.getSwan6hrIOB());
        assertEquals(expected.getIOBTypeInt(), parsedRes.getIOBTypeInt());
    }
}
