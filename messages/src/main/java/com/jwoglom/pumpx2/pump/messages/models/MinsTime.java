package com.jwoglom.pumpx2.pump.messages.models;

/**
 * Used by ControlIQSleepScheduleResponse and RemindersResponse.Reminder
 */
public class MinsTime {
    private final int hour;
    private final int min;

    public MinsTime(int totalMins) {
        this.hour = totalMins / 60;
        this.min = totalMins % 60;
    }

    public MinsTime(int hour, int min) {
        this.hour = hour;
        this.min = min;
    }

    public int hour() {
        return hour;
    }

    public int min() {
        return min;
    }

    public int encode() {
        return hour*60 + min;
    }
}
