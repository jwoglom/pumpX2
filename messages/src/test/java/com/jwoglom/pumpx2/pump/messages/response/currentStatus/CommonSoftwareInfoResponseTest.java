package com.jwoglom.pumpx2.pump.messages.response.currentStatus;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.messages.bluetooth.CharacteristicUUID;

import org.apache.commons.codec.DecoderException;
import org.junit.Ignore;
import org.junit.Test;

public class CommonSoftwareInfoResponseTest {
    @Test
    public void testCommonSoftwareInfoResponse() throws DecoderException { 
        CommonSoftwareInfoResponse expected = new CommonSoftwareInfoResponse(
            new byte[]{
                    0,
                    102,
                    99,
                    56,
                    50,
                    101,
                    52,
                    57,
                    53,
                    100,
                    100,
                    101,
                    56,
                    50,
                    102,
                    99,
                    102,
                    0,
                    22,
                    122,
                    15,
                    0,
                    0,
                    0,
                    0,
                    0,
                    102,
                    99,
                    56,
                    50,
                    101,
                    52,
                    57,
                    53,
                    100,
                    100,
                    101,
                    56,
                    50,
                    102,
                    99,
                    102,
                    0,
                    22,
                    122,
                    15,
                    0,
                    0,
                    0,
                    0,
                    0
            }
        );

        CommonSoftwareInfoResponse parsedRes = (CommonSoftwareInfoResponse) MessageTester.test(
                "002f8f2f33006663383265343935646465383266636600167a0f00000000006663383265343935646465383266636600167a0f00000000000457",
                47,
                4,
                CharacteristicUUID.CURRENT_STATUS_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }
}