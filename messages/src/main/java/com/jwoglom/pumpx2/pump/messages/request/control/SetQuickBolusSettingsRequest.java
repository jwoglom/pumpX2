package com.jwoglom.pumpx2.pump.messages.request.control;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.bluetooth.Characteristic;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.response.control.SetQuickBolusSettingsResponse;

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
        this.cargo = buildCargo(enabled, mode.getRaw(), increment.getMagic());
        this.enabled = enabled;
        this.modeRaw = mode.getRaw();
        this.magic = increment.getMagic();
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
        UNITS_0_5(QuickBolusMode.UNITS, new byte[]{-12,1,-48,7,1}),
        UNITS_1_0(QuickBolusMode.UNITS, new byte[]{-24,3,-48,7,4}),
        UNITS_2_O(QuickBolusMode.UNITS, new byte[]{-48,7,-48,7,4}),
        UNITS_5_0(QuickBolusMode.UNITS, new byte[]{-120,19,-48,7,4}),

        CARBS_2G(QuickBolusMode.CARBS, new byte[]{-120,19,-48,7,8}),
        CARBS_5G(QuickBolusMode.CARBS, new byte[]{-120,19,-120,19,8}),
        CARBS_10G(QuickBolusMode.CARBS, new byte[]{-120,19,16,39,8}),
        CARBS_15G(QuickBolusMode.CARBS, new byte[]{-120,19,-104,58,8}),
        ;

        private final QuickBolusMode mode;
        private final byte[] magic;
        QuickBolusIncrement(QuickBolusMode mode, byte[] magic) {
            this.mode = mode;
            this.magic = magic;
        }

        public QuickBolusMode getMode() {
            return mode;
        }

        public byte[] getMagic() {
            return magic;
        }

        public static QuickBolusIncrement forMagic(byte[] magic) {
            for (QuickBolusIncrement i : values()) {
                if (Arrays.equals(i.magic, magic)) return i;
            }
            return null;
        }
    }

    public QuickBolusIncrement getIncrement() {
        checkValidIncrement();
        return QuickBolusIncrement.forMagic(this.magic);
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