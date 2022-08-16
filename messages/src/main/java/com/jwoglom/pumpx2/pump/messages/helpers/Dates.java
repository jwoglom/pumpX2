package com.jwoglom.pumpx2.pump.messages.helpers;

import java.time.Instant;

public class Dates {
    public static long fromJan12008ToUnixEpochSeconds(long seconds) {
        return seconds + 1199145600;
    }

    public static Instant fromJan12008EpochSecondsToDate(long seconds) {
        return Instant.ofEpochSecond(fromJan12008ToUnixEpochSeconds(seconds));
    }

    private static final long SECONDS_IN_DAY = 60 * 60 * 24;

    public static Instant fromJan12008EpochDaysToDate(long days) {
        return Instant.ofEpochSecond(fromJan12008ToUnixEpochSeconds(days * SECONDS_IN_DAY));
    }
}
