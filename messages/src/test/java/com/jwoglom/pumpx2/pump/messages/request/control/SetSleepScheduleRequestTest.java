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
import java.util.Set;

public class SetSleepScheduleRequestTest {
    private static final Set<MultiDay> EVERY_DAY = EnumSet.allOf(MultiDay.class);
    private static final Set<MultiDay> WEEKDAYS = EnumSet.range(MultiDay.MONDAY, MultiDay.FRIDAY);
    private static final Set<MultiDay> SAT_SUN = EnumSet.of(MultiDay.SATURDAY, MultiDay.SUNDAY);
    private static final Set<MultiDay> NO_DAYS = EnumSet.noneOf(MultiDay.class);

    private static SleepSchedule schedule(int enabled, Set<MultiDay> days, MinsTime start, MinsTime end) {
        return new SleepSchedule(enabled, MultiDay.toBitmask(days.toArray(new MultiDay[0])), start.encode(), end.encode());
    }

    private static void assertRequest(SetSleepScheduleRequest req, int slot, int enabled, Set<MultiDay> days,
                                      MinsTime start, MinsTime end, Set<ChangeBitmask> changes) {
        assertEquals(slot, req.getSlot());
        assertEquals(enabled, req.getSleepSchedule().getEnabled());
        assertEquals(days, req.getSleepSchedule().activeDays());
        assertEquals(start, req.getSleepSchedule().startTime());
        assertEquals(end, req.getSleepSchedule().endTime());
        assertEquals(changes, req.getChangeBitmask());
        assertEquals(ChangeBitmask.toBitmask(changes), req.getFlag());
    }

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
        // change bitmask 0x03 = BASE | ENABLED: only the on/off switch changed
        assertEquals(3, parsedReq.getFlag());
        assertEquals(EnumSet.of(ChangeBitmask.BASE, ChangeBitmask.ENABLED), parsedReq.getChangeBitmask());
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
        // change bitmask 0x03 = BASE | ENABLED: only the on/off switch changed
        assertEquals(3, parsedReq.getFlag());
        assertEquals(EnumSet.of(ChangeBitmask.BASE, ChangeBitmask.ENABLED), parsedReq.getChangeBitmask());
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

    // The tests below are every SetSleepScheduleRequest the Mobi app (iOS) sent while editing sleep
    // schedules on 2026-10-27, in order. They were captured with PacketLogger and matched to a screen
    // recording of the app. "before" is the pump's schedule for that slot just before the change (from
    // ControlIQSleepScheduleResponse). After each request the pump replied SetSleepScheduleResponse
    // status 0 and returned the "after" schedule when read back.
    //
    // cargo: [slot][enabled][active days][start time u16 LE][end time u16 LE][change bitmask]
    //   slot 0 = Sleep Schedule 1, slot 1 = Sleep Schedule 2 in the app
    //   days: Mon=0x01 Tue=0x02 Wed=0x04 Thu=0x08 Fri=0x10 Sat=0x20 Sun=0x40
    //   times: minutes after midnight
    //   change bitmask: BASE=0x01 (always set) ENABLED=0x02 ACTIVE_DAYS=0x04 START_TIME=0x08 END_TIME=0x10

