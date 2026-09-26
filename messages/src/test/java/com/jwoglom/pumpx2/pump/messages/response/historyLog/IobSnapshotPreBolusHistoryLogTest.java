package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;
import static org.junit.Assert.assertEquals;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class IobSnapshotPreBolusHistoryLogTest {
    // Mobi driven by Trio (Control-IQ off, Mudaliar IOB displayed), BLE bolus 2904.
    @Test
    public void testMobiMudaliarDisplayed() throws DecoderException {
        IobSnapshotPreBolusHistoryLog expected = new IobSnapshotPreBolusHistoryLog(
                // long pumpTimeSec, long sequenceNum, int bolusId, int unknownU8At12, int selectedIob, float mudaliarIob, float swan6hrIob, float unknownFloatAt22, int headerHighNibble
                589529231L, 640844L, 2904, 1, 0,
                Float.intBitsToFloat(0x3f3fe725), // ~0.7496
                Float.intBitsToFloat(0x3f206f16), // ~0.6267
                Float.intBitsToFloat(0x3ec8f713), // ~0.3925
                1
        );

        IobSnapshotPreBolusHistoryLog parsedRes = (IobSnapshotPreBolusHistoryLog) HistoryLogMessageTester.testSingle(
                "ce108f8023234cc70900580b010025e73f3f166f203f13f7c83e",
                expected
        );
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
        assertEquals(2904, parsedRes.getBolusId());
        assertEquals(BolusRequestedMsg2HistoryLog.SelectedIOBType.MUDALIAR_IOB, parsedRes.getSelectedIobType());
        assertEquals(0x3f3fe725, Float.floatToIntBits(parsedRes.getMudaliarIob()));
        assertEquals(0x3f206f16, Float.floatToIntBits(parsedRes.getSwan6hrIob()));
        assertEquals(0x3ec8f713, Float.floatToIntBits(parsedRes.getUnknownFloatAt22()));
    }

    // t:slim X2 with Control-IQ (Swan IOB displayed), bolus 10673. A ControlIQIOBResponse read
    // 16 s later reported mudaliarIOB 2500 and swan6hrIOB 2462, and the BolusActivated record for
    // this bolus carried iob 0x401d9425, the Swan value.
    @Test
    public void testX2SwanDisplayed() throws DecoderException {
        IobSnapshotPreBolusHistoryLog expected = new IobSnapshotPreBolusHistoryLog(
                503759752L, 2108107L, 10673, 3, 1,
                Float.intBitsToFloat(0x40200001), // ~2.5
                Float.intBitsToFloat(0x401d9425), // ~2.4622
                Float.intBitsToFloat(0x401c2104)  // ~2.4395
        );

        IobSnapshotPreBolusHistoryLog parsedRes = (IobSnapshotPreBolusHistoryLog) HistoryLogMessageTester.testSingle(
                "ce0088c3061ecb2a2000b12903010100204025941d4004211c40",
                expected
        );
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
        assertEquals(3, parsedRes.getUnknownU8At12());
        assertEquals(BolusRequestedMsg2HistoryLog.SelectedIOBType.SWAN_IOB_MEAL, parsedRes.getSelectedIobType());

        BolusActivatedHistoryLog activated = (BolusActivatedHistoryLog) HistoryLogParser.parse(
                com.jwoglom.pumpx2.shared.Hex.decodeHex("370098c3061ed82a2000b129010025941d40cdcc4c3d00000000"));
        assertEquals(Float.floatToIntBits(activated.getIob()), Float.floatToIntBits(parsedRes.getSwan6hrIob()));
    }
}
