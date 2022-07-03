package com.jwoglom.pumpx2.pump.messages.response.currentStatus;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.bluetooth.CharacteristicUUID;
import com.jwoglom.pumpx2.pump.messages.response.currentStatus.ProfileStatusResponse;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class ProfileStatusResponseTest {
    @Test
    public void testProfileStatusResponse() throws DecoderException {
        ProfileStatusResponse expected = new ProfileStatusResponse(
            // int numberOfProfiles, int idpSlot0Id, int idpSlot1Id, int idpSlot2Id, int idpSlot3Id, int idpSlot4Id, int idpSlot5Id, int activeSegmentIndex
            1, 0, -1, -1, -1, -1, -1, 3
        );

        ProfileStatusResponse parsedRes = (ProfileStatusResponse) MessageTester.test(
                "00033f03080100ffffffffff03d4c6",
                3,
                1,
                CharacteristicUUID.CURRENT_STATUS_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }
}