    @Test
    public void testSetSleepScheduleRequest_mobi01_ss1EndTime() throws DecoderException {
        // TimeSinceResetResponse[currentTime=593972128,pumpTimeSinceReset=430]
        initPumpState(PacketArrayList.IGNORE_INVALID_HMAC, 430L);

        // Sleep Schedule 1: changed End Time from 11:59 PM to 10:00 AM.
        //   before: on, every day, 12:00 AM - 11:59 PM
        //   after:  on, every day, 12:00 AM - 10:00 AM
        // cargo 00 01 7f 0000 5802 11: slot 0, enabled 1, days 0x7f (every day),
        //   start 0 (12:00 AM), end 600 (10:00 AM), change bitmask 0x11 = BASE | END_TIME
        SleepSchedule before = schedule(1, EVERY_DAY, new MinsTime(0, 0), new MinsTime(23, 59));
        SleepSchedule after = schedule(1, EVERY_DAY, new MinsTime(0, 0), new MinsTime(10, 0));
        SetSleepScheduleRequest expected = new SetSleepScheduleRequest(0, before, after);

        SetSleepScheduleRequest parsedReq = (SetSleepScheduleRequest) MessageTester.test(
                // 2026-10-27T20:15:46.379000+00:00
                "02b5ceb52000017f0000580211f2836723fff760",
                -75,
                3,
                CharacteristicUUID.CONTROL_CHARACTERISTICS,
                expected,
                "01b55e84de41869b4ea538d52f5879ee2475a666",
                "00b50e"
        );

        assertEquals("00017f0000580211", Hex.encodeHexString(parsedReq.getCargo()));
        assertRequest(parsedReq, 0, 1, EVERY_DAY, new MinsTime(0, 0), new MinsTime(10, 0),
                EnumSet.of(ChangeBitmask.BASE, ChangeBitmask.END_TIME));
    }

    @Test
    public void testSetSleepScheduleRequest_mobi02_ss1StartTime() throws DecoderException {
        // TimeSinceResetResponse[currentTime=593972128,pumpTimeSinceReset=430]
        initPumpState(PacketArrayList.IGNORE_INVALID_HMAC, 430L);

        // Sleep Schedule 1: changed Start Time from 12:00 AM to 1:00 AM.
        //   before: on, every day, 12:00 AM - 10:00 AM
        //   after:  on, every day, 1:00 AM - 10:00 AM
        // cargo 00 01 7f 3c00 5802 09: slot 0, enabled 1, days 0x7f (every day),
        //   start 60 (1:00 AM), end 600 (10:00 AM), change bitmask 0x09 = BASE | START_TIME
        SleepSchedule before = schedule(1, EVERY_DAY, new MinsTime(0, 0), new MinsTime(10, 0));
        SleepSchedule after = schedule(1, EVERY_DAY, new MinsTime(1, 0), new MinsTime(10, 0));
        SetSleepScheduleRequest expected = new SetSleepScheduleRequest(0, before, after);

        SetSleepScheduleRequest parsedReq = (SetSleepScheduleRequest) MessageTester.test(
                // 2026-10-27T20:15:54.129000+00:00
                "02baceba2000017f3c00580209fa836723e2e9a2",
                -70,
                3,
                CharacteristicUUID.CONTROL_CHARACTERISTICS,
                expected,
                "01ba815b878f67ac33a97deded2039513ab6a64c",
                "00ba10"
        );

        assertEquals("00017f3c00580209", Hex.encodeHexString(parsedReq.getCargo()));
        assertRequest(parsedReq, 0, 1, EVERY_DAY, new MinsTime(1, 0), new MinsTime(10, 0),
                EnumSet.of(ChangeBitmask.BASE, ChangeBitmask.START_TIME));
    }

    @Test
    public void testSetSleepScheduleRequest_mobi03_ss1Weekdays() throws DecoderException {
        // TimeSinceResetResponse[currentTime=593972128,pumpTimeSinceReset=430]
        initPumpState(PacketArrayList.IGNORE_INVALID_HMAC, 430L);

        // Sleep Schedule 1: unchecked Sunday and Saturday.
        //   before: on, every day, 1:00 AM - 10:00 AM
        //   after:  on, Mon-Fri, 1:00 AM - 10:00 AM
        // cargo 00 01 1f 3c00 5802 05: slot 0, enabled 1, days 0x1f (Mon-Fri),
        //   start 60 (1:00 AM), end 600 (10:00 AM), change bitmask 0x05 = BASE | ACTIVE_DAYS
        SleepSchedule before = schedule(1, EVERY_DAY, new MinsTime(1, 0), new MinsTime(10, 0));
        SleepSchedule after = schedule(1, WEEKDAYS, new MinsTime(1, 0), new MinsTime(10, 0));
        SetSleepScheduleRequest expected = new SetSleepScheduleRequest(0, before, after);

        SetSleepScheduleRequest parsedReq = (SetSleepScheduleRequest) MessageTester.test(
                // 2026-10-27T20:16:00.720000+00:00
                "02c2cec22000011f3c0058020500846723ed34d8",
                -62,
                3,
                CharacteristicUUID.CONTROL_CHARACTERISTICS,
                expected,
                "01c24bc95ba9e349eb782b6c92649df7745c2793",
                "00c2c2"
        );

        assertEquals("00011f3c00580205", Hex.encodeHexString(parsedReq.getCargo()));
        assertRequest(parsedReq, 0, 1, WEEKDAYS, new MinsTime(1, 0), new MinsTime(10, 0),
                EnumSet.of(ChangeBitmask.BASE, ChangeBitmask.ACTIVE_DAYS));
    }

    @Test
    public void testSetSleepScheduleRequest_mobi04_ss1DaysStartEnd() throws DecoderException {
        // TimeSinceResetResponse[currentTime=593972128,pumpTimeSinceReset=430]
        initPumpState(PacketArrayList.IGNORE_INVALID_HMAC, 430L);

        // Sleep Schedule 1: changed Start Time from 1:00 AM to 2:00 AM and End Time from 10:00 AM
        // to 9:00 AM, unchecked Monday-Friday and checked Saturday and Sunday.
        //   before: on, Mon-Fri, 1:00 AM - 10:00 AM
        //   after:  on, Sat, Sun, 2:00 AM - 9:00 AM
        // cargo 00 01 60 7800 1c02 1d: slot 0, enabled 1, days 0x60 (Sat, Sun),
        //   start 120 (2:00 AM), end 540 (9:00 AM), change bitmask 0x1d = BASE | ACTIVE_DAYS | START_TIME | END_TIME
        SleepSchedule before = schedule(1, WEEKDAYS, new MinsTime(1, 0), new MinsTime(10, 0));
        SleepSchedule after = schedule(1, SAT_SUN, new MinsTime(2, 0), new MinsTime(9, 0));
        SetSleepScheduleRequest expected = new SetSleepScheduleRequest(0, before, after);

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

        assertEquals("00016078001c021d", Hex.encodeHexString(parsedReq.getCargo()));
        assertRequest(parsedReq, 0, 1, SAT_SUN, new MinsTime(2, 0), new MinsTime(9, 0),
                EnumSet.of(ChangeBitmask.BASE, ChangeBitmask.ACTIVE_DAYS, ChangeBitmask.START_TIME, ChangeBitmask.END_TIME));
    }

    @Test
    public void testSetSleepScheduleRequest_mobi05_ss1Off() throws DecoderException {
        // TimeSinceResetResponse[currentTime=593972128,pumpTimeSinceReset=430]
        initPumpState(PacketArrayList.IGNORE_INVALID_HMAC, 430L);

        // Sleep Schedule 1: switched the schedule off. Days and times are sent unchanged.
        //   before: on, Sat, Sun, 2:00 AM - 9:00 AM
        //   after:  off, Sat, Sun, 2:00 AM - 9:00 AM
        // cargo 00 00 60 7800 1c02 03: slot 0, enabled 0, days 0x60 (Sat, Sun),
        //   start 120 (2:00 AM), end 540 (9:00 AM), change bitmask 0x03 = BASE | ENABLED
        SleepSchedule before = schedule(1, SAT_SUN, new MinsTime(2, 0), new MinsTime(9, 0));
        SleepSchedule after = schedule(0, SAT_SUN, new MinsTime(2, 0), new MinsTime(9, 0));
        SetSleepScheduleRequest expected = new SetSleepScheduleRequest(0, before, after);

        SetSleepScheduleRequest parsedReq = (SetSleepScheduleRequest) MessageTester.test(
                // 2026-10-27T20:16:18.055000+00:00
                "02cecece2000006078001c0203128467232a4518",
                -50,
                3,
                CharacteristicUUID.CONTROL_CHARACTERISTICS,
                expected,
                "01ce9d02175972937e30eb4481835d97a4208762",
                "00cef1"
        );

        assertEquals("00006078001c0203", Hex.encodeHexString(parsedReq.getCargo()));
        assertRequest(parsedReq, 0, 0, SAT_SUN, new MinsTime(2, 0), new MinsTime(9, 0),
                EnumSet.of(ChangeBitmask.BASE, ChangeBitmask.ENABLED));
    }

