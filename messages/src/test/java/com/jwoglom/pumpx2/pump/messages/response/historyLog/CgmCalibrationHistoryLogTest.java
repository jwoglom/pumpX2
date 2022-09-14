package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.messages.bluetooth.CharacteristicUUID;
import com.jwoglom.pumpx2.pump.messages.response.historyLog.CgmCalibrationHistoryLog;

import com.google.common.collect.ImmutableList;
import org.apache.commons.codec.DecoderException;
import org.junit.Ignore;
import org.junit.Test;
@Ignore("needs historyLog sample")
public class CgmCalibrationHistoryLogTest {
    @Test
    public void testCgmCalibrationHistoryLog() throws DecoderException {
        CgmCalibrationHistoryLog expected = new CgmCalibrationHistoryLog(
            // long currentTime, long timestamp, long calTimestamp, int value, int currentDisplayValue
        );

        CgmCalibrationHistoryLog parsedRes = (CgmCalibrationHistoryLog) HistoryLogMessageTester.testSingle(
                "xxxx",
                expected
        );
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }
}