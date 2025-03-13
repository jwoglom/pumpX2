package com.jwoglom.pumpx2.pump.messages.models;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.TreeSet;

public enum MultiDay {
    MONDAY(1),
    TUESDAY(2),
    WEDNESDAY(4),
    THURSDAY(8),
    FRIDAY(16),
    SATURDAY(32),
    SUNDAY(64),
    ;

    public static final Set<MultiDay> ALL_DAYS = fromBitmask(127);

    private final int id;

    MultiDay(int id) {
        this.id = id;
    }

    public int id() {
        return id;
    }

    public static Set<MultiDay> fromBitmask(int bitmask) {
        Set<MultiDay> set = new TreeSet<>();
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
