package com.jwoglom.pumpx2.pump.messages.response.currentStatus;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.bluetooth.CharacteristicUUID;
import com.jwoglom.pumpx2.pump.messages.response.currentStatus.IDPSegmentResponse;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class IDPSegmentResponseTest {
    @Test
    public void testIDPSegmentResponse1() throws DecoderException {
        // Response to IDPSegmentRequest(0, 0)
        // 12am: 0.8 Basal, 1:30 correct, 1:6 carb, 110 target BG (TODO: returned 120, is this stored somewhere else?)
        IDPSegmentResponse expected = new IDPSegmentResponse(
            // int idpId, int segmentIndex, int profileStartTime, int profileBasalRate, long profileCarbRatio, int profileTargetBG, int profileISF, int status
                0, 0, 0, 800, 6000, 120, 30, 15
        );

        IDPSegmentResponse parsedRes = (IDPSegmentResponse) MessageTester.test(
                "000443040f0000000020037017000078001e000f3078",
                4,
                1,
                CharacteristicUUID.CURRENT_STATUS_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }

    @Test
    public void testIDPSegmentResponse2() throws DecoderException {
        // Response to IDPSegmentRequest(0, 1)
        // 6am: 1.25 Basal, 1:30 correct, 1:6 carb, 110 target BG (TODO: returned 120, is this stored somewhere else?)
        IDPSegmentResponse expected = new IDPSegmentResponse(
                // int idpId, int segmentIndex, int profileStartTime, int profileBasalRate, long profileCarbRatio, int profileTargetBG, int profileISF, int status
                0, 1, 360, 1250, 6000, 120, 30, 15
        );

        IDPSegmentResponse parsedRes = (IDPSegmentResponse) MessageTester.test(
                "000543050f00016801e2047017000078001e000f121d",
                5,
                1,
                CharacteristicUUID.CURRENT_STATUS_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }

    @Test
    public void testIDPSegmentResponse3() throws DecoderException {
        // Response to IDPSegmentRequest(0, 2)
        // 11am: 1 Basal, 1:30 correct, 1:6 carb, 110 target BG (TODO: returned 120, is this stored somewhere else?)
        IDPSegmentResponse expected = new IDPSegmentResponse(
                // int idpId, int segmentIndex, int profileStartTime, int profileBasalRate, long profileCarbRatio, int profileTargetBG, int profileISF, int status
                0, 2, 660, 1000, 6000, 120, 30, 15
        );

        IDPSegmentResponse parsedRes = (IDPSegmentResponse) MessageTester.test(
                "000643060f00029402e8037017000078001e000f9460",
                6,
                1,
                CharacteristicUUID.CURRENT_STATUS_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }

    @Test
    public void testIDPSegmentResponse4() throws DecoderException {
        // Response to IDPSegmentRequest(0, 3)
        // 12pm: 0.8 Basal, 1:30 correct, 1:6 carb, 110 target BG (TODO: returned 120, is this stored somewhere else?)
        IDPSegmentResponse expected = new IDPSegmentResponse(
                // int idpId, int segmentIndex, int profileStartTime, int profileBasalRate, long profileCarbRatio, int profileTargetBG, int profileISF, int status
                0, 3, 720, 800, 6000, 120, 30, 15
        );

        IDPSegmentResponse parsedRes = (IDPSegmentResponse) MessageTester.test(
                "000743070f0003d00220037017000078001e000f9b3b",
                7,
                1,
                CharacteristicUUID.CURRENT_STATUS_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }
}