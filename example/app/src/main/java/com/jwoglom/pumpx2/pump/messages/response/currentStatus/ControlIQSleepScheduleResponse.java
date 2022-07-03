package com.jwoglom.pumpx2.pump.messages.response.currentStatus;

import com.google.common.base.Preconditions;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.models.MinsTime;
import com.jwoglom.pumpx2.pump.messages.models.MultiDay;
import com.jwoglom.pumpx2.pump.messages.request.currentStatus.ControlIQSleepScheduleRequest;
import com.jwoglom.pumpx2.shared.JavaHelpers;

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

        public Set<MultiDay> activeDays() {
            return MultiDay.fromBitmask(activeDays);
        }

        public MinsTime startTime() {
            return new MinsTime(startTime);
        }

        public MinsTime endTime() {
            return new MinsTime(endTime);
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