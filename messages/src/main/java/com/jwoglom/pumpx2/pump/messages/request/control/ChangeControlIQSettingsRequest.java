package com.jwoglom.pumpx2.pump.messages.request.control;

import com.google.common.base.Preconditions;
import com.jwoglom.pumpx2.pump.messages.bluetooth.Characteristic;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.response.control.ChangeControlIQSettingsResponse;

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

    public ChangeControlIQSettingsRequest() {
        this.cargo = EMPTY;
    }

    public ChangeControlIQSettingsRequest(byte[] raw) {
        parse(raw);
    }

    public ChangeControlIQSettingsRequest(boolean enabled, int weightLbs, int totalDailyInsulinUnits) {
        parse(buildCargo(enabled, weightLbs, totalDailyInsulinUnits));
    }

    public void parse(byte[] raw) { 
        raw = this.removeSignedRequestHmacBytes(raw);
        Preconditions.checkArgument(raw.length == props().size());
        this.cargo = raw;
        this.enabled = raw[0] == 1;
        this.weightLbs = Bytes.readShort(raw, 1);
        this.totalDailyInsulinUnits = raw[4];
    }


    public static byte[] buildCargo(boolean enabled, int weightLbs, int totalDailyInsulinUnits) {
        return Bytes.combine(
                new byte[]{(byte)(enabled ? 1 : 0)},
                Bytes.firstTwoBytesLittleEndian(weightLbs),
                new byte[]{
                        1,
                        (byte) totalDailyInsulinUnits,
                        1
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
}