package com.jwoglom.pumpx2.pump.messages.request.control;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.bluetooth.Characteristic;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.response.control.ChangeControlIQSettingsResponse;

import java.util.Set;
import java.util.TreeSet;

@MessageProps(
    opCode=-54,
    size=6,
    type=MessageType.REQUEST,
    characteristic=Characteristic.CONTROL,
    signed=true,
    response=ChangeControlIQSettingsResponse.class
)
public class ChangeControlIQSettingsRequest extends Message {

    private boolean enabled;
    private int weightLbs;
    private int totalDailyInsulinUnits;
    private int changeBitmaskRaw;
    private Set<ChangeBitmask> changeBitmask;

    public ChangeControlIQSettingsRequest() {
        this.cargo = EMPTY;
    }

    public ChangeControlIQSettingsRequest(byte[] raw) {
        parse(raw);
    }

    public ChangeControlIQSettingsRequest(boolean enabled, int weightLbs, int totalDailyInsulinUnits) {
        this(enabled, weightLbs, totalDailyInsulinUnits, ChangeBitmask.ENABLED.id());
    }

    public ChangeControlIQSettingsRequest(boolean enabled, int weightLbs, int totalDailyInsulinUnits, Set<ChangeBitmask> changeBitmask) {
        this(enabled, weightLbs, totalDailyInsulinUnits, ChangeBitmask.toBitmask(changeBitmask));
    }

    public ChangeControlIQSettingsRequest(boolean enabled, int weightLbs, int totalDailyInsulinUnits, int changeBitmaskRaw) {
        parse(buildCargo(enabled, weightLbs, totalDailyInsulinUnits, changeBitmaskRaw));
    }

    public void parse(byte[] raw) { 
        raw = this.removeSignedRequestHmacBytes(raw);
        Validate.isTrue(raw.length == props().size());
        this.cargo = raw;
        this.enabled = raw[0] == 1;
        this.weightLbs = Bytes.readShort(raw, 1);
        this.totalDailyInsulinUnits = raw[4];
        this.changeBitmaskRaw = raw[5];
        this.changeBitmask = ChangeBitmask.fromBitmask(changeBitmaskRaw);
    }


    public static byte[] buildCargo(boolean enabled, int weightLbs, int totalDailyInsulinUnits) {
        return buildCargo(enabled, weightLbs, totalDailyInsulinUnits, ChangeBitmask.ENABLED.id());
    }

    public static byte[] buildCargo(boolean enabled, int weightLbs, int totalDailyInsulinUnits, int changeBitmask) {
        return Bytes.combine(
                new byte[]{(byte)(enabled ? 1 : 0)},
                Bytes.firstTwoBytesLittleEndian(weightLbs),
                new byte[]{
                        1,
                        (byte) totalDailyInsulinUnits,
                        (byte) changeBitmask
                }
        );
    }


    public boolean isEnabled() {
        return enabled;
    }

    public int getWeightLbs() {
        return weightLbs;
    }

    public int getTotalDailyInsulinUnits() {
        return totalDailyInsulinUnits;
    }

    public int getChangeBitmaskRaw() {
        return changeBitmaskRaw;
    }

    public Set<ChangeBitmask> getChangeBitmask() {
        return changeBitmask;
    }

    /**
     * Which of the settings in the request the pump should apply.
     */
    public enum ChangeBitmask {
        ENABLED(1),
        WEIGHT(2),
        TOTAL_DAILY_INSULIN(4),

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
    }
}
