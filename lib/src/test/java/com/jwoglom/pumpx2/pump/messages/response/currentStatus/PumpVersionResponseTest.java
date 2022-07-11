package com.jwoglom.pumpx2.pump.messages.response.currentStatus;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.bluetooth.CharacteristicUUID;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class PumpVersionResponseTest {
    @Test
    public void testPumpVersionResponse() throws DecoderException {
        PumpVersionResponse expected = new PumpVersionResponse(
            // long armSwVer, long mspSwVer, long configABits, long configBBits, long serialNum, long partNum, String pumpRev, long pcbaSN, String pcbaRev, long modelNum
                105900L, 105900L, 0L, 0L, 90556643L, 1005279L, "0", 1088111696L, "A", 1000354L
        );

        PumpVersionResponse parsedRes = (PumpVersionResponse) MessageTester.test(
                "0003550330ac9d0100ac9d01000000000000000000e3c86505df560f0030000000000000005044db404100000000000000a2430f008722",
                3,
                3,
                CharacteristicUUID.CURRENT_STATUS_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }
}