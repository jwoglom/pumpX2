package com.jwoglom.pumpx2.pump.messages.response.currentStatus;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.bluetooth.CharacteristicUUID;
import com.jwoglom.pumpx2.pump.messages.models.MinsTime;
import com.jwoglom.pumpx2.pump.messages.response.currentStatus.ControlIQSleepScheduleResponse.SleepSchedule;
import com.jwoglom.pumpx2.pump.messages.models.MultiDay;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class ControlIQSleepScheduleResponseTest {
    @Test
    public void testControlIQSleepScheduleResponse1() throws DecoderException {
        ControlIQSleepScheduleResponse expected = new ControlIQSleepScheduleResponse(
            // Every day from 1:30am - 10:00am
            new SleepSchedule(1, 127, 90, 600),
            // Disabled, defaults to 11PM - 7AM
            new SleepSchedule(0, 0, 1380, 420),
            // Not visible in UI
            new SleepSchedule(0, 0, 1380, 420),
            // Not visible in UI
            new SleepSchedule(0, 0, 1380, 420)
        );

        ControlIQSleepScheduleResponse parsedRes = (ControlIQSleepScheduleResponse) MessageTester.test(
                "00036b0318017f5a00580200006405a40100006405a40100006405a401a330",
                3,
                2,
                CharacteristicUUID.CURRENT_STATUS_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }
    @Test
    public void testControlIQSleepScheduleResponse2() throws DecoderException {
        ControlIQSleepScheduleResponse expected = new ControlIQSleepScheduleResponse(
                // M Tu W Th F from 10:30pm - 10am
                new SleepSchedule(1,
                        MultiDay.toBitmask(MultiDay.MONDAY, MultiDay.TUESDAY, MultiDay.WEDNESDAY, MultiDay.THURSDAY, MultiDay.FRIDAY),
                        new MinsTime(12+10, 30).encode(),
                        new MinsTime(10, 0).encode()),
                // Sat from 11PM - 7am
                new SleepSchedule(1,
                        MultiDay.toBitmask(MultiDay.SATURDAY),
                        new MinsTime(12+11, 0).encode(),
                        new MinsTime(7, 0).encode()),
                // Not visible in UI
                new SleepSchedule(0, 0,
                        new MinsTime(12+11, 0).encode(),
                        new MinsTime(7, 0).encode()),
                // Not visible in UI
                new SleepSchedule(0, 0,
                        new MinsTime(12+11, 0).encode(),
                        new MinsTime(7, 0).encode())
        );

        ControlIQSleepScheduleResponse parsedRes = (ControlIQSleepScheduleResponse) MessageTester.test(
                "00046b0418011f4605580201206405a40100006405a40100006405a40109de",
                4,
                2,
                CharacteristicUUID.CURRENT_STATUS_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }
}