package com.jwoglom.pumpx2.pump.messages.response.currentStatus;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.messages.bluetooth.CharacteristicUUID;
import com.jwoglom.pumpx2.pump.messages.response.currentStatus.BolusCalcDataSnapshotResponse;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class BolusCalcDataSnapshotResponseTest {
    @Test
    public void testBolusCalcDataSnapshotResponse1() throws DecoderException {
        BolusCalcDataSnapshotResponse expected = new BolusCalcDataSnapshotResponse(
            // boolean isUnacked, int correctionFactor, long iob, int cartridgeRemainingInsulin, int targetBg, int isf, boolean carbEntryEnabled, long carbRatio, int maxBolusAmount, long maxBolusHourlyTotal, boolean maxBolusEventsExceeded, boolean maxIobEventsExceeded, boolean isAutopopAllowed
                false, 209, 2937, 120, 110, 30, true, 6000, 25000, 2810, false, false, true,
                // authentication hmac bytes
                new byte[]{-112,-87,84,27,-35,15,0,0,1,-5,1},
                new byte[]{85,85,85,85,85,85,85,85}
        );
        BolusCalcDataSnapshotResponse parsedRes = (BolusCalcDataSnapshotResponse) MessageTester.test(
                "000373032e00d100790b000078006e001e000170170000a861fa0a0000000090a9541bdd0f000001fb0101555555555555555571b3",
                3,
                3,
                CharacteristicUUID.CURRENT_STATUS_CHARACTERISTICS,
                expected
        );

         assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }

    @Test
    public void testBolusCalcDataSnapshotResponse2() throws DecoderException {
        BolusCalcDataSnapshotResponse expected = new BolusCalcDataSnapshotResponse(
                // boolean isUnacked, int correctionFactor, long iob, int cartridgeRemainingInsulin, int targetBg, int isf, boolean carbEntryEnabled, long carbRatio, int maxBolusAmount, long maxBolusHourlyTotal, boolean maxBolusEventsExceeded, boolean maxIobEventsExceeded, boolean isAutopopAllowed
                false, 174, 2302, 120, 110, 30, true, 6000, 25000, 0, false, false, true,
                // authentication hmac bytes
                new byte[]{64,-82,84,27,-115,20,0,0,1,-16,1},
                new byte[]{85,85,85,85,85,85,85,85}
        );
        BolusCalcDataSnapshotResponse parsedRes = (BolusCalcDataSnapshotResponse) MessageTester.test(
                "000473042e00ae00fe08000078006e001e000170170000a86100000000000040ae541b8d14000001f001015555555555555555d2d7",
                4,
                3,
                CharacteristicUUID.CURRENT_STATUS_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }
}