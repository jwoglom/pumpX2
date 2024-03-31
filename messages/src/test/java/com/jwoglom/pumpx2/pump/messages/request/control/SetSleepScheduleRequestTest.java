package com.jwoglom.pumpx2.pump.messages.request.control;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;
import static com.jwoglom.pumpx2.pump.messages.MessageTester.initPumpState;

import static org.junit.Assert.assertEquals;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.messages.PacketArrayList;
import com.jwoglom.pumpx2.pump.messages.bluetooth.CharacteristicUUID;
import com.jwoglom.pumpx2.pump.messages.models.MinsTime;
import com.jwoglom.pumpx2.pump.messages.models.MultiDay;
import com.jwoglom.pumpx2.pump.messages.request.control.SetSleepScheduleRequest;
import com.jwoglom.pumpx2.pump.messages.response.currentStatus.ControlIQSleepScheduleResponse;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class SetSleepScheduleRequestTest {
    @Test
    public void testSetSleepScheduleRequest_set0_rawBytes() throws DecoderException {
        // TimeSinceResetResponse[currentTime=512443266,pumpTimeSinceReset=1905836,cargo={-126,67,-117,30,-84,20,29,0}]
        initPumpState(PacketArrayList.IGNORE_INVALID_HMAC, 1905836L);

        SetSleepScheduleRequest expected = new SetSleepScheduleRequest(
                new byte[]{0,1,127,0,0,-97,5,3}
        );

        // Sleep Schedule 1 on (StartTime:00:00, End Time 23:59, Repeat: Su-Sa, On)	(start time, end time, repeat days, on/off)
        SetSleepScheduleRequest parsedReq = (SetSleepScheduleRequest) MessageTester.test(
                // 2024-03-28T00:21:36.204000+00:00
                "02a2cea22000017f00009f0503ff978b1e828836",
                -94,
                3,
                CharacteristicUUID.CONTROL_CHARACTERISTICS,
                expected,
                "01a222d3b77a25443cc291be989dc841df1a8a75",
                "00a266"
        );

        // for: ControlIQSleepScheduleResponse[schedule0=ControlIQSleepScheduleResponse.SleepSchedule[activeDays=127,enabled=1,endTime=1439,startTime=0],schedule1=ControlIQSleepScheduleResponse.SleepSchedule[activeDays=48,enabled=0,endTime=540,startTime=1320],schedule2=ControlIQSleepScheduleResponse.SleepSchedule[activeDays=0,enabled=0,endTime=420,startTime=1380],schedule3=ControlIQSleepScheduleResponse.SleepSchedule[activeDays=0,enabled=0,endTime=420,startTime=1380],cargo={1,127,0,0,-97,5,0,48,40,5,28,2,0,0,100,5,-92,1,0,0,100,5,-92,1}]

        assertHexEquals(expected.getCargo(), parsedReq.getCargo());
        assertEquals(0, parsedReq.getSlot());
        assertEquals(1, parsedReq.getSleepSchedule().getEnabled());
        assertEquals(MultiDay.ALL_DAYS, parsedReq.getSleepSchedule().activeDays());
        assertEquals(new MinsTime(0), parsedReq.getSleepSchedule().startTime());
        assertEquals(new MinsTime(23, 59), parsedReq.getSleepSchedule().endTime());
        assertEquals(3, parsedReq.getFlag());
    }

    @Test
    public void testSetSleepScheduleRequest_disable0() throws DecoderException {
        // TimeSinceResetResponse[currentTime=512443266,pumpTimeSinceReset=1905836,cargo={-126,67,-117,30,-84,20,29,0}]
        initPumpState(PacketArrayList.IGNORE_INVALID_HMAC, 1905836L);

        SetSleepScheduleRequest expected = new SetSleepScheduleRequest(
                new byte[]{0,0,127,0,0,-97,5,3}
        );

        // Sleep Schedule 1 off
        SetSleepScheduleRequest parsedReq = (SetSleepScheduleRequest) MessageTester.test(
                // 2024-03-28T00:21:54.478000+00:00
                "02abceab2000007f00009f050311988b1ef9651b",
                -85,
                3,
                CharacteristicUUID.CONTROL_CHARACTERISTICS,
                expected,
                "01abc15275490acd94a3416d8cfc038cd7ff461a",
                "00ab0c"
        );

        // for: ControlIQSleepScheduleResponse[schedule0=ControlIQSleepScheduleResponse.SleepSchedule[activeDays=127,enabled=0,endTime=1439,startTime=0],schedule1=ControlIQSleepScheduleResponse.SleepSchedule[activeDays=48,enabled=0,endTime=540,startTime=1320],schedule2=ControlIQSleepScheduleResponse.SleepSchedule[activeDays=0,enabled=0,endTime=420,startTime=1380],schedule3=ControlIQSleepScheduleResponse.SleepSchedule[activeDays=0,enabled=0,endTime=420,startTime=1380],cargo={0,127,0,0,-97,5,0,48,40,5,28,2,0,0,100,5,-92,1,0,0,100,5,-92,1}]

        assertHexEquals(expected.getCargo(), parsedReq.getCargo());
        assertEquals(0, parsedReq.getSlot());
        assertEquals(0, parsedReq.getSleepSchedule().getEnabled());
        assertEquals(MultiDay.ALL_DAYS, parsedReq.getSleepSchedule().activeDays());
        assertEquals(new MinsTime(0), parsedReq.getSleepSchedule().startTime());
        assertEquals(new MinsTime(23, 59), parsedReq.getSleepSchedule().endTime());
        assertEquals(3, parsedReq.getFlag());
    }
}