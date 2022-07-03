package com.jwoglom.pumpx2.pump.messages.models;

import com.jwoglom.pumpx2.pump.messages.response.currentStatus.ControlIQSleepScheduleResponse;

import java.util.HashSet;
import java.util.Set;

public enum MultiDay {
    MONDAY(1),
    TUESDAY(2),
    WEDNESDAY(4),
    THURSDAY(8),
    FRIDAY(16),
    SATURDAY(32),
    SUNDAY(64),
    ;

    private final int id;

    MultiDay(int id) {
        this.id = id;
    }

    public int id() {
        return id;
    }

    public static Set<MultiDay> fromBitmask(int bitmask) {
        Set<MultiDay> set = new HashSet<>();
        for (MultiDay day : values()) {
            if (bitmask % day.id() == 0) {
                set.add(day);
            }
        }

        return set;
    }

    public static int toBitmask(MultiDay... days) {
        int mask = 0;
        for (MultiDay day : days) {
            mask += day.id();
        }

        return mask;
    }
}
