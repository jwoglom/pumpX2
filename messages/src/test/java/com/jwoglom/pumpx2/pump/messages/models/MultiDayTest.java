package com.jwoglom.pumpx2.pump.messages.models;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.util.EnumSet;

public class MultiDayTest {
    @Test
    public void testFromBitmask() {
        assertEquals(EnumSet.allOf(MultiDay.class), MultiDay.fromBitmask(127));
        assertEquals(EnumSet.range(MultiDay.MONDAY, MultiDay.FRIDAY), MultiDay.fromBitmask(31));
        assertEquals(EnumSet.of(MultiDay.SATURDAY, MultiDay.SUNDAY), MultiDay.fromBitmask(96));
        assertEquals(EnumSet.of(MultiDay.MONDAY, MultiDay.SATURDAY, MultiDay.SUNDAY), MultiDay.fromBitmask(97));
        assertEquals(EnumSet.of(MultiDay.TUESDAY), MultiDay.fromBitmask(2));
        assertTrue(MultiDay.fromBitmask(0).isEmpty());
    }

    @Test
    public void testAllDays() {
        assertEquals(EnumSet.allOf(MultiDay.class), MultiDay.ALL_DAYS);
    }

    @Test
    public void testToBitmask() {
        assertEquals(96, MultiDay.toBitmask(MultiDay.SATURDAY, MultiDay.SUNDAY));
        assertEquals(127, MultiDay.toBitmask(MultiDay.values()));
        assertEquals(1, MultiDay.toBitmask(MultiDay.MONDAY, MultiDay.MONDAY));
        for (int i = 0; i < 128; i++) {
            assertEquals(i, MultiDay.toBitmask(MultiDay.fromBitmask(i).toArray(new MultiDay[0])));
        }
    }
}
