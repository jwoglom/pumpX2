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
import com.jwoglom.pumpx2.pump.messages.request.control.SetSleepScheduleRequest.ChangeBitmask;
import com.jwoglom.pumpx2.pump.messages.response.currentStatus.ControlIQSleepScheduleResponse;
import com.jwoglom.pumpx2.pump.messages.response.currentStatus.ControlIQSleepScheduleResponse.SleepSchedule;
import com.jwoglom.pumpx2.shared.Hex;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

import java.util.EnumSet;

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

    @Test
    public void testSetSleepScheduleRequest_set0_daysStartEnd() throws DecoderException {
        // TimeSinceResetResponse[currentTime=593972128,pumpTimeSinceReset=430]
        initPumpState(PacketArrayList.IGNORE_INVALID_HMAC, 430L);

        // Sleep Schedule 1 from every weekday 1:00 AM - 10:00 AM to Su, Sa 2:00 AM - 9:00 AM
        SetSleepScheduleRequest expected = new SetSleepScheduleRequest(
                0,
                new SleepSchedule(1, MultiDay.toBitmask(MultiDay.SATURDAY, MultiDay.SUNDAY), 120, 540),
                EnumSet.of(ChangeBitmask.ACTIVE_DAYS, ChangeBitmask.START_TIME, ChangeBitmask.END_TIME)
        );

        SetSleepScheduleRequest parsedReq = (SetSleepScheduleRequest) MessageTester.test(
                // 2026-10-27T20:16:13.937000+00:00
                "02c7cec72000016078001c021d0d846723c98b05",
                -57,
                3,
                CharacteristicUUID.CONTROL_CHARACTERISTICS,
                expected,
                "01c757fadab087b57780e086f7d1e3b0b8d877b8",
                "00c754"
        );

        assertHexEquals(expected.getCargo(), parsedReq.getCargo());
        assertEquals(0, parsedReq.getSlot());
        assertEquals(1, parsedReq.getSleepSchedule().getEnabled());
        assertEquals(EnumSet.of(MultiDay.SATURDAY, MultiDay.SUNDAY), parsedReq.getSleepSchedule().activeDays());
        assertEquals(new MinsTime(2, 0), parsedReq.getSleepSchedule().startTime());
        assertEquals(new MinsTime(9, 0), parsedReq.getSleepSchedule().endTime());
        assertEquals(29, parsedReq.getFlag());
        assertEquals(EnumSet.of(ChangeBitmask.BASE, ChangeBitmask.ACTIVE_DAYS, ChangeBitmask.START_TIME, ChangeBitmask.END_TIME),
                parsedReq.getChangeBitmask());
    }

    @Test
    public void testSetSleepScheduleRequest_enable1() throws DecoderException {
        // TimeSinceResetResponse[currentTime=593972249,pumpTimeSinceReset=551]
        initPumpState(PacketArrayList.IGNORE_INVALID_HMAC, 551L);

        // Sleep Schedule 2 from off (pump default: no days, 11:00 PM - 7:00 AM) to on, Su, Sa 9:00 AM - 10:00 AM
        SetSleepScheduleRequest expected = new SetSleepScheduleRequest(
                1,
                new SleepSchedule(0, 0, 1380, 420),
                new SleepSchedule(1, MultiDay.toBitmask(MultiDay.SATURDAY, MultiDay.SUNDAY), 540, 600)
        );

        SetSleepScheduleRequest parsedReq = (SetSleepScheduleRequest) MessageTester.test(
                // 2026-10-27T20:17:39.046000+00:00
                "02e2cee2200101601c0258021f63846723cfb8f3",
                -30,
                3,
                CharacteristicUUID.CONTROL_CHARACTERISTICS,
                expected,
                "01e2cb0689ce18bd82e003125636d15fd1be3ae1",
                "00e25e"
        );

        assertHexEquals(expected.getCargo(), parsedReq.getCargo());
        assertEquals(1, parsedReq.getSlot());
        assertEquals(1, parsedReq.getSleepSchedule().getEnabled());
        assertEquals(EnumSet.of(MultiDay.SATURDAY, MultiDay.SUNDAY), parsedReq.getSleepSchedule().activeDays());
        assertEquals(new MinsTime(9, 0), parsedReq.getSleepSchedule().startTime());
        assertEquals(new MinsTime(10, 0), parsedReq.getSleepSchedule().endTime());
        assertEquals(31, parsedReq.getFlag());
        assertEquals(EnumSet.allOf(ChangeBitmask.class), parsedReq.getChangeBitmask());
    }

    @Test
    public void testSetSleepScheduleRequest_constructorSetsFields() {
        SetSleepScheduleRequest req = new SetSleepScheduleRequest(1, new SleepSchedule(1, 31, 60, 600), 5);

        assertEquals("01011f3c00580205", Hex.encodeHexString(req.getCargo()));
        assertEquals(1, req.getSlot());
        assertEquals(5, req.getFlag());
        assertEquals(EnumSet.of(ChangeBitmask.BASE, ChangeBitmask.ACTIVE_DAYS), req.getChangeBitmask());
        assertEquals(EnumSet.range(MultiDay.MONDAY, MultiDay.FRIDAY), req.getSleepSchedule().activeDays());
        assertEquals(new MinsTime(1, 0), req.getSleepSchedule().startTime());
        assertEquals(new MinsTime(10, 0), req.getSleepSchedule().endTime());
    }

    @Test
    public void testSetSleepScheduleRequest_previousUpdatedMatchesMobiApp() {
        // Every SetSleepScheduleRequest cargo the Mobi app sent while editing sleep schedules,
        // with the pump's schedule for that slot before each change.
        Object[][] cases = {
            // slot, previous, updated, cargo sent by the app
            {0, new SleepSchedule(1, 127, 0, 1439), new SleepSchedule(1, 127, 0, 600), "00017f0000580211"},
            {0, new SleepSchedule(1, 127, 0, 600), new SleepSchedule(1, 127, 60, 600), "00017f3c00580209"},
            {0, new SleepSchedule(1, 127, 60, 600), new SleepSchedule(1, 31, 60, 600), "00011f3c00580205"},
            {0, new SleepSchedule(1, 31, 60, 600), new SleepSchedule(1, 96, 120, 540), "00016078001c021d"},
            {0, new SleepSchedule(1, 96, 120, 540), new SleepSchedule(0, 96, 120, 540), "00006078001c0203"},
            {0, new SleepSchedule(0, 96, 120, 540), new SleepSchedule(1, 127, 1380, 420), "00017f6405a4011f"},
            {1, new SleepSchedule(0, 0, 1380, 420), new SleepSchedule(1, 96, 540, 600), "0101601c0258021f"},
            {1, new SleepSchedule(1, 96, 540, 600), new SleepSchedule(1, 97, 540, 600), "0101611c02580205"},
            {1, new SleepSchedule(1, 97, 540, 600), new SleepSchedule(1, 96, 540, 600), "0101601c02580205"},
            {1, new SleepSchedule(1, 96, 540, 600), new SleepSchedule(0, 96, 540, 600), "0100601c02580203"},
            {0, new SleepSchedule(1, 127, 1380, 420), new SleepSchedule(0, 127, 1380, 420), "00007f6405a40103"},
        };

        for (Object[] c : cases) {
            SetSleepScheduleRequest req = new SetSleepScheduleRequest((int) c[0], (SleepSchedule) c[1], (SleepSchedule) c[2]);
            assertEquals((String) c[3], Hex.encodeHexString(req.getCargo()));
        }
    }
}