    @Test
    public void testSetSleepScheduleRequest_mobi06_ss1OnEveryNight() throws DecoderException {
        // TimeSinceResetResponse[currentTime=593972189,pumpTimeSinceReset=491]
        initPumpState(PacketArrayList.IGNORE_INVALID_HMAC, 491L);

        // Sleep Schedule 1: switched the schedule back on (it showed its last days and times),
        // changed Start Time from 2:00 AM to 11:00 PM and End Time from 9:00 AM to 7:00 AM, and checked
        // Monday-Friday.
        //   before: off, Sat, Sun, 2:00 AM - 9:00 AM
        //   after:  on, every day, 11:00 PM - 7:00 AM
        // cargo 00 01 7f 6405 a401 1f: slot 0, enabled 1, days 0x7f (every day),
        //   start 1380 (11:00 PM), end 420 (7:00 AM), change bitmask 0x1f = BASE | ENABLED | ACTIVE_DAYS | START_TIME | END_TIME
        SleepSchedule before = schedule(0, SAT_SUN, new MinsTime(2, 0), new MinsTime(9, 0));
        SleepSchedule after = schedule(1, EVERY_DAY, new MinsTime(23, 0), new MinsTime(7, 0));
        SetSleepScheduleRequest expected = new SetSleepScheduleRequest(0, before, after);

        SetSleepScheduleRequest parsedReq = (SetSleepScheduleRequest) MessageTester.test(
                // 2026-10-27T20:16:31.914000+00:00
                "02d7ced72000017f6405a4011f1f846723f7e789",
                -41,
                3,
                CharacteristicUUID.CONTROL_CHARACTERISTICS,
                expected,
                "01d7503f2bd9ee97f5980c77c0ff851e95d988be",
                "00d799"
        );

        assertEquals("00017f6405a4011f", Hex.encodeHexString(parsedReq.getCargo()));
        assertRequest(parsedReq, 0, 1, EVERY_DAY, new MinsTime(23, 0), new MinsTime(7, 0),
                EnumSet.of(ChangeBitmask.BASE, ChangeBitmask.ENABLED, ChangeBitmask.ACTIVE_DAYS, ChangeBitmask.START_TIME, ChangeBitmask.END_TIME));
    }

    @Test
    public void testSetSleepScheduleRequest_mobi07_ss2On() throws DecoderException {
        // TimeSinceResetResponse[currentTime=593972249,pumpTimeSinceReset=551]
        initPumpState(PacketArrayList.IGNORE_INVALID_HMAC, 551L);

        // Sleep Schedule 2: switched the schedule on for the first time and set Start Time 9:00 AM,
        // End Time 10:00 AM, Sunday and Saturday. Three earlier saves of this schedule (every day
        // 12:00 AM - 11:59 PM, Mon-Fri 12:00 AM - 11:59 PM, Tue-Fri 1:00 AM - 1:01 AM) were rejected by
        // the app for overlapping Sleep Schedule 1 and never sent. The "before" is the pump's default
        // for an unused slot.
        //   before: off, no days, 11:00 PM - 7:00 AM
        //   after:  on, Sat, Sun, 9:00 AM - 10:00 AM
        // cargo 01 01 60 1c02 5802 1f: slot 1, enabled 1, days 0x60 (Sat, Sun),
        //   start 540 (9:00 AM), end 600 (10:00 AM), change bitmask 0x1f = BASE | ENABLED | ACTIVE_DAYS | START_TIME | END_TIME
        SleepSchedule before = schedule(0, NO_DAYS, new MinsTime(23, 0), new MinsTime(7, 0));
        SleepSchedule after = schedule(1, SAT_SUN, new MinsTime(9, 0), new MinsTime(10, 0));
        SetSleepScheduleRequest expected = new SetSleepScheduleRequest(1, before, after);

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

        assertEquals("0101601c0258021f", Hex.encodeHexString(parsedReq.getCargo()));
        assertRequest(parsedReq, 1, 1, SAT_SUN, new MinsTime(9, 0), new MinsTime(10, 0),
                EnumSet.of(ChangeBitmask.BASE, ChangeBitmask.ENABLED, ChangeBitmask.ACTIVE_DAYS, ChangeBitmask.START_TIME, ChangeBitmask.END_TIME));
    }

