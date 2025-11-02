package com.jwoglom.pumpx2.pump.messages.request.control;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.bluetooth.Characteristic;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.response.control.SetQuickBolusSettingsResponse;
import com.jwoglom.pumpx2.shared.Hex;

import java.util.Arrays;

@MessageProps(
    opCode=-46,
    size=7,
    type=MessageType.REQUEST,
    characteristic=Characteristic.CONTROL,
    signed=true,
    response=SetQuickBolusSettingsResponse.class
)
public class SetQuickBolusSettingsRequest extends Message {

    private boolean enabled;
    private int modeRaw;
    private QuickBolusMode mode;
    private byte[] magic;
    private QuickBolusIncrement increment;

    public SetQuickBolusSettingsRequest() {
        this.cargo = EMPTY;
    }

    public SetQuickBolusSettingsRequest(byte[] raw) {
        parse(raw);
    }

    public SetQuickBolusSettingsRequest(boolean enabled, QuickBolusMode mode, QuickBolusIncrement increment) {
        if (enabled) {
            Validate.isTrue(increment.equals(QuickBolusIncrement.DISABLED), "must specify QuickBolusIncrement.DISABLED when disabled: " + this);
        } else {
            Validate.isTrue(!increment.equals(QuickBolusIncrement.DISABLED), "cannot specify QuickBolusIncrement.DISABLED when enabled: " + this);
        }
        this.cargo = buildCargo(enabled, mode.getRaw(), increment.getMagic());
        parse(cargo);
    }


    public SetQuickBolusSettingsRequest(QuickBolusIncrement increment) {
        this.cargo = buildCargo(increment.isEnabled(), increment.getMode().getRaw(), increment.getMagic());
        parse(cargo);
    }

    public void parse(byte[] raw) { 
        raw = this.removeSignedRequestHmacBytes(raw);
        Validate.isTrue(raw.length == props().size());
        this.cargo = raw;
        this.enabled = raw[0] == 1;
        this.modeRaw = raw[1];
        this.mode = getMode();
        this.magic = Bytes.dropFirstN(raw, 2);
        this.increment = getIncrement();
        checkValidIncrement();
    }

    public static byte[] buildCargo(boolean enabled, int modeRaw, byte[] magic) {
        Validate.isTrue(magic.length == 5);
        return Bytes.combine(
                new byte[]{(byte) (enabled ? 1 : 0)},
                new byte[]{(byte) modeRaw},
                magic
        );
    }


    public boolean isEnabled() {
        return enabled;
    }

    public byte[] getMagic() {
        return magic;
    }

    public enum QuickBolusIncrement {
        DISABLED(false, QuickBolusMode.UNITS, new byte[]{-12,1,-48,7,1}),

        UNITS_0_5(true, QuickBolusMode.UNITS, new byte[]{-12,1,-48,7,1}),
        UNITS_1_0(true, QuickBolusMode.UNITS, new byte[]{-24,3,-48,7,4}),
        UNITS_2_0(true, QuickBolusMode.UNITS, new byte[]{-48,7,-48,7,4}),
        UNITS_5_0(true, QuickBolusMode.UNITS, new byte[]{-120,19,-48,7,4}),

        CARBS_2G(true, QuickBolusMode.CARBS, new byte[]{-120,19,-48,7,8}),
        CARBS_5G(true, QuickBolusMode.CARBS, new byte[]{-120,19,-120,19,8}),
        CARBS_10G(true, QuickBolusMode.CARBS, new byte[]{-120,19,16,39,8}),
        CARBS_15G(true, QuickBolusMode.CARBS, new byte[]{-120,19,-104,58,8}),
        ;

        private final boolean enabled;
        private final QuickBolusMode mode;
        private final byte[] magic;
        QuickBolusIncrement(boolean enabled, QuickBolusMode mode, byte[] magic) {
            this.enabled = enabled;
            this.mode = mode;
            this.magic = magic;
        }

        public QuickBolusMode getMode() {
            return mode;
        }

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
            return this.enabled == o.enabled && this.mode == o.mode && Hex.encodeHexString(this.magic).equals(Hex.encodeHexString(o.magic));
        }

        public static QuickBolusIncrement forMagic(byte[] magic) {
            for (QuickBolusIncrement i : values()) {
                if (i.enabled && Arrays.equals(i.magic, magic)) return i;
            }
            return null;
        }
    }

    public QuickBolusIncrement getIncrement() {
        checkValidIncrement();
        if (this.enabled) {
            return QuickBolusIncrement.forMagic(this.magic);
        } else {
            return QuickBolusIncrement.DISABLED;
        }
    }

    public int getModeRaw() {
        return modeRaw;
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

    public QuickBolusMode getMode() {
        return QuickBolusMode.forRaw(this.modeRaw);
    }

    private void checkValidIncrement() {
        QuickBolusIncrement increment = QuickBolusIncrement.forMagic(this.magic);
        if (increment != null) {
            Validate.isTrue(increment.getMode().getRaw() == getModeRaw(),
                    "invalid mode selection: increment=" + increment + " mode=" + getMode());
        }
    }
}