package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;
import static org.junit.Assert.assertEquals;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class BolusActivatedHistoryLogTest {
    @Test
    public void testBolusActivatedHistoryLog1() throws DecoderException {
        BolusActivatedHistoryLog expected = new BolusActivatedHistoryLog(
            // long pumpTimeSec, long sequenceNum, int bolusId, int selectedIob, float iob, float bolusSize
            446033777L, 182751L, 1037, 1, 0.0F, 1.1F
        );

        BolusActivatedHistoryLog parsedRes = (BolusActivatedHistoryLog) HistoryLogMessageTester.testSingle(
                "370071ef951adfc902000d04010000000000cdcc8c3f00000000",
                expected
        );
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }

    @Test
    public void testBolusActivatedHistoryLog2() throws DecoderException {
        BolusActivatedHistoryLog expected = new BolusActivatedHistoryLog(
                // long pumpTimeSec, long sequenceNum, int bolusId, int selectedIob, float iob, float bolusSize
                446141315L, 185926L, 1053, 1, 2.0116007F, 0.5F
        );

        BolusActivatedHistoryLog parsedRes = (BolusActivatedHistoryLog) HistoryLogMessageTester.testSingle(
                "37008393971a46d602001d04010011be00400000003f00000000",
                expected
        );
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }

    /**
     * Observed on a Tandem Mobi driven by Trio (Control-IQ off, no CGM paired), Sept 2026 BLE capture.
     *
     * <p>Remote (BLE) bolus id 2903 with header high nibble 1. Across 156 records in the capture,
     * bolusId at bytes 10-11 matched the paired BolusRequestedMsg1/2/3, BolusDelivery and
     * BolusCompleted ids in 156/156. Byte 11 (0x0B) is the high byte of the id, which refutes the
     * cloud-mirror placement of selectedIob at byte 11. Byte 12 (selectedIob) was 0 in all 156,
     * equal to selectedIOB at byte 24 of the paired BolusRequestedMsg2 in every case.
     */
    @Test
    public void testBolusActivatedHistoryLogMobiRemoteBolus() throws DecoderException {
        BolusActivatedHistoryLog expected = new BolusActivatedHistoryLog(
                // long pumpTimeSec, long sequenceNum, int bolusId, int selectedIob, float iob, float bolusSize, int headerHighNibble
                589526845L, 640754L, 2903, 0,
                Float.intBitsToFloat(0x3f9eec42), // ~1.2416, bytes 42ec9e3f
                Float.intBitsToFloat(0x3e19999a), // 0.15, bytes 9a99193e
                1
        );

        BolusActivatedHistoryLog parsedRes = (BolusActivatedHistoryLog) HistoryLogMessageTester.testSingle(
                "37103d772323f2c60900570b000042ec9e3f9a99193e00000000",
                expected
        );
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());

        assertEquals(1, parsedRes.getHeaderHighNibble());
        assertEquals(2903, parsedRes.getBolusId());
        assertEquals(0, parsedRes.getSelectedIob());
        assertEquals(BolusRequestedMsg2HistoryLog.SelectedIOBType.MUDALIAR_IOB, parsedRes.getSelectedIobType());
        assertEquals(0x3f9eec42, Float.floatToIntBits(parsedRes.getIob()));
        assertEquals(0x3e19999a, Float.floatToIntBits(parsedRes.getBolusSize()));
    }
}