    @Test
    public void testSetSleepScheduleRequest_mobi08_ss2AddMonday() throws DecoderException {
        // TimeSinceResetResponse[currentTime=593972249,pumpTimeSinceReset=551]
        initPumpState(PacketArrayList.IGNORE_INVALID_HMAC, 551L);

        // Sleep Schedule 2: checked Monday.
        //   before: on, Sat, Sun, 9:00 AM - 10:00 AM
        //   after:  on, Mon, Sat, Sun, 9:00 AM - 10:00 AM
        // cargo 01 01 61 1c02 5802 05: slot 1, enabled 1, days 0x61 (Mon, Sat, Sun),
        //   start 540 (9:00 AM), end 600 (10:00 AM), change bitmask 0x05 = BASE | ACTIVE_DAYS
        SleepSchedule before = schedule(1, SAT_SUN, new MinsTime(9, 0), new MinsTime(10, 0));
        SleepSchedule after = schedule(1, EnumSet.of(MultiDay.MONDAY, MultiDay.SATURDAY, MultiDay.SUNDAY), new MinsTime(9, 0), new MinsTime(10, 0));
        SetSleepScheduleRequest expected = new SetSleepScheduleRequest(1, before, after);

        SetSleepScheduleRequest parsedReq = (SetSleepScheduleRequest) MessageTester.test(
                // 2026-10-27T20:17:43.867000+00:00
                "02e6cee6200101611c02580205678467230fc6c0",
                -26,
                3,
                CharacteristicUUID.CONTROL_CHARACTERISTICS,
                expected,
                "01e68467c2008897e598426ad718c43034640839",
                "00e6dd"
        );

        assertEquals("0101611c02580205", Hex.encodeHexString(parsedReq.getCargo()));
        assertRequest(parsedReq, 1, 1, EnumSet.of(MultiDay.MONDAY, MultiDay.SATURDAY, MultiDay.SUNDAY), new MinsTime(9, 0), new MinsTime(10, 0),
                EnumSet.of(ChangeBitmask.BASE, ChangeBitmask.ACTIVE_DAYS));
    }

    @Test
    public void testSetSleepScheduleRequest_mobi09_ss2RemoveMonday() throws DecoderException {
        // TimeSinceResetResponse[currentTime=593972249,pumpTimeSinceReset=551]
        initPumpState(PacketArrayList.IGNORE_INVALID_HMAC, 551L);

        // Sleep Schedule 2: unchecked Monday.
        //   before: on, Mon, Sat, Sun, 9:00 AM - 10:00 AM
        //   after:  on, Sat, Sun, 9:00 AM - 10:00 AM
        // cargo 01 01 60 1c02 5802 05: slot 1, enabled 1, days 0x60 (Sat, Sun),
        //   start 540 (9:00 AM), end 600 (10:00 AM), change bitmask 0x05 = BASE | ACTIVE_DAYS
        SleepSchedule before = schedule(1, EnumSet.of(MultiDay.MONDAY, MultiDay.SATURDAY, MultiDay.SUNDAY), new MinsTime(9, 0), new MinsTime(10, 0));
        SleepSchedule after = schedule(1, SAT_SUN, new MinsTime(9, 0), new MinsTime(10, 0));
        SetSleepScheduleRequest expected = new SetSleepScheduleRequest(1, before, after);

        SetSleepScheduleRequest parsedReq = (SetSleepScheduleRequest) MessageTester.test(
                // 2026-10-27T20:17:49.007000+00:00
                "02eeceee200101601c025802056c846723cb135f",
                -18,
                3,
                CharacteristicUUID.CONTROL_CHARACTERISTICS,
                expected,
                "01eead956cbeef7e1f4329c216ff1cd502cfe098",
                "00eee1"
        );

        assertEquals("0101601c02580205", Hex.encodeHexString(parsedReq.getCargo()));
        assertRequest(parsedReq, 1, 1, SAT_SUN, new MinsTime(9, 0), new MinsTime(10, 0),
                EnumSet.of(ChangeBitmask.BASE, ChangeBitmask.ACTIVE_DAYS));
    }

