package com.jwoglom.pumpx2.pump.messages.response.currentStatus;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static com.jwoglom.pumpx2.pump.messages.response.currentStatus.IDPSegmentResponse.IDPSegmentStatus;

import com.google.common.collect.ImmutableSet;
import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.messages.bluetooth.CharacteristicUUID;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class IDPSegmentResponseTest {
//    @Test
//    public void testIDPSegmentResponse1() throws DecoderException {
//        // Response to IDPSegmentRequest(0, 0)
//        // 12am: 0.8 Basal, 1:30 correct, 1:6 carb, 110 target BG (TODO: returned 120, is this stored somewhere else?)
//        IDPSegmentResponse expected = new IDPSegmentResponse(
//            // int idpId, int segmentIndex, int profileStartTime, int profileBasalRate, long profileCarbRatio, int profileTargetBG, int profileISF, int status
//                0, 0, 0, 800, 6000, 120, 30, 15
//        );
//
//        IDPSegmentResponse parsedRes = (IDPSegmentResponse) MessageTester.test(
//                "000443040f0000000020037017000078001e000f3078",
//                4,
//                2,
//                CharacteristicUUID.CURRENT_STATUS_CHARACTERISTICS,
//                expected
//        );
//
//        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
//        assertEquals(ImmutableSet.of(IDPSegmentStatus.CORRECTION_FACTOR, IDPSegmentStatus.TARGET_BG, IDPSegmentStatus.CARB_RATIO, IDPSegmentStatus.BASAL_RATE), parsedRes.getStatus());
//    }
//
//    @Test
//    public void testIDPSegmentResponse2() throws DecoderException {
//        // Response to IDPSegmentRequest(0, 1)
//        // 6am: 1.25 Basal, 1:30 correct, 1:6 carb, 110 target BG (TODO: returned 120, is this stored somewhere else?)
//        IDPSegmentResponse expected = new IDPSegmentResponse(
//                // int idpId, int segmentIndex, int profileStartTime, int profileBasalRate, long profileCarbRatio, int profileTargetBG, int profileISF, int status
//                0, 1, 360, 1250, 6000, 120, 30, 15
//        );
//
//        IDPSegmentResponse parsedRes = (IDPSegmentResponse) MessageTester.test(
//                "000543050f00016801e2047017000078001e000f121d",
//                5,
//                2,
//                CharacteristicUUID.CURRENT_STATUS_CHARACTERISTICS,
//                expected
//        );
//
//        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
//    }
//
//    @Test
//    public void testIDPSegmentResponse3() throws DecoderException {
//        // Response to IDPSegmentRequest(0, 2)
//        // 11am: 1 Basal, 1:30 correct, 1:6 carb, 110 target BG (TODO: returned 120, is this stored somewhere else?)
//        IDPSegmentResponse expected = new IDPSegmentResponse(
//                // int idpId, int segmentIndex, int profileStartTime, int profileBasalRate, long profileCarbRatio, int profileTargetBG, int profileISF, int status
//                0, 2, 660, 1000, 6000, 120, 30, 15
//        );
//
//        IDPSegmentResponse parsedRes = (IDPSegmentResponse) MessageTester.test(
//                "000643060f00029402e8037017000078001e000f9460",
//                6,
//                2,
//                CharacteristicUUID.CURRENT_STATUS_CHARACTERISTICS,
//                expected
//        );
//
//        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
//    }
//
//    @Test
//    public void testIDPSegmentResponse4() throws DecoderException {
//        // Response to IDPSegmentRequest(0, 3)
//        // 12pm: 0.8 Basal, 1:30 correct, 1:6 carb, 110 target BG (TODO: returned 120, is this stored somewhere else?)
//        IDPSegmentResponse expected = new IDPSegmentResponse(
//                // int idpId, int segmentIndex, int profileStartTime, int profileBasalRate, long profileCarbRatio, int profileTargetBG, int profileISF, int status
//                0, 3, 720, 800, 6000, 120, 30, 15
//        );
//
//        IDPSegmentResponse parsedRes = (IDPSegmentResponse) MessageTester.test(
//                "000743070f0003d00220037017000078001e000f9b3b",
//                7,
//                2,
//                CharacteristicUUID.CURRENT_STATUS_CHARACTERISTICS,
//                expected
//        );
//
//        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
//    }
//
//
    @Test
    public void testIDPSegmentResponse_Profile1_Midnight_NoBasalOrTargetBg() throws DecoderException {
        // Response to IDPSegmentRequest(1, 0)
        // second IDP profile, first index
        // 12pm: 0 Basal, 1:99 correct, 1:99 carb, no target BG
        IDPSegmentResponse expected = new IDPSegmentResponse(
                // int idpId, int segmentIndex, int profileStartTime, int profileBasalRate, long profileCarbRatio, int profileTargetBG, int profileISF, int status
                1, 0, 0, 0, 99000, 0, 99, 11
        );

        IDPSegmentResponse parsedRes = (IDPSegmentResponse) MessageTester.test(
                "000643060f010000000000b8820100000063000bb5d0",
                6,
                2,
                CharacteristicUUID.CURRENT_STATUS_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
        assertEquals(ImmutableSet.of(IDPSegmentStatus.BASAL_RATE, IDPSegmentStatus.CARB_RATIO, IDPSegmentStatus.CORRECTION_FACTOR), parsedRes.getStatus());
    }


    @Test
    public void testIDPSegmentResponse_Profile1_Midnight_NoBasal() throws DecoderException {
        // Response to IDPSegmentRequest(1, 0)
        // second IDP profile, first index
        // 12pm: 0 Basal, 1:99 correct, 1:99 carb, 130 target BG
        IDPSegmentResponse expected = new IDPSegmentResponse(
                // int idpId, int segmentIndex, int profileStartTime, int profileBasalRate, long profileCarbRatio, int profileTargetBG, int profileISF, int status
                1, 0, 0, 0, 99000, 130, 99, 15
        );

        IDPSegmentResponse parsedRes = (IDPSegmentResponse) MessageTester.test(
                "000743070f010000000000b8820100820063000f80e6",
                7,
                2,
                CharacteristicUUID.CURRENT_STATUS_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
        assertEquals(ImmutableSet.of(IDPSegmentStatus.BASAL_RATE, IDPSegmentStatus.CARB_RATIO, IDPSegmentStatus.TARGET_BG, IDPSegmentStatus.CORRECTION_FACTOR), parsedRes.getStatus());
    }

    @Test
    public void testIDPSegmentResponse_Profile1_Midnight_All() throws DecoderException {
        // Response to IDPSegmentRequest(1, 0)
        // second IDP profile, first index
        // 12pm: 0.20u/hr Basal, 1:99 correct, 1:99 carb, 130 target BG
        IDPSegmentResponse expected = new IDPSegmentResponse(
                // int idpId, int segmentIndex, int profileStartTime, int profileBasalRate, long profileCarbRatio, int profileTargetBG, int profileISF, int status
                1, 0, 0, 200, 99000, 130, 99, 15
        );

        IDPSegmentResponse parsedRes = (IDPSegmentResponse) MessageTester.test(
                "000843080f01000000c800b8820100820063000fcf7c",
                8,
                2,
                CharacteristicUUID.CURRENT_STATUS_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
        assertEquals(ImmutableSet.of(IDPSegmentStatus.BASAL_RATE, IDPSegmentStatus.CARB_RATIO, IDPSegmentStatus.TARGET_BG, IDPSegmentStatus.CORRECTION_FACTOR), parsedRes.getStatus());
    }

    @Test
    public void testIDPSegmentResponse_Profile1_Noon_All() throws DecoderException {
        // Response to IDPSegmentRequest(1, 1)
        // second IDP profile, second index
        // 12pm: 0.40u/hr Basal, 1:66 correct, 1:66 carb, 140 target BG
        IDPSegmentResponse expected = new IDPSegmentResponse(
                // int idpId, int segmentIndex, int profileStartTime, int profileBasalRate, long profileCarbRatio, int profileTargetBG, int profileISF, int status
                1, 1, 720, 400, 66000, 140, 66, 15
        );

        IDPSegmentResponse parsedRes = (IDPSegmentResponse) MessageTester.test(
                "000843080f01000000c800b8820100820063000fcf7c",
                8,
                2,
                CharacteristicUUID.CURRENT_STATUS_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
        assertEquals(ImmutableSet.of(IDPSegmentStatus.BASAL_RATE, IDPSegmentStatus.CARB_RATIO, IDPSegmentStatus.TARGET_BG, IDPSegmentStatus.CORRECTION_FACTOR), parsedRes.getStatus());
    }

    // Response to IDPSegmentRequest(1, 2) when there are only 2 IDP segments (e.g. an invalid entry)
    // is ErrorResponse[errorCode=UNDEFINED_ERROR,errorCodeId=0]


    @Test
    public void testIDPSegmentStatus() {
        assertTrue((IDPSegmentStatus.toBitmask(IDPSegmentStatus.BASAL_RATE) & 1) != 0);
        assertTrue((IDPSegmentStatus.toBitmask(IDPSegmentStatus.CARB_RATIO) & 2) != 0);
        assertTrue((IDPSegmentStatus.toBitmask(IDPSegmentStatus.TARGET_BG) & 4) != 0);
        assertTrue((IDPSegmentStatus.toBitmask(IDPSegmentStatus.CORRECTION_FACTOR) & 8) != 0);

        assertEquals(IDPSegmentStatus.fromBitmask(1), ImmutableSet.of(IDPSegmentStatus.BASAL_RATE));
        assertEquals(IDPSegmentStatus.fromBitmask(2), ImmutableSet.of(IDPSegmentStatus.CARB_RATIO));
        assertEquals(IDPSegmentStatus.fromBitmask(3), ImmutableSet.of(IDPSegmentStatus.BASAL_RATE, IDPSegmentStatus.CARB_RATIO));
        assertEquals(IDPSegmentStatus.fromBitmask(4), ImmutableSet.of(IDPSegmentStatus.TARGET_BG));
        assertEquals(IDPSegmentStatus.fromBitmask(5), ImmutableSet.of(IDPSegmentStatus.TARGET_BG, IDPSegmentStatus.BASAL_RATE));
        assertEquals(IDPSegmentStatus.fromBitmask(6), ImmutableSet.of(IDPSegmentStatus.TARGET_BG, IDPSegmentStatus.CARB_RATIO));
        assertEquals(IDPSegmentStatus.fromBitmask(7), ImmutableSet.of(IDPSegmentStatus.TARGET_BG, IDPSegmentStatus.CARB_RATIO, IDPSegmentStatus.BASAL_RATE));
    }

}