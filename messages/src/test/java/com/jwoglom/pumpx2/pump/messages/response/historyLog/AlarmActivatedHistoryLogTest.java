package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;

import static org.junit.Assert.assertEquals;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.messages.bluetooth.CharacteristicUUID;
import com.jwoglom.pumpx2.pump.messages.response.currentStatus.AlarmStatusResponse;
import com.jwoglom.pumpx2.pump.messages.response.historyLog.AlarmActivatedHistoryLog;

import java.util.Arrays;
import org.apache.commons.codec.DecoderException;
import org.junit.Ignore;
import org.junit.Test;
public class AlarmActivatedHistoryLogTest {
    @Test
    public void testAlarmActivatedHistoryLog1() throws DecoderException {
        AlarmActivatedHistoryLog expected = new AlarmActivatedHistoryLog(
            // long alarmId, long faultLocatorData, long param1, float param2
                18, 8311, 6544514, 0.0f
        );

        AlarmActivatedHistoryLog parsedRes = (AlarmActivatedHistoryLog) HistoryLogMessageTester.testSingleIgnoringBaseFields(
                "0500b3e8951a86c90200120000007720000082dc630000000000",
                expected
        );
        assertEquals(AlarmStatusResponse.AlarmResponseType.RESUME_PUMP_ALARM, parsedRes.getAlarmResponseType());
    }

    @Test
    public void testAlarmActivatedHistoryLog2() throws DecoderException {
        AlarmActivatedHistoryLog expected = new AlarmActivatedHistoryLog(
                // long alarmId, long faultLocatorData, long param1, float param2
                23, 8311, 18, 0.0f
        );

        AlarmActivatedHistoryLog parsedRes = (AlarmActivatedHistoryLog) HistoryLogMessageTester.testSingleIgnoringBaseFields(
                "05004044971af9d2020017000000772000001200000000000000",
                expected
        );
        assertEquals(AlarmStatusResponse.AlarmResponseType.RESUME_PUMP_ALARM2, parsedRes.getAlarmResponseType());
    }

    @Test
    public void testAlarmActivatedHistoryLog3() throws DecoderException {
        AlarmActivatedHistoryLog expected = new AlarmActivatedHistoryLog(
                // long alarmId, long faultLocatorData, long param1, float param2
                23, 8311, 18, 0.0f
        );

        AlarmActivatedHistoryLog parsedRes = (AlarmActivatedHistoryLog) HistoryLogMessageTester.testSingleIgnoringBaseFields(
                "0500b3e8951a87c9020017000000772000001200000000000000",
                expected
        );
        assertEquals(AlarmStatusResponse.AlarmResponseType.RESUME_PUMP_ALARM2, parsedRes.getAlarmResponseType());
    }

    @Test
    public void testAlarmActivatedHistoryLog4() throws DecoderException {
        AlarmActivatedHistoryLog expected = new AlarmActivatedHistoryLog(
                // long alarmId, long faultLocatorData, long param1, float param2
                18, 8311, 6633487, 0.0f
        );

        AlarmActivatedHistoryLog parsedRes = (AlarmActivatedHistoryLog) HistoryLogMessageTester.testSingleIgnoringBaseFields(
                "05004044971af8d2020012000000772000000f38650000000000",
                expected
        );
        assertEquals(AlarmStatusResponse.AlarmResponseType.RESUME_PUMP_ALARM, parsedRes.getAlarmResponseType());
    }
}