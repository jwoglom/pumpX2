package com.jwoglom.pumpx2.pump.messages.request.control;

import com.google.common.base.Preconditions;
import com.jwoglom.pumpx2.pump.messages.bluetooth.Characteristic;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.models.KnownApiVersion;
import com.jwoglom.pumpx2.pump.messages.models.SupportedDevices;
import com.jwoglom.pumpx2.pump.messages.response.control.SetModesResponse;

@MessageProps(
    opCode=-52,
    size=1,
    type=MessageType.REQUEST,
    characteristic=Characteristic.CONTROL,
    signed=true,
    minApi=KnownApiVersion.MOBI_API_V3_5,
    supportedDevices=SupportedDevices.MOBI_ONLY,
    response=SetModesResponse.class
)
public class SetModesRequest extends Message { 
    private int bitmap;
    
    public SetModesRequest() {}

    public SetModesRequest(int bitmap) {
        parse(buildCargo(bitmap));
    }

    public SetModesRequest(ModeCommand mode) {
        this(mode.getBitmap());
    }

    public SetModesRequest(byte[] raw) {
        this.cargo = raw;
        this.bitmap = raw[0];
    }

    public void parse(byte[] raw) {
        raw = this.removeSignedRequestHmacBytes(raw);
        Preconditions.checkArgument(raw.length == props().size(), "got "+raw.length);
        this.cargo = raw;
        this.bitmap = raw[0];
    }

    
    public static byte[] buildCargo(int bitmap) {
        return Bytes.combine(
            // 10 at 0
            new byte[]{(byte) bitmap}
        );
    }

    public int getBitmap() {
        return bitmap;
    }

    public ModeCommand getCommand() {
        return ModeCommand.fromBitmap(bitmap);
    }

    public enum ModeCommand {
        SLEEP_MODE_ON(1),
        SLEEP_MODE_OFF(2),
        EXERCISE_MODE_ON(3),
        EXERCISE_MODE_OFF(4),
        ;

        private int bitmap;
        ModeCommand(int bitmap) {
            this.bitmap = bitmap;
        }

        public int getBitmap() {
            return bitmap;
        }

        public static ModeCommand fromBitmap(int bitmap) {
            for (ModeCommand c : values()) {
                if (c.getBitmap() == bitmap) {
                    return c;
                }
            }
            return null;
        }
    }
    
    
}