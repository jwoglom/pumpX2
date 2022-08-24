package com.jwoglom.pumpx2.pump.messages.request.control;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;
import static com.jwoglom.pumpx2.pump.messages.MessageTester.initPumpState;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.messages.bluetooth.CharacteristicUUID;
import com.jwoglom.pumpx2.pump.messages.request.control.CancelBolusRequest;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class CancelBolusRequestTest {
    @Test
    public void testCancelBolusRequest_ID10677() throws DecoderException {
        // TimeSinceResetResponse[pumpTime=1200239,timeSinceReset=461710145]
        initPumpState("6VeDeRAL5DCigGw2", 461710145L);
        CancelBolusRequest expected = new CancelBolusRequest(
                10677
        );

        CancelBolusRequest parsedReq = (CancelBolusRequest) MessageTester.test(
                "01bca0bc1cb52900004123851bde7e214f942297",
                -68,
                1,
                CharacteristicUUID.CONTROL_CHARACTERISTICS,
                expected,
                "00bcea477474bb87dbb3293c92642f43e8"
        );
        // response 00bca1bc1d00b52900006923851bda3537a211cbd8ae95fec6d7a2a3d892d8f8af36bdc7

        assertHexEquals(expected.getCargo(), parsedReq.getCargo());
    }

    @Test
    public void testCancelBolusRequest_ID10678() throws DecoderException {
        // TimeSinceResetResponse[pumpTime=1200239,timeSinceReset=461710145]
        initPumpState("6VeDeRAL5DCigGw2", 461710145L);
        CancelBolusRequest expected = new CancelBolusRequest(
                10678
        );

        CancelBolusRequest parsedReq = (CancelBolusRequest) MessageTester.test(
                "01e8a0e81cb62900007a23851b1b100405c517c0",
                -24,
                1,
                CharacteristicUUID.CONTROL_CHARACTERISTICS,
                expected,
                "00e887856f69ef78c79bf186fa39a0c5d1"
        );
        // response 00e8a1e81d00b62900009e23851b90d69beb26e8071804420d24cee664931b862a875929

        assertHexEquals(expected.getCargo(), parsedReq.getCargo());
    }
}