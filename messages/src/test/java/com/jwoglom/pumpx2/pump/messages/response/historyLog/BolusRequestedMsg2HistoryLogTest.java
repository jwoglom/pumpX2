package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.messages.bluetooth.CharacteristicUUID;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class BolusRequestedMsg2HistoryLogTest {
    @Test
    public void testBolusRequestedMsg2HistoryLog1() throws DecoderException {
        BolusRequestedMsg2HistoryLog expected = new BolusRequestedMsg2HistoryLog(
            // long pumpTimeSec, long sequenceNum, int bolusId, int options, int standardPercent, int duration, int spare1, int isf, int targetBG, boolean userOverride, boolean declinedCorrection, int selectedIOB, int spare2
                445961478L, 180876L, 1028, 0,100, 0, 0, 30, 110, true, false, 1, 0

        );

        BolusRequestedMsg2HistoryLog parsedRes = (BolusRequestedMsg2HistoryLog) HistoryLogMessageTester.testSingle(
                "410006d5941a8cc2020004040064000000001e006e0001000100",
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }

    @Test
    public void testBolusRequestedMsg2HistoryLog2() throws DecoderException {
        BolusRequestedMsg2HistoryLog expected = new BolusRequestedMsg2HistoryLog(
                // long pumpTimeSec, long sequenceNum, int bolusId, int options, int standardPercent, int duration, int spare1, int isf, int targetBG, boolean userOverride, boolean declinedCorrection, int selectedIOB, int spare2
                445948266L, 180529L, 1026, 0,100, 0, 0, 30, 110, false, false, 1, 0

        );

        BolusRequestedMsg2HistoryLog parsedRes = (BolusRequestedMsg2HistoryLog) HistoryLogMessageTester.testSingle(
                "41006aa1941a31c1020002040064000000001e006e0000000100",
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }



    @Test
    public void testBolusRequestedMsg2HistoryLog_declinedCorrection() throws DecoderException {
        // 4100c9f4981a71e002002e040064000000001e006e0000010100	BolusRequestedMsg2HistoryLog[bolusId=1070,declinedCorrection=true,duration=0,isf=30,options=0,selectedIOB=1,spare1=0,spare2=0,standardPercent=100,targetBG=110,userOverride=false,cargo={65,0,-55,-12,-104,26,113,-32,2,0,46,4,0,100,0,0,0,0,30,0,110,0,0,1,1,0},pumpTimeSec=446231753,sequenceNum=188529]
        BolusRequestedMsg2HistoryLog expected = new BolusRequestedMsg2HistoryLog(
                // long pumpTimeSec, long sequenceNum, int bolusId, int options, int standardPercent, int duration, int spare1, int isf, int targetBG, boolean userOverride, boolean declinedCorrection, int selectedIOB, int spare2
                1070, 0,100, 0, 0, 30, 110, false, true, 1, 0

        );

        BolusRequestedMsg2HistoryLog parsedRes = (BolusRequestedMsg2HistoryLog) HistoryLogMessageTester.testSingleIgnoringBaseFields(
                "4100c9f4981a71e002002e040064000000001e006e0000010100",
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }

    // Observed on a Tandem Mobi driven by Trio (Control-IQ off, no CGM paired), Sept 2026 BLE capture.
    // Bolus initiated over BLE via InitiateBolusRequest (bolusSource = 8 in the paired
    // BolusDeliveryHistoryLog). options = 4 (BLE_STANDARD) was seen in 156/156 such boluses, with
    // standardPercent 100, duration 0, userOverride true and isf/targetBG 0 (no calculator inputs
    // are sent over BLE). Header high nibble is 1 on this pump. See jwoglom/pumpX2#51.
    @Test
    public void testBolusRequestedMsg2HistoryLog_bleStandard() throws DecoderException {
        BolusRequestedMsg2HistoryLog expected = new BolusRequestedMsg2HistoryLog(
                // long pumpTimeSec, long sequenceNum, int bolusId, int options, int standardPercent, int duration, int spare1, int isf, int targetBG, boolean userOverride, boolean declinedCorrection, int selectedIOB, int spare2, int headerHighNibble
                589526830L, 640747L, 2903, 4, 100, 0, 0, 0, 0, true, false, 0, 0, 1
        );

        BolusRequestedMsg2HistoryLog parsedRes = (BolusRequestedMsg2HistoryLog) HistoryLogMessageTester.testSingle(
                "41102e772323ebc60900570b0464000000000000000001000000",
                expected
        );

        assertEquals(1, parsedRes.getHeaderHighNibble());
        assertEquals(2903, parsedRes.getBolusId());
        assertEquals(4, parsedRes.getOptions());
        assertEquals(BolusRequestedMsg2HistoryLog.BolusOption.BLE_STANDARD, parsedRes.getBolusOption());
        assertEquals(100, parsedRes.getStandardPercent());
        assertEquals(0, parsedRes.getDuration());
        assertEquals(0, parsedRes.getIsf());
        assertEquals(0, parsedRes.getTargetBG());
        assertTrue(parsedRes.getUserOverride());
        assertFalse(parsedRes.getDeclinedCorrection());
        assertEquals(0, parsedRes.getSelectedIOB());
        assertEquals(BolusRequestedMsg2HistoryLog.SelectedIOBType.MUDALIAR_IOB, parsedRes.getSelectedIOBType());
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }
}
