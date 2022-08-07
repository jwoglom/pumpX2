package com.jwoglom.pumpx2.pump.messages.response.control;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;
import static com.jwoglom.pumpx2.pump.messages.MessageTester.initPumpState;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.messages.bluetooth.CharacteristicUUID;
import com.jwoglom.pumpx2.pump.messages.bluetooth.PumpStateSupplier;

import org.apache.commons.codec.DecoderException;
import org.junit.Ignore;
import org.junit.Test;

public class ChangeCartridgeResponseTest {
    /**
     * with no message signing on request or response, returns cargo of 100 100 followed by 0s TO CURRENT_STATUS
     *
     * with message signing on request and response, returns:
     * ErrorResponse[errorCode=UNDEFINED_ERROR,errorCodeId=0,requestCodeId=0,cargo={0,0}]
     * (invalid message signing?)
     */
    @Test
    @Ignore("WIP; need to fix duplicate UUID across UUIDs: https://github.com/jwoglom/pumpX2/issues/2")
    public void testChangeCartridgeResponseNoSignature() throws DecoderException {
        ChangeCartridgeResponse expected = new ChangeCartridgeResponse(
            // int status, int unknown1
            100, 100
        );

        ChangeCartridgeResponse parsedRes = (ChangeCartridgeResponse) MessageTester.test(
                "000491040b6464000000000000000000d201",
                4,
                1,
                CharacteristicUUID.CONTROL_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }
}