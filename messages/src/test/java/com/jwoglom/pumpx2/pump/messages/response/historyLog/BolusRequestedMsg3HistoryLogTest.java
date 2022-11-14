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
}