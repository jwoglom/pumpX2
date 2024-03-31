package com.jwoglom.pumpx2.pump.messages.request.control;

import com.google.common.base.Preconditions;
import com.jwoglom.pumpx2.pump.messages.bluetooth.Characteristic;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.models.KnownApiVersion;
import com.jwoglom.pumpx2.pump.messages.models.SupportedDevices;
import com.jwoglom.pumpx2.pump.messages.response.control.SetSleepScheduleResponse;
import com.jwoglom.pumpx2.pump.messages.response.currentStatus.ControlIQSleepScheduleResponse;

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
    
    public SetSleepScheduleRequest() {}

    public SetSleepScheduleRequest(int slot, byte[] rawSchedule, int flag) {
        this.cargo = buildCargo(slot, rawSchedule, flag);
    }

    public SetSleepScheduleRequest(int slot, ControlIQSleepScheduleResponse.SleepSchedule schedule, int flag) {
        this.cargo = buildCargo(slot, schedule.build(), flag);
    }

    public SetSleepScheduleRequest(byte[] raw) {
        parse(raw);
        
    }

    public void parse(byte[] raw) {
        raw = this.removeSignedRequestHmacBytes(raw);
        Preconditions.checkArgument(raw.length == props().size());
        this.cargo = raw;
        this.slot = raw[0];
        this.rawSchedule = Bytes.dropLastN(Bytes.dropFirstN(raw, 1), 1);
        this.flag = raw[7];

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

    public ControlIQSleepScheduleResponse.SleepSchedule getSleepSchedule() {
        return new ControlIQSleepScheduleResponse.SleepSchedule(rawSchedule, 0);
    }

    public int getFlag() {
        return flag;
    }
}