package com.jwoglom.pumpx2.pump.messages.response.currentStatus;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;
import static org.junit.Assert.assertEquals;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.messages.bluetooth.CharacteristicUUID;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class ControlIQIOBResponseTest {
    @Test
    public void testControlIQIOBResponse_noInsulinOnBoard() throws DecoderException {
        ControlIQIOBResponse expected = new ControlIQIOBResponse(
                0L, 0L, 0L, 0L, ControlIQIOBResponse.IOBType.SWAN_6HR.id());

        ControlIQIOBResponse parsedRes = (ControlIQIOBResponse) MessageTester.test(
                "00036d031100000000000000000000000000000000015aa3",
                3,
                2,
                CharacteristicUUID.CURRENT_STATUS_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
        assertEquals(expected.getMudaliarIOB(), parsedRes.getMudaliarIOB());
        assertEquals(expected.getTimeRemainingSeconds(), parsedRes.getTimeRemainingSeconds());
        assertEquals(expected.getMudaliarTotalIOB(), parsedRes.getMudaliarTotalIOB());
        assertEquals(expected.getSwan6hrIOB(), parsedRes.getSwan6hrIOB());
        assertEquals(expected.getIOBTypeInt(), parsedRes.getIOBTypeInt());
    }

    @Test
    public void testControlIQIOBResponse_withInsulinOnBoard_controlIQOff() throws DecoderException {
        // ControlIQIOBResponse[iobType=0,mudaliarIOB=192,mudaliarTotalIOB=241,swan6hrIOB=161,timeRemainingSeconds=14460]
        ControlIQIOBResponse expected = new ControlIQIOBResponse(
                192L, 14460L, 241L, 161L, ControlIQIOBResponse.IOBType.MUDALIAR.id());

        ControlIQIOBResponse parsedRes = (ControlIQIOBResponse) MessageTester.test(
                "00056d0511c00000007c380000f1000000a100000000ab8d",
                5,
                2,
                CharacteristicUUID.CURRENT_STATUS_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
        assertEquals(expected.getMudaliarIOB(), parsedRes.getMudaliarIOB());
        assertEquals(expected.getTimeRemainingSeconds(), parsedRes.getTimeRemainingSeconds());
        assertEquals(expected.getMudaliarTotalIOB(), parsedRes.getMudaliarTotalIOB());
        assertEquals(expected.getSwan6hrIOB(), parsedRes.getSwan6hrIOB());
        assertEquals(expected.getIOBTypeInt(), parsedRes.getIOBTypeInt());
    }

    @Test
    public void testControlIQIOBResponse_withInsulinOnBoard_controlIQOn() throws DecoderException {
        // ControlIQIOBResponse[iobType=1,mudaliarIOB=184,mudaliarTotalIOB=241,swan6hrIOB=154,timeRemainingSeconds=14220]
        ControlIQIOBResponse expected = new ControlIQIOBResponse(
                184L, 14220L, 241L, 154L, ControlIQIOBResponse.IOBType.SWAN_6HR.id());

        ControlIQIOBResponse parsedRes = (ControlIQIOBResponse) MessageTester.test(
                "00066d0611b80000008c370000f10000009a00000001c315",
                6,
                2,
                CharacteristicUUID.CURRENT_STATUS_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
        assertEquals(expected.getMudaliarIOB(), parsedRes.getMudaliarIOB());
        assertEquals(expected.getTimeRemainingSeconds(), parsedRes.getTimeRemainingSeconds());
        assertEquals(expected.getMudaliarTotalIOB(), parsedRes.getMudaliarTotalIOB());
        assertEquals(expected.getSwan6hrIOB(), parsedRes.getSwan6hrIOB());
        assertEquals(expected.getIOBTypeInt(), parsedRes.getIOBTypeInt());
    }
}
