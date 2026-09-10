package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;

import static org.junit.Assert.assertEquals;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.messages.bluetooth.CharacteristicUUID;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class BolusRequestedMsg3HistoryLogTest {
    @Test
    public void testBolusRequestedMsg3HistoryLog() throws DecoderException {
        BolusRequestedMsg3HistoryLog expected = new BolusRequestedMsg3HistoryLog(
            // long pumpTimeSec, long sequenceNum, int bolusId, int spare, float foodBolusSize, float correctionBolusSize, float totalBolusSize
                445992481L, 181642L, 1032, 0, 3.33F, 0.0F, 3.33F
        );

        BolusRequestedMsg3HistoryLog parsedRes = (BolusRequestedMsg3HistoryLog) HistoryLogMessageTester.testSingle(
                "4200214e951a8ac5020008040000b81e554000000000b81e5540",
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }

    @Test
    public void testBolusRequestedMsg3HistoryLog2() throws DecoderException {
        // BolusRequestedMsg3HistoryLog[bolusId=1066,correctionBolusSize=0.27,foodBolusSize=6.67,spare=0,totalBolusSize=6.94,cargo={66,0,77,-82,-104,26,48,-34,2,0,42,4,0,0,-92,112,-43,64,113,61,-118,62,123,20,-34,64},pumpTimeSec=446213709,sequenceNum=187952]
        BolusRequestedMsg3HistoryLog expected = new BolusRequestedMsg3HistoryLog(
                // long pumpTimeSec, long sequenceNum, int bolusId, int spare, float foodBolusSize, float correctionBolusSize, float totalBolusSize
                446213709L, 187952L, 1066, 0, 6.67F, 0.27F, 6.94F
        );

        BolusRequestedMsg3HistoryLog parsedRes = (BolusRequestedMsg3HistoryLog) HistoryLogMessageTester.testSingle(
                "42004dae981a30de02002a040000a470d540713d8a3e7b14de40",
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }

    // Observed on a Tandem Mobi driven by Trio (Control-IQ off, no CGM paired), Sept 2026 BLE capture.
    // Remote (BLE) bolus id 2903 for 0.15 U: bolusId@10 matched the paired BolusActivated / BolusDelivery
    // records in 156/156 boluses, spare@12 was 0 in 156/156, and foodBolusSize == totalBolusSize (correction 0).
    // Header high nibble is 1 on this firmware. Floats are built from the exact bit pattern so the
    // cargo round-trip is byte-exact.
    @Test
    public void testBolusRequestedMsg3HistoryLog_mobiRemoteBolus() throws DecoderException {
        float bolusSize = Float.intBitsToFloat(0x3e19999a); // 0.15 U (bytes 9a99193e)
        BolusRequestedMsg3HistoryLog expected = (BolusRequestedMsg3HistoryLog) new BolusRequestedMsg3HistoryLog(
                // long pumpTimeSec, long sequenceNum, int bolusId, int spare, float foodBolusSize, float correctionBolusSize, float totalBolusSize
                589526830L, 640748L, 2903, 0, bolusSize, 0.0F, bolusSize
        ).withHeaderHighNibble(1);

        BolusRequestedMsg3HistoryLog parsedRes = (BolusRequestedMsg3HistoryLog) HistoryLogMessageTester.testSingle(
                "42102e772323ecc60900570b00009a99193e000000009a99193e",
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
        assertEquals(2903, parsedRes.getBolusId());
        assertEquals(0, parsedRes.getSpare());
        assertEquals(0x3e19999a, Float.floatToIntBits(parsedRes.getFoodBolusSize()));
        assertEquals(0.0F, parsedRes.getCorrectionBolusSize(), 0.0F);
        assertEquals(parsedRes.getFoodBolusSize(), parsedRes.getTotalBolusSize(), 0.0F);
    }
}
