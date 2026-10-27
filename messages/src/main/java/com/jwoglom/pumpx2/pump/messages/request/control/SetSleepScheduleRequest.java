package com.jwoglom.pumpx2.pump.messages.request.control;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.bluetooth.Characteristic;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.models.KnownApiVersion;
import com.jwoglom.pumpx2.pump.messages.models.SupportedDevices;
import com.jwoglom.pumpx2.pump.messages.response.control.SetSleepScheduleResponse;
import com.jwoglom.pumpx2.pump.messages.response.currentStatus.ControlIQSleepScheduleResponse.SleepSchedule;

import java.util.Set;
import java.util.TreeSet;

@MessageProps(
    opCode=-50,
    size=8,
    type=MessageType.REQUEST,
    characteristic=Characteristic.CONTROL,
    signed=true,
    minApi=KnownApiVersion.MOBI_API_V3_5,
    supportedDevices=SupportedDevices.MOBI_ONLY,
    response=SetSleepScheduleResponse.class
)
public class SetSleepScheduleRequest extends Message {

    private int slot;
    private byte[] rawSchedule;
    private int flag;
    private Set<ChangeBitmask> changeBitmask;
    
    public SetSleepScheduleRequest() {}

    public SetSleepScheduleRequest(int slot, byte[] rawSchedule, int flag) {
        parse(buildCargo(slot, rawSchedule, flag));
    }

    public SetSleepScheduleRequest(int slot, SleepSchedule schedule, int flag) {
        this(slot, schedule.build(), flag);
    }

    /**
     * @param changes the fields of the schedule the pump should apply. {@link ChangeBitmask#BASE} is always added.
     */
    public SetSleepScheduleRequest(int slot, SleepSchedule schedule, Set<ChangeBitmask> changes) {
        this(slot, schedule, ChangeBitmask.toBitmask(changes) | ChangeBitmask.BASE.id());
    }

    /**
     * Replaces the schedule in the slot, flagging the fields that differ from the previous
     * schedule (as last read with ControlIQSleepScheduleRequest), as the Mobi app does.
     */
    public SetSleepScheduleRequest(int slot, SleepSchedule previous, SleepSchedule updated) {
        this(slot, updated, ChangeBitmask.between(previous, updated));
    }

    public SetSleepScheduleRequest(byte[] raw) {
        parse(raw);
        
    }

    public void parse(byte[] raw) {
        raw = this.removeSignedRequestHmacBytes(raw);
        Validate.isTrue(raw.length == props().size());
        this.cargo = raw;
        this.slot = raw[0];
        this.rawSchedule = Bytes.dropLastN(Bytes.dropFirstN(raw, 1), 1);
        this.flag = raw[7] & 0xFF;
        this.changeBitmask = ChangeBitmask.fromBitmask(flag);
    }

    
    public static byte[] buildCargo(int slot, byte[] schedule, int flag) {
        return Bytes.combine(
            new byte[]{ (byte) slot },
            schedule,
            new byte[]{ (byte) flag }
        );
    }

    public int getSlot() {
        return slot;
    }

    public byte[] getRawSchedule() {
        return rawSchedule;
    }

    public SleepSchedule getSleepSchedule() {
        return new SleepSchedule(rawSchedule, 0);
    }

    public int getFlag() {
        return flag;
    }

    public Set<ChangeBitmask> getChangeBitmask() {
        return changeBitmask;
    }

    /**
     * The last cargo byte: which fields of the schedule the pump should apply.
     *
     * The Mobi app sends BASE plus one bit for each field that differs from the schedule on the
     * pump, and the pump then records one AaSleepScheduleChangeHistoryLog per flagged field.
     * Whether the pump ignores a changed field whose bit is not set has not been tested.
     */
    public enum ChangeBitmask {
        /** Set by the Mobi app on every request; meaning unknown. */
        BASE(1),
        ENABLED(2),
        ACTIVE_DAYS(4),
        START_TIME(8),
        END_TIME(16),

        ;

        private final int id;

        ChangeBitmask(int id) {
            this.id = id;
        }

        public int id() {
            return id;
        }

        public static Set<ChangeBitmask> fromBitmask(int bitmask) {
            Set<ChangeBitmask> set = new TreeSet<>();
            for (ChangeBitmask i : values()) {
                if ((bitmask & i.id()) != 0) {
                    set.add(i);
                }
            }

            return set;
        }

        public static int toBitmask(ChangeBitmask... items) {
            int mask = 0;
            for (ChangeBitmask i : items) {
                mask |= i.id();
            }

            return mask;
        }

        public static int toBitmask(Set<ChangeBitmask> items) {
            return toBitmask(items.toArray(new ChangeBitmask[0]));
        }

        /**
         * The fields that differ between two schedules, not including BASE.
         */
        public static Set<ChangeBitmask> between(SleepSchedule previous, SleepSchedule updated) {
            Set<ChangeBitmask> set = new TreeSet<>();
            if (previous.getEnabled() != updated.getEnabled()) {
                set.add(ENABLED);
            }
            if (!previous.activeDays().equals(updated.activeDays())) {
                set.add(ACTIVE_DAYS);
            }
            if (!previous.startTime().equals(updated.startTime())) {
                set.add(START_TIME);
            }
            if (!previous.endTime().equals(updated.endTime())) {
                set.add(END_TIME);
            }

            return set;
        }
    }
}