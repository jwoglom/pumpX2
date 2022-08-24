package com.jwoglom.pumpx2.pump.messages.response.control;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;
import static com.jwoglom.pumpx2.pump.messages.MessageTester.initPumpState;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
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
    }
}