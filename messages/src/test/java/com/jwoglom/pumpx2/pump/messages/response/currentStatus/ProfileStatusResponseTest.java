package com.jwoglom.pumpx2.pump.messages.response.currentStatus;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.messages.bluetooth.CharacteristicUUID;

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

    @Test
    public void testProfileStatusResponse_twoProfiles() throws DecoderException {
        // first profile is active
        ProfileStatusResponse expected = new ProfileStatusResponse(
                // int numberOfProfiles, int idpSlot0Id, int idpSlot1Id, int idpSlot2Id, int idpSlot3Id, int idpSlot4Id, int idpSlot5Id, int activeSegmentIndex
                2, 0, 1, -1, -1, -1, -1, 3
        );

        ProfileStatusResponse parsedRes = (ProfileStatusResponse) MessageTester.test(
                "00043f0408020001ffffffff034403",
                4,
                1,
                CharacteristicUUID.CURRENT_STATUS_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }


    @Test
    public void testProfileStatusResponse_twoProfiles_secondActive() throws DecoderException {
        // second profile is active. so the ID previously in idpSlot1 is now swapped with idoSlot0
        ProfileStatusResponse expected = new ProfileStatusResponse(
                // int numberOfProfiles, int idpSlot0Id, int idpSlot1Id, int idpSlot2Id, int idpSlot3Id, int idpSlot4Id, int idpSlot5Id, int activeSegmentIndex
                2, 1, 0, -1, -1, -1, -1, 1
        );

        ProfileStatusResponse parsedRes = (ProfileStatusResponse) MessageTester.test(
                "00113f1108020100ffffffff012188",
                17,
                1,
                CharacteristicUUID.CURRENT_STATUS_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }
}