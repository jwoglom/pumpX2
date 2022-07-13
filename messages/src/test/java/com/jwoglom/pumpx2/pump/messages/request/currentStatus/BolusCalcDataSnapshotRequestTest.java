package com.jwoglom.pumpx2.pump.messages.request.currentStatus;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.messages.bluetooth.CharacteristicUUID;
import com.jwoglom.pumpx2.pump.messages.request.currentStatus.BolusCalcDataSnapshotRequest;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class BolusCalcDataSnapshotRequestTest {
    @Test
    public void testBolusCalcDataSnapshotRequest() throws DecoderException {
        // empty cargo
        BolusCalcDataSnapshotRequest expected = new BolusCalcDataSnapshotRequest();

        BolusCalcDataSnapshotRequest parsedReq = (BolusCalcDataSnapshotRequest) MessageTester.test(
                "0003720300a72f",
                3,
                1,
                CharacteristicUUID.CURRENT_STATUS_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedReq.getCargo());
    }
}