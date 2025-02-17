package com.jwoglom.pumpx2.pump.messages.response.currentStatus;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.messages.bluetooth.CharacteristicUUID;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class UnknownMobiOpcode30ResponseTest {
    @Test
    public void testUnknownMobiOpcode30Response() throws DecoderException { 
        UnknownMobiOpcode30Response expected = new UnknownMobiOpcode30Response(
            new byte[]{
                    0,
                    0,
                    0,
                    0,
                    0,
                    0,
                    0,
                    0,
                    -1,
                    -1,
                    -1,
                    -1,
                    0,
                    0,
                    0,
                    0
            }
        );

        UnknownMobiOpcode30Response parsedRes = (UnknownMobiOpcode30Response) MessageTester.test(
                "00311f31100000000000000000ffffffff0000000023ff",
                49,
                2,
                CharacteristicUUID.CURRENT_STATUS_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }
}