package com.jwoglom.pumpx2.pump.messages.response.currentStatus;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.messages.bluetooth.CharacteristicUUID;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class LastBolusStatusResponseTest {
    @Test
    public void testLastBolusStatusResponse() throws DecoderException { 
        LastBolusStatusResponse expected = new LastBolusStatusResponse(
            // int status, int bolusId, long timestamp, long deliveredVolume, int bolusStatusId, int bolusSourceId, int bolusTypeBitmask, long extendedBolusDuration
                1, 10690, 463703230, 200, 3, 8, 8, 0, new byte[2]
        );

        LastBolusStatusResponse parsedRes = (LastBolusStatusResponse) MessageTester.test(
                "000531051401c2290000be8ca31bc800000003080800000000bc1f",
                5,
                2,
                CharacteristicUUID.CURRENT_STATUS_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }
}