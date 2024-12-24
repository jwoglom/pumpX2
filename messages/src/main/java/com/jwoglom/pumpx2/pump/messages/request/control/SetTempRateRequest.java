package com.jwoglom.pumpx2.pump.messages.request.control;

import com.google.common.base.Preconditions;
import com.jwoglom.pumpx2.pump.messages.bluetooth.Characteristic;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.models.KnownApiVersion;
import com.jwoglom.pumpx2.pump.messages.models.SupportedDevices;
import com.jwoglom.pumpx2.pump.messages.response.control.SetTempRateResponse;

@MessageProps(
    opCode=-92,
    size=6, // 30 with signed
    type=MessageType.REQUEST,
    characteristic=Characteristic.CONTROL,
    signed=true,
    modifiesInsulinDelivery=true,
    minApi=KnownApiVersion.MOBI_API_V3_5,
    supportedDevices=SupportedDevices.MOBI_ONLY,
    response=SetTempRateResponse.class
)
public class SetTempRateRequest extends Message {

    private int minutes;
    private int percent;
    
    public SetTempRateRequest() {}

    public SetTempRateRequest(byte[] raw) {
        parse(raw);
    }

    public SetTempRateRequest(int minutes, int percent) {
        Preconditions.checkArgument(minutes >= 2, "duration of temp rate must be 2 minutes or greater");
        Preconditions.checkArgument(percent >= 0 && percent <= 250, "percent temp rate must be between 0-250%");
        this.cargo = buildCargo(minutes, percent);
        this.minutes = minutes;
        this.percent = percent;
    }

    public void parse(byte[] raw) {
        raw = this.removeSignedRequestHmacBytes(raw);
        Preconditions.checkArgument(raw.length == props().size());
        this.cargo = raw;
        this.minutes = Bytes.readShort(raw, 2) + 2;
        this.percent = Bytes.readShort(raw, 4);
    }

    
    public static byte[] buildCargo(int minutes, int percent) {
        byte[] pfx = new byte[]{-96, -69};
//        if (percent == 0) {
//            pfx = new byte[]{0, -90};
//        }
        return Bytes.combine(
                pfx,
                Bytes.firstTwoBytesLittleEndian(minutes - 2),
                Bytes.firstTwoBytesLittleEndian(percent)
        );
    }


    public int getMinutes() {
        return minutes;
    }

    public int getPercent() {
        return percent;
    }
}