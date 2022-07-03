package com.jwoglom.pumpx2.pump.messages.response.currentStatus;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.bluetooth.CharacteristicUUID;
import com.jwoglom.pumpx2.pump.messages.models.MinsTime;
import com.jwoglom.pumpx2.pump.messages.models.MultiDay;
import com.jwoglom.pumpx2.pump.messages.response.currentStatus.RemindersResponse.Reminder;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class RemindersResponseTest {
    @Test
    public void testRemindersResponseAllOff() throws DecoderException {
        // All reminders off
        // Site Reminder turned off, with Thursday 9PM
        RemindersResponse expected = new RemindersResponse(
            // Low BG
            new Reminder(15, 0, 0, 0, 0, 0),
            // High BG
            new Reminder(120, 0, 0, 0, 0, 0),
            // Site change
            new Reminder(1260, 0, 0, 0, 0, 3),
            // Missed bolus
            new Reminder(0, 0, 0, 0, 0, 0),
            new Reminder(0, 0, 0, 0, 0, 0),
            new Reminder(0, 0, 0, 0, 0, 0),
            new Reminder(0, 0, 0, 0, 0, 0),
            // After bolus
            new Reminder(90, 0, 0, 0, 0, 0),
            // Additional bolus
            new Reminder(0, 0, 0, 0, 1, 0),
            70,
            200,
            3,
            4
        );

        RemindersResponse parsedRes = (RemindersResponse) MessageTester.test(
                "00035903690f000000000000000000007800000000000000000000ec0400000000000000000300000000000000000000000000000000000000000000000000000000000000000000000000000000000000005a0000000000000000000000000000000000000001004600c8000304e7f9",
                3,
                7,
                CharacteristicUUID.CURRENT_STATUS_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }

    @Test
    public void testRemindersResponse1() throws DecoderException {
        // Low BG Reminder enabled, 80mg/dl after 20 mins
        RemindersResponse expected = new RemindersResponse(
                // Low BG
                new Reminder(20, 0, 0, 0, 1, 3),
                // High BG
                new Reminder(120, 0, 0, 0, 0, 0),
                // Site change
                new Reminder(1260, 0, 0, 0, 0, 3),
                // Missed bolus
                new Reminder(0, 0, 0, 0, 0, 0),
                new Reminder(0, 0, 0, 0, 0, 0),
                new Reminder(0, 0, 0, 0, 0, 0),
                new Reminder(0, 0, 0, 0, 0, 0),
                // After bolus
                new Reminder(90, 0, 0, 0, 0, 0),
                // Additional bolus
                new Reminder(0, 0, 0, 0, 1, 0),
                80,
                200,
                3,
                5
        );

        RemindersResponse parsedRes = (RemindersResponse) MessageTester.test(
                "000459046914000000000000000001037800000000000000000000ec0400000000000000000300000000000000000000000000000000000000000000000000000000000000000000000000000000000000005a0000000000000000000000000000000000000001005000c80003052efa",
                4,
                7,
                CharacteristicUUID.CURRENT_STATUS_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }

    @Test
    public void testRemindersResponse2() throws DecoderException {
        // Low BG Reminder enabled, 80mg/dl after 20 mins
        RemindersResponse expected = new RemindersResponse(
                // Low BG
                new Reminder(20, 0, 0, 0, 1, 3),
                // High BG
                new Reminder(new MinsTime(3, 0).encode(), 0, 0, 0, 1, 3),
                // Site change
                new Reminder(1260, 0, 0, 0, 0, 3),
                // Missed bolus
                new Reminder(0, 0, 0, 0, 0, 0),
                new Reminder(0, 0, 0, 0, 0, 0),
                new Reminder(0, 0, 0, 0, 0, 0),
                new Reminder(0, 0, 0, 0, 0, 0),
                // After bolus
                new Reminder(90, 0, 0, 0, 0, 0),
                // Additional bolus
                new Reminder(0, 0, 0, 0, 1, 0),
                80,
                300,
                3,
                7
        );

        RemindersResponse parsedRes = (RemindersResponse) MessageTester.test(
                "00065906691400000000000000000103b400000000000000000103ec0400000000000000000300000000000000000000000000000000000000000000000000000000000000000000000000000000000000005a00000000000000000000000000000000000000010050002c0103077f31",
                6,
                7,
                CharacteristicUUID.CURRENT_STATUS_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }

    @Test
    public void testRemindersResponse3() throws DecoderException {
        // Missed bolus 0, Monday 5pm-7pm
        // Missed bolus 1, M T W Th F, 2pm-3pm
        RemindersResponse expected = new RemindersResponse(
                // Low BG
                new Reminder(20, 0, 0, 0, 1, 3),
                // High BG
                new Reminder(new MinsTime(3, 0).encode(), 0, 0, 0, 1, 3),
                // Site change
                new Reminder(1260, 0, 0, 0, 0, 3),
                // Missed bolus
                new Reminder(0,
                        new MinsTime(12+5, 0).encode(),
                        new MinsTime(12+7, 0).encode(),
                        MultiDay.toBitmask(MultiDay.MONDAY),
                        1,
                        30),
                new Reminder(0,
                        new MinsTime(12+2, 0).encode(),
                        new MinsTime(12+3, 0).encode(),
                        MultiDay.toBitmask(MultiDay.MONDAY, MultiDay.TUESDAY, MultiDay.WEDNESDAY, MultiDay.THURSDAY, MultiDay.FRIDAY),
                        1,
                        30),
                new Reminder(0, 0, 0, 0, 0, 0),
                new Reminder(0, 0, 0, 0, 0, 0),
                // After bolus
                new Reminder(90, 0, 0, 0, 0, 0),
                // Additional bolus
                new Reminder(0, 0, 0, 0, 1, 0),
                80,
                300,
                3,
                7
        );

        RemindersResponse parsedRes = (RemindersResponse) MessageTester.test(
                "00075907691400000000000000000103b400000000000000000103ec0400000000000000000300000000fc03740401011e00000000480384031f011e000000000000000000000000000000000000000000005a00000000000000000000000000000000000000010050002c010307c5e8",
                7,
                7,
                CharacteristicUUID.CURRENT_STATUS_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }

    @Test
    public void testRemindersResponse4() throws DecoderException {
        // Missed bolus 0, Monday 5pm-7pm
        // Missed bolus 1, disabled
        RemindersResponse expected = new RemindersResponse(
                // Low BG
                new Reminder(20, 0, 0, 0, 1, 3),
                // High BG
                new Reminder(new MinsTime(3, 0).encode(), 0, 0, 0, 1, 3),
                // Site change
                new Reminder(1260, 0, 0, 0, 0, 3),
                // Missed bolus
                new Reminder(0,
                        new MinsTime(12+5, 0).encode(),
                        new MinsTime(12+7, 0).encode(),
                        MultiDay.toBitmask(MultiDay.MONDAY),
                        1,
                        30),
                new Reminder(0,
                        new MinsTime(12+2, 0).encode(),
                        new MinsTime(12+3, 0).encode(),
                        MultiDay.toBitmask(MultiDay.MONDAY, MultiDay.TUESDAY, MultiDay.WEDNESDAY, MultiDay.THURSDAY, MultiDay.FRIDAY),
                        0,
                        30),
                new Reminder(0, 0, 0, 0, 0, 0),
                new Reminder(0, 0, 0, 0, 0, 0),
                // After bolus
                new Reminder(90, 0, 0, 0, 0, 0),
                // Additional bolus
                new Reminder(0, 0, 0, 0, 1, 0),
                80,
                300,
                3,
                7
        );

        RemindersResponse parsedRes = (RemindersResponse) MessageTester.test(
                "00085908691400000000000000000103b400000000000000000103ec0400000000000000000300000000fc03740401011e00000000480384031f001e000000000000000000000000000000000000000000005a00000000000000000000000000000000000000010050002c010307e289",
                8,
                7,
                CharacteristicUUID.CURRENT_STATUS_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }

}