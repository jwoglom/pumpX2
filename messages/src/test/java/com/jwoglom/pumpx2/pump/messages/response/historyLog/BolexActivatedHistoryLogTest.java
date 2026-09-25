package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;
import static org.junit.Assert.assertEquals;

import com.jwoglom.pumpx2.shared.Hex;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class BolexActivatedHistoryLogTest {
    @Test
    public void testBolexActivatedHistoryLog_mobiExtendedBolus() throws DecoderException {
        // Tandem Mobi, official Tandem app, March 2025 BLE capture. Extended bolus 248: 2u, all of
        // it extended (BolusRequestedMsg2 options 5, standardPercent 0, 480 min), so the pump wrote
        // no BolusActivated (55). Msg2 selectedIOB was 0, matching byte 12. Header high nibble is 1.
        BolexActivatedHistoryLog expected = (BolexActivatedHistoryLog) new BolexActivatedHistoryLog(
                // long pumpTimeSec, long sequenceNum, int bolusId, int selectedIob, float iob, float bolexSize
                543715895L, 68927L, 248, 0, 0.0F, 2.0F
        ).withHeaderHighNibble(1);

        BolexActivatedHistoryLog parsedRes = (BolexActivatedHistoryLog) HistoryLogMessageTester.testSingle(
                "3b10377268203f0d0100f8000000000000000000004000000000",
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
        assertEquals(248, parsedRes.getBolusId());
        assertEquals(0, parsedRes.getSelectedIob());
        assertEquals(BolusRequestedMsg2HistoryLog.SelectedIOBType.MUDALIAR_IOB, parsedRes.getSelectedIobType());
        assertEquals(0.0F, parsedRes.getIob(), 0.0F);
        assertEquals(2.0F, parsedRes.getBolexSize(), 0.0F);
    }

    // The only opcode 59 record that can be published carries selectedIob 0, so selectedIob 1 is
    // covered by a SYNTHETIC record built to the same layout.
    @Test
    public void testBolexActivatedHistoryLog_syntheticRoundTrip() throws DecoderException {
        BolexActivatedHistoryLog expected = new BolexActivatedHistoryLog(
                // long pumpTimeSec, long sequenceNum, int bolusId, int selectedIob, float iob, float bolexSize
                1000L, 2000L, 0x1234, 1, 1.5F, 2.25F
        );

        // SYNTHETIC: 3b00 | e8030000 | d0070000 | 3412 | 01 00 | 0000c03f | 00001040 | 00000000
        BolexActivatedHistoryLog parsedRes = (BolexActivatedHistoryLog) HistoryLogMessageTester.testSingle(
                "3b00e8030000d0070000341201000000c03f0000104000000000",
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
        assertEquals(1, expected.getCargo()[12]);
        assertEquals(0, expected.getCargo()[13]);
        assertEquals(0x1234, parsedRes.getBolusId());
        assertEquals(1, parsedRes.getSelectedIob());
        assertEquals(BolusRequestedMsg2HistoryLog.SelectedIOBType.SWAN_IOB_MEAL, parsedRes.getSelectedIobType());
        assertEquals(1.5F, parsedRes.getIob(), 0.0F);
        assertEquals(2.25F, parsedRes.getBolexSize(), 0.0F);
    }

    @Test
    public void testBolexActivatedHistoryLog_syntheticHeaderHighNibble() throws DecoderException {
        BolexActivatedHistoryLog log = new BolexActivatedHistoryLog(1000L, 2000L, 0x1234, 0, 1.5F, 2.25F, 1);

        assertHexEquals(Hex.decodeHex("3b10e8030000d0070000341200000000c03f0000104000000000"), log.getCargo());
        assertEquals(BolusRequestedMsg2HistoryLog.SelectedIOBType.MUDALIAR_IOB, log.getSelectedIobType());
    }

    @Test
    @SuppressWarnings("deprecation")
    public void testBolexActivatedHistoryLog_deprecatedConstructorWritesZeroSelectedIob() {
        BolexActivatedHistoryLog log = new BolexActivatedHistoryLog(1000L, 2000L, 0x1234, 1.5F, 2.25F);

        assertHexEquals(new BolexActivatedHistoryLog(1000L, 2000L, 0x1234, 0, 1.5F, 2.25F).getCargo(), log.getCargo());
        assertEquals(0, log.getCargo()[12]);
    }
}
