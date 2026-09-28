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
     * Sends the increment's captured cargo verbatim, which applies only the fields that capture
     * flagged: to reach the increment from an arbitrary pump state use {@link #forChange}.
     */
    public SetQuickBolusSettingsRequest(boolean enabled, QuickBolusMode mode, QuickBolusIncrement increment) {
        if (enabled) {
            Validate.isTrue(!increment.equals(QuickBolusIncrement.DISABLED), "cannot specify QuickBolusIncrement.DISABLED when enabled");
        } else {
            Validate.isTrue(increment.equals(QuickBolusIncrement.DISABLED), "must specify QuickBolusIncrement.DISABLED when disabled");
        }
        this.cargo = buildCargo(enabled, mode.getRaw(), increment.magic);
        parse(cargo);
    }

    /** See {@link #SetQuickBolusSettingsRequest(boolean, QuickBolusMode, QuickBolusIncrement)}. */
    public SetQuickBolusSettingsRequest(QuickBolusIncrement increment) {
        this.cargo = buildCargo(increment.isEnabled(), increment.getMode().getRaw(), increment.magic);
        parse(cargo);
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

    public static byte[] buildCargo(boolean enabled, int modeRaw, byte[] magic) {
        Validate.isTrue(magic.length == 5);
        return Bytes.combine(
                new byte[]{(byte) (enabled ? 1 : 0)},
                new byte[]{(byte) modeRaw},
                magic
        );
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

    public static SetQuickBolusSettingsRequest forChange(PumpGlobalsResponse current, QuickBolusIncrement increment) {
        return forChange(current, increment.isEnabled(), increment.getMode(), increment.getIncrement());
    }

    public boolean isEnabled() {
        return enabled;
    }

    /** The increment the pump is left at if it applies this request, or null if not a supported one. */
    public QuickBolusIncrement getIncrement() {
        return QuickBolusIncrement.forSettings(enabled, modeRaw, incrementUnits, incrementCarbs);
    }

    /** @deprecated not an opaque value: use {@link #getIncrementUnits}, {@link #getIncrementCarbs} and {@link #getChangedFields}. */
    @Deprecated
    public byte[] getMagic() {
        return Bytes.dropFirstN(cargo, 2);
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

    public enum QuickBolusIncrement {
        DISABLED(false, QuickBolusMode.UNITS, 0, new byte[]{-12,1,-48,7,1}),

        UNITS_0_5(true, QuickBolusMode.UNITS, 500, new byte[]{-12,1,-48,7,1}),
        UNITS_1_0(true, QuickBolusMode.UNITS, 1000, new byte[]{-24,3,-48,7,4}),
        UNITS_2_0(true, QuickBolusMode.UNITS, 2000, new byte[]{-48,7,-48,7,4}),
        UNITS_5_0(true, QuickBolusMode.UNITS, 5000, new byte[]{-120,19,-48,7,4}),

        CARBS_2G(true, QuickBolusMode.CARBS, 2000, new byte[]{-120,19,-48,7,8}),
        CARBS_5G(true, QuickBolusMode.CARBS, 5000, new byte[]{-120,19,-120,19,8}),
        CARBS_10G(true, QuickBolusMode.CARBS, 10000, new byte[]{-120,19,16,39,8}),
        CARBS_15G(true, QuickBolusMode.CARBS, 15000, new byte[]{-120,19,-104,58,8}),
        ;

        private final boolean enabled;
        private final QuickBolusMode mode;
        private final int increment;
        // One captured Tandem app write per increment, not a derivation of it.
        private final byte[] magic;
        QuickBolusIncrement(boolean enabled, QuickBolusMode mode, int increment, byte[] magic) {
            this.enabled = enabled;
            this.mode = mode;
            this.increment = increment;
            this.magic = magic;
        }

        public QuickBolusMode getMode() {
            return mode;
        }

        /** Milliunits in UNITS mode, milligrams in CARBS mode; 0 for DISABLED. */
        public int getIncrement() {
            return increment;
        }

        /** @deprecated a captured cargo tail, not an encoding of this increment. */
        @Deprecated
        public byte[] getMagic() {
            return magic;
        }

        public boolean isEnabled() {
            return enabled;
        }

        public boolean getEnabled() {
            return enabled;
        }

        public boolean isEqual(QuickBolusIncrement o) {
            return this.enabled == o.enabled && this.mode == o.mode && Arrays.equals(this.magic, o.magic);
        }

        public static QuickBolusIncrement forMagic(byte[] magic) {
            for (QuickBolusIncrement i : values()) {
                if (i.enabled && Arrays.equals(i.magic, magic)) return i;
            }
            return null;
        }

        /** @return null when enabled at an increment or mode the pump does not support */
        public static QuickBolusIncrement forSettings(boolean enabled, int modeRaw, int incrementUnits, int incrementCarbs) {
            if (!enabled) {
                return DISABLED;
            }
            QuickBolusMode mode = QuickBolusMode.forRaw(modeRaw);
            if (mode == null) {
                return null;
            }
            int increment = mode == QuickBolusMode.UNITS ? incrementUnits : incrementCarbs;
            for (QuickBolusIncrement i : values()) {
                if (i.enabled && i.mode == mode && i.increment == increment) return i;
            }
            return null;
        }
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
