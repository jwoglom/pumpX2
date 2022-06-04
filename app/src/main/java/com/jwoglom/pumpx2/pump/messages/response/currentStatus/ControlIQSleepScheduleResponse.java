package com.jwoglom.pumpx2.pump.messages.response.currentStatus;

import com.google.common.base.Preconditions;
import com.jwoglom.pumpx2.pump.messages.Bytes;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.request.currentStatus.ControlIQSleepScheduleRequest;
import com.jwoglom.pumpx2.shared.JavaHelpers;

import java.math.BigInteger;
import java.util.HashSet;
import java.util.Set;

@MessageProps(
    opCode=107,
    size=24,
    type=MessageType.RESPONSE,
    request=ControlIQSleepScheduleRequest.class
)
public class ControlIQSleepScheduleResponse extends Message {
    
    private SleepSchedule schedule0;
    private SleepSchedule schedule1;
    private SleepSchedule schedule2;
    private SleepSchedule schedule3;
    
    public ControlIQSleepScheduleResponse() {}
    
    public ControlIQSleepScheduleResponse(SleepSchedule schedule0, SleepSchedule schedule1, SleepSchedule schedule2, SleepSchedule schedule3) {
        this.cargo = buildCargo(schedule0, schedule1, schedule2, schedule3);
        this.schedule0 = schedule0;
        this.schedule1 = schedule1;
        this.schedule2 = schedule2;
        this.schedule3 = schedule3;
        
    }

    public void parse(byte[] raw) {
        Preconditions.checkArgument(raw.length == props().size());
        this.cargo = raw;
        this.schedule0 = new SleepSchedule(raw, 0);
        this.schedule1 = new SleepSchedule(raw, 6);
        this.schedule2 = new SleepSchedule(raw, 12);
        this.schedule3 = new SleepSchedule(raw, 18);
    }

    
    public static byte[] buildCargo(SleepSchedule schedule0, SleepSchedule schedule1, SleepSchedule schedule2, SleepSchedule schedule3) {
        return Bytes.combine(
            schedule0.build(),
            schedule1.build(),
            schedule2.build(),
            schedule3.build());
    }
    
    public SleepSchedule getSchedule0() {
        return schedule0;
    }
    public SleepSchedule getSchedule1() {
        return schedule1;
    }
    public SleepSchedule getSchedule2() {
        return schedule2;
    }
    public SleepSchedule getSchedule3() {
        return schedule3;
    }

    public static class SleepSchedule {
        private final int enabled;
        private final int activeDays;
        private final int startTime;
        private final int endTime;

        public SleepSchedule(byte[] cargo, int i) {
            this.enabled = cargo[i];
            this.activeDays = cargo[i+1];
            this.startTime = Bytes.readShort(cargo, i+2);
            this.endTime = Bytes.readShort(cargo, i+4);
        }

        public SleepSchedule(int enabled, int activeDays, int startTime, int endTime) {
            this.enabled = enabled;
            this.activeDays = activeDays;
            this.startTime = startTime;
            this.endTime = endTime;
        }

        public int getEnabled() {
            return enabled;
        }

        public enum Day {
            MONDAY(1),
            TUESDAY(2),
            WEDNESDAY(4),
            THURSDAY(8),
            FRIDAY(16),
            SATURDAY(32),
            SUNDAY(64),
            ;

            private final int id;
            Day(int id) {
                this.id = id;
            }

            public int id() {
                return id;
            }

            public static Set<Day> fromBitmask(int bitmask) {
                Set<Day> set = new HashSet<>();
                for (Day day : values()) {
                    if (bitmask % day.id() == 0) {
                        set.add(day);
                    }
                }

                return set;
            }

            public static int toBitmask(Day ...days) {
                int mask = 0;
                for (Day day : days) {
                    mask += day.id();
                }

                return mask;
            }
        }

        public Set<Day> activeDays() {
            return Day.fromBitmask(activeDays);
        }

        public static class Time {
            private final int hour;
            private final int min;

            public Time(int totalMins) {
                this.hour = totalMins / 60;
                this.min = totalMins % 60;
            }

            public Time(int hour, int min) {
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

        public Time startTime() {
            return new Time(startTime);
        }

        public Time endTime() {
            return new Time(endTime);
        }

        public byte[] build() {
            return Bytes.combine(
                new byte[]{ (byte) enabled },
                new byte[]{ (byte) activeDays },
                Bytes.firstTwoBytesLittleEndian(startTime),
                Bytes.firstTwoBytesLittleEndian(endTime));
        }

        public String toString() {
            return JavaHelpers.autoToString(this, null);
        }
    }
    
}