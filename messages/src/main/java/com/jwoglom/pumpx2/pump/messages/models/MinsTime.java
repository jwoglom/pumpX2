package com.jwoglom.pumpx2.pump.messages.models;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MinsTime minsTime = (MinsTime) o;
        return encode() == minsTime.encode();
    }

    @Override
    public int hashCode() {
        return Objects.hash(hour, min);
    }

    @SuppressWarnings("DefaultLocale")
    public String toString() {
        return String.format("%02d:%02d", hour(), min());
    }
}
