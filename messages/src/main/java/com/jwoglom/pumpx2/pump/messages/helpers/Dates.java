package com.jwoglom.pumpx2.pump.messages.helpers;

import static java.time.ZoneOffset.UTC;

import java.time.Instant;
import java.time.temporal.TemporalAdjuster;

public class Dates {
    public static final long JANUARY_1_2008_UNIX_EPOCH = 1199145600;

    public static long fromJan12008ToUnixEpochSeconds(long seconds) {
        return seconds + JANUARY_1_2008_UNIX_EPOCH;
    }

    public static Instant fromJan12008EpochSecondsToDate(long seconds) {
        return Instant.ofEpochSecond(fromJan12008ToUnixEpochSeconds(seconds));
    }

    public static long fromInstantToJan12008EpochSeconds(Instant instant) {
        return instant.getEpochSecond() - JANUARY_1_2008_UNIX_EPOCH;
    }

    private static final long SECONDS_IN_DAY = 60 * 60 * 24;

    public static Instant fromJan12008EpochDaysToDate(long days) {
        return Instant.ofEpochSecond(fromJan12008ToUnixEpochSeconds(days * SECONDS_IN_DAY));
    }
}
