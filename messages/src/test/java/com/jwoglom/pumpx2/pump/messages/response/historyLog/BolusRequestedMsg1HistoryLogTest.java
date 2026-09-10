package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.messages.bluetooth.CharacteristicUUID;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class BolusRequestedMsg1HistoryLogTest {
    @Test
    public void testBolusRequestedMsg1HistoryLog1() throws DecoderException {
        BolusRequestedMsg1HistoryLog expected = new BolusRequestedMsg1HistoryLog(
            // long pumpTimeSec, long sequenceNum, int bolusId, int bolusType, boolean correctionBolusIncluded, int carbAmount, int bg, float iob, long carbRatio
                445997729L, 181895L, 1036, 2, true, 0, 0, 2.1372654F, 0
        );

        BolusRequestedMsg1HistoryLog parsedRes = (BolusRequestedMsg1HistoryLog) HistoryLogMessageTester.testSingle(
                "4000a162951a87c602000c04020100000000f5c8084000000000",
                expected
        );
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }

    @Test
    public void testBolusRequestedMsg1HistoryLog2() throws DecoderException {
        BolusRequestedMsg1HistoryLog expected = new BolusRequestedMsg1HistoryLog(
                // 0L, 0L, long pumpTimeSec, long sequenceNum, int bolusId, int bolusType, boolean correctionBolusIncluded, int carbAmount, int bg, float iob, long carbRatio
                446158654L, 186468L, 1057, 2, true, 0, 0, 1.8658969F, 0
        );

        BolusRequestedMsg1HistoryLog parsedRes = (BolusRequestedMsg1HistoryLog) HistoryLogMessageTester.testSingle(
                "40003ed7971a64d802002104020100000000b6d5ee3f00000000",
                expected
        );
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }

    @Test
    public void testBolusRequestedMsg1HistoryLog3() throws DecoderException {
        BolusRequestedMsg1HistoryLog expected = new BolusRequestedMsg1HistoryLog(
                // long pumpTimeSec, long sequenceNum, int bolusId, int bolusType, boolean correctionBolusIncluded, int carbAmount, int bg, float iob, long carbRatio
                446056900L, 183357L, 1040, 1, false, 0, 0, 1.7629877F, 6000
        );

        BolusRequestedMsg1HistoryLog parsedRes = (BolusRequestedMsg1HistoryLog) HistoryLogMessageTester.testSingle(
                "4000c449961a3dcc0200100401000000000095a9e13f70170000",
                expected
        );
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }

    /**
     * Observed on a Tandem Mobi driven by Trio (Control-IQ off, no CGM paired), Sept 2026 BLE capture.
     *
     * Remote (BLE) bolus id 2903, requested without any calculator inputs: bolusTypeId=3
     * ({@link BolusRequestedMsg1HistoryLog.BolusType#REMOTE}), no correction bolus, carbAmount=0,
     * bg=0, iob=0.0 and carbRatio=0. Across 156 BLE-initiated boluses in the capture the
     * bolusTypeId at byte 12 was 3 in 156/156 and the bolusId at bytes 10-11 matched the paired
     * BolusActivated/BolusDelivery records in 156/156. Header high nibble is 1 on this pump.
     */
    @Test
    public void testBolusRequestedMsg1HistoryLog4_mobiRemoteBolus() throws DecoderException {
        BolusRequestedMsg1HistoryLog expected = new BolusRequestedMsg1HistoryLog(
                // long pumpTimeSec, long sequenceNum, int bolusId, int bolusType, boolean correctionBolusIncluded, int carbAmount, int bg, float iob, long carbRatio, int headerHighNibble
                589526830L, 640746L, 2903, 3, false, 0, 0, 0.0F, 0, 1
        );

        BolusRequestedMsg1HistoryLog parsedRes = (BolusRequestedMsg1HistoryLog) HistoryLogMessageTester.testSingle(
                "40102e772323eac60900570b0300000000000000000000000000",
                expected
        );
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());

        assertEquals(2903, parsedRes.getBolusId());
        assertEquals(3, parsedRes.getBolusTypeId());
        assertEquals(BolusRequestedMsg1HistoryLog.BolusType.REMOTE, parsedRes.getBolusType());
        assertFalse(parsedRes.getCorrectionBolusIncluded());
        assertEquals(0, parsedRes.getCarbAmount());
        assertEquals(0, parsedRes.getBg());
        assertEquals(0.0F, parsedRes.getIob(), 0.0F);
        assertEquals(0L, parsedRes.getCarbRatio());
    }
}