    @Test
    public void testSetSleepScheduleRequest_mobi10_ss2Off() throws DecoderException {
        // TimeSinceResetResponse[currentTime=593972249,pumpTimeSinceReset=551]
        initPumpState(PacketArrayList.IGNORE_INVALID_HMAC, 551L);

        // Sleep Schedule 2: switched the schedule off. Days and times are sent unchanged.
        //   before: on, Sat, Sun, 9:00 AM - 10:00 AM
        //   after:  off, Sat, Sun, 9:00 AM - 10:00 AM
        // cargo 01 00 60 1c02 5802 03: slot 1, enabled 0, days 0x60 (Sat, Sun),
        //   start 540 (9:00 AM), end 600 (10:00 AM), change bitmask 0x03 = BASE | ENABLED
        SleepSchedule before = schedule(1, SAT_SUN, new MinsTime(9, 0), new MinsTime(10, 0));
        SleepSchedule after = schedule(0, SAT_SUN, new MinsTime(9, 0), new MinsTime(10, 0));
        SetSleepScheduleRequest expected = new SetSleepScheduleRequest(1, before, after);

        SetSleepScheduleRequest parsedReq = (SetSleepScheduleRequest) MessageTester.test(
                // 2026-10-27T20:17:52.431000+00:00
                "02f2cef2200100601c025802037084672312acd1",
                -14,
                3,
                CharacteristicUUID.CONTROL_CHARACTERISTICS,
                expected,
                "01f202a405d291c66d9c48d071558e3ca57779ad",
                "00f2d2"
        );

        assertEquals("0100601c02580203", Hex.encodeHexString(parsedReq.getCargo()));
        assertRequest(parsedReq, 1, 0, SAT_SUN, new MinsTime(9, 0), new MinsTime(10, 0),
                EnumSet.of(ChangeBitmask.BASE, ChangeBitmask.ENABLED));
    }

    @Test
    public void testSetSleepScheduleRequest_mobi11_ss1OffAgain() throws DecoderException {
        // TimeSinceResetResponse[currentTime=593972249,pumpTimeSinceReset=551]
        initPumpState(PacketArrayList.IGNORE_INVALID_HMAC, 551L);

        // Sleep Schedule 1: switched the schedule off. Days and times are sent unchanged.
        //   before: on, every day, 11:00 PM - 7:00 AM
        //   after:  off, every day, 11:00 PM - 7:00 AM
        // cargo 00 00 7f 6405 a401 03: slot 0, enabled 0, days 0x7f (every day),
        //   start 1380 (11:00 PM), end 420 (7:00 AM), change bitmask 0x03 = BASE | ENABLED
        SleepSchedule before = schedule(1, EVERY_DAY, new MinsTime(23, 0), new MinsTime(7, 0));
        SleepSchedule after = schedule(0, EVERY_DAY, new MinsTime(23, 0), new MinsTime(7, 0));
        SetSleepScheduleRequest expected = new SetSleepScheduleRequest(0, before, after);

        SetSleepScheduleRequest parsedReq = (SetSleepScheduleRequest) MessageTester.test(
                // 2026-10-27T20:17:58.070000+00:00
                "02facefa2000007f6405a4010376846723c27b25",
                -6,
                3,
                CharacteristicUUID.CONTROL_CHARACTERISTICS,
                expected,
                "01fa965219d450fb1dcc66e92d8d285df26bf9ff",
                "00fabd"
        );

        assertEquals("00007f6405a40103", Hex.encodeHexString(parsedReq.getCargo()));
        assertRequest(parsedReq, 0, 0, EVERY_DAY, new MinsTime(23, 0), new MinsTime(7, 0),
                EnumSet.of(ChangeBitmask.BASE, ChangeBitmask.ENABLED));
    }
}
