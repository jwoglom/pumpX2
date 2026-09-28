package com.jwoglom.pumpx2.pump.messages.request.control;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.bluetooth.Characteristic;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.response.control.SetQuickBolusSettingsResponse;
import com.jwoglom.pumpx2.pump.messages.response.currentStatus.PumpGlobalsResponse;

import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

/**
 * The pump applies only the fields named in changedFields. Every other field must still carry the
 * pump's current value, as the Tandem app does: build a change with {@link #forChange}.
 */
@MessageProps(
    opCode=-46,
    size=7,
    type=MessageType.REQUEST,
    characteristic=Characteristic.CONTROL,
    signed=true,
    response=SetQuickBolusSettingsResponse.class
)
public class SetQuickBolusSettingsRequest extends Message {

    /** Milliunits. */
    public static final List<Integer> SUPPORTED_INCREMENT_UNITS = Collections.unmodifiableList(Arrays.asList(500, 1000, 2000, 5000));
    /** Milligrams. */
    public static final List<Integer> SUPPORTED_INCREMENT_CARBS = Collections.unmodifiableList(Arrays.asList(2000, 5000, 10000, 15000));

    private boolean enabled;
    private int modeRaw;
    private int incrementUnits;
    private int incrementCarbs;
    private int changedFieldsRaw;

    public SetQuickBolusSettingsRequest() {
        this.cargo = EMPTY;
    }

    public SetQuickBolusSettingsRequest(byte[] raw) {
        parse(raw);
    }

    /**
     * @param incrementUnits milliunits
     * @param incrementCarbs milligrams
     * @param changedFieldsRaw bitmask of {@link ChangedField}
     */
    public SetQuickBolusSettingsRequest(boolean enabled, int modeRaw, int incrementUnits, int incrementCarbs, int changedFieldsRaw) {
        this.cargo = buildCargo(enabled, modeRaw, incrementUnits, incrementCarbs, changedFieldsRaw);
        parse(cargo);
    }

    public void parse(byte[] raw) {
        raw = this.removeSignedRequestHmacBytes(raw);
        Validate.isTrue(raw.length == props().size());
        this.cargo = raw;
        this.enabled = raw[0] == 1;
        this.modeRaw = raw[1];
        this.incrementUnits = Bytes.readShort(raw, 2);
        this.incrementCarbs = Bytes.readShort(raw, 4);
        this.changedFieldsRaw = raw[6] & 0xFF;
    }

    public static byte[] buildCargo(boolean enabled, int modeRaw, int incrementUnits, int incrementCarbs, int changedFieldsRaw) {
        return Bytes.combine(
                new byte[]{(byte) (enabled ? 1 : 0)},
                new byte[]{(byte) modeRaw},
                Bytes.firstTwoBytesLittleEndian(incrementUnits),
                Bytes.firstTwoBytesLittleEndian(incrementCarbs),
                new byte[]{(byte) changedFieldsRaw}
        );
    }

    /**
     * The request that moves the pump from {@code current} to the given settings, or one with no
     * changed fields when the pump already matches (which need not be sent).
     *
     * @param mode ignored when disabling
     * @param increment milliunits in UNITS mode, milligrams in CARBS mode; ignored when disabling
     */
    public static SetQuickBolusSettingsRequest forChange(PumpGlobalsResponse current, boolean enabled, QuickBolusMode mode, int increment) {
        int units = current.getQuickBolusIncrementUnits();
        int carbs = current.getQuickBolusIncrementCarbs();
        int modeRaw = current.getQuickBolusEntryType();
        Set<ChangedField> changed = EnumSet.noneOf(ChangedField.class);

        if (current.isQuickBolusEnabled() != enabled) {
            changed.add(ChangedField.ENABLED);
        }
        if (enabled) {
            if (modeRaw != mode.getRaw()) {
                changed.add(ChangedField.MODE);
                modeRaw = mode.getRaw();
            }
            if (mode == QuickBolusMode.UNITS) {
                Validate.isTrue(SUPPORTED_INCREMENT_UNITS.contains(increment), "unsupported quick bolus units increment: %d", increment);
                if (units != increment) {
                    changed.add(ChangedField.INCREMENT_UNITS);
                    units = increment;
                }
            } else {
                Validate.isTrue(SUPPORTED_INCREMENT_CARBS.contains(increment), "unsupported quick bolus carbs increment: %d", increment);
                if (carbs != increment) {
                    changed.add(ChangedField.INCREMENT_CARBS);
                    carbs = increment;
                }
            }
        }

        return new SetQuickBolusSettingsRequest(enabled, modeRaw, units, carbs, ChangedField.toBitmask(changed));
    }

    public boolean isEnabled() {
        return enabled;
    }

    public int getModeRaw() {
        return modeRaw;
    }

    public QuickBolusMode getMode() {
        return QuickBolusMode.forRaw(this.modeRaw);
    }

    /** Milliunits. */
    public int getIncrementUnits() {
        return incrementUnits;
    }

    /** Milligrams. */
    public int getIncrementCarbs() {
        return incrementCarbs;
    }

    public int getChangedFieldsRaw() {
        return changedFieldsRaw;
    }

    public Set<ChangedField> getChangedFields() {
        return ChangedField.fromBitmask(changedFieldsRaw);
    }

    public enum ChangedField {
        ENABLED(0x01),
        MODE(0x02),
        INCREMENT_UNITS(0x04),
        INCREMENT_CARBS(0x08),
        ;

        private final int mask;
        ChangedField(int mask) {
            this.mask = mask;
        }

        public int getMask() {
            return mask;
        }

        public static Set<ChangedField> fromBitmask(int bitmask) {
            Set<ChangedField> ret = EnumSet.noneOf(ChangedField.class);
            for (ChangedField f : values()) {
                if ((bitmask & f.mask) != 0) ret.add(f);
            }
            return ret;
        }

        public static int toBitmask(Set<ChangedField> fields) {
            int ret = 0;
            for (ChangedField f : fields) {
                ret |= f.mask;
            }
            return ret;
        }
    }

    public enum QuickBolusMode {
        UNITS(0),
        CARBS(1),

        ;

        private final int raw;
        QuickBolusMode(int raw) {
            this.raw = raw;
        }

        public int getRaw() {
            return raw;
        }

        public static QuickBolusMode forRaw(int raw) {
            for (QuickBolusMode m : values()) {
                if (m.raw == raw) return m;
            }
            return null;
        }
    }
}
