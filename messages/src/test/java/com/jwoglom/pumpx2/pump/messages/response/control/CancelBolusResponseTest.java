package com.jwoglom.pumpx2.pump.messages.response.control;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;
import static com.jwoglom.pumpx2.pump.messages.MessageTester.initPumpState;

import static org.junit.Assert.assertEquals;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.messages.PacketArrayList;
import com.jwoglom.pumpx2.pump.messages.bluetooth.CharacteristicUUID;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class CancelBolusResponseTest {
    @Test
    public void testCancelBolusResponse_ID10677() throws DecoderException {
        // TimeSinceResetResponse[pumpTime=1200239,timeSinceReset=461710145]
        initPumpState("6VeDeRAL5DCigGw2", 461710145L);
        
        CancelBolusResponse expected = new CancelBolusResponse(
            0, 10677, 0
        );

        CancelBolusResponse parsedRes = (CancelBolusResponse) MessageTester.test(
                "00bca1bc1d00b52900006923851bda3537a211cbd8ae95fec6d7a2a3d892d8f8af36bdc7",
                -68,
                1,
                CharacteristicUUID.CONTROL_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
        assertEquals(parsedRes.getStatus(), CancelBolusResponse.CancelStatus.SUCCESS);
        assertEquals(parsedRes.getReason(), CancelBolusResponse.CancelReason.NO_ERROR);
    }

    @Test
    public void testCancelBolusResponse_ID10678() throws DecoderException {
        // TimeSinceResetResponse[pumpTime=1200239,timeSinceReset=461710145]
        initPumpState("6VeDeRAL5DCigGw2", 461710145L);

        CancelBolusResponse expected = new CancelBolusResponse(
                0, 10678, 0
        );

        CancelBolusResponse parsedRes = (CancelBolusResponse) MessageTester.test(
                "00e8a1e81d00b62900009e23851b90d69beb26e8071804420d24cee664931b862a875929",
                -24,
                1,
                CharacteristicUUID.CONTROL_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
        assertEquals(parsedRes.getStatus(), CancelBolusResponse.CancelStatus.SUCCESS);
        assertEquals(parsedRes.getReason(), CancelBolusResponse.CancelReason.NO_ERROR);
    }

    @Test
    public void testCancelBolusResponse_ID10688_cancelledAfterCompletedDelivery() throws DecoderException {
        // authenticationKey=bJVYJq9jdtjxPcYa pumpTimeSinceReset=463692320
        initPumpState("bJVYJq9jdtjxPcYa", 463692320L);

        // CancelBolusResponse[bolusId=10688,reason=2,status=1,cargo={1,-64,41,2,0}]
        CancelBolusResponse expected = new CancelBolusResponse(
                1, 10688, 2
        );

        CancelBolusResponse parsedRes = (CancelBolusResponse) MessageTester.test(
                "0006a1061d01c02902004e62a31b93f40e8ae917aa40df366dd04d0cdb19d1c0fdd3b4f9",
                6,
                1,
                CharacteristicUUID.CONTROL_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
        assertEquals(parsedRes.getStatus(), CancelBolusResponse.CancelStatus.FAILED);
        assertEquals(parsedRes.getReason(), CancelBolusResponse.CancelReason.INVALID_OR_ALREADY_DELIVERED);
    }

    @Test
    public void testCancelBolusResponse_invalidId() throws DecoderException {
        // authenticationKey=bJVYJq9jdtjxPcYa pumpTimeSinceReset=463693031
        initPumpState("bJVYJq9jdtjxPcYa", 463693031L);

        // CancelBolusResponse[bolusId=34463,reasonId=2,statusId=1,cargo={1,-97,-122,2,0}]
        CancelBolusResponse expected = new CancelBolusResponse(
                1, 34463, 2
        );

        CancelBolusResponse parsedRes = (CancelBolusResponse) MessageTester.test(
                "0004a1041d019f8602000f65a31ba24ec318b5b102d56ee0db9463eb4d645ed2b2560905",
                4,
                1,
                CharacteristicUUID.CONTROL_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
        assertEquals(parsedRes.getStatus(), CancelBolusResponse.CancelStatus.FAILED);
        assertEquals(parsedRes.getReason(), CancelBolusResponse.CancelReason.INVALID_OR_ALREADY_DELIVERED);
    }

    @Test
    public void testCancelBolusResponse_Mobi_extendedbolus() throws DecoderException {
        // TimeSinceResetResponse[currentTime=512442842,pumpTimeSinceReset=1905413,cargo={-38,65,-117,30,5,19,29,0}]
        initPumpState(PacketArrayList.IGNORE_INVALID_HMAC, 1905413L);

        // CancelBolusResponse[bolusId=34463,reasonId=2,statusId=1,cargo={1,-97,-122,2,0}]
        CancelBolusResponse expected = new CancelBolusResponse(
                0, 501, 0
        );

        CancelBolusResponse parsedRes = (CancelBolusResponse) MessageTester.test(
                "002da12d1d00f50100007b428b1e59cc3479f5c851559f897f196f9511a5916f7499ff65",
                45,
                1,
                CharacteristicUUID.CONTROL_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
        assertEquals(parsedRes.getStatus(), CancelBolusResponse.CancelStatus.SUCCESS);
        assertEquals(parsedRes.getReason(), CancelBolusResponse.CancelReason.NO_ERROR);
    }
}