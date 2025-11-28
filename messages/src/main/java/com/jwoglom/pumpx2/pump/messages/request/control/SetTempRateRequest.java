package com.jwoglom.pumpx2.pump.messages.request.control;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.bluetooth.Characteristic;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.models.KnownApiVersion;
import com.jwoglom.pumpx2.pump.messages.models.SupportedDevices;
import com.jwoglom.pumpx2.pump.messages.response.control.SetTempRateResponse;

/**
 * Sets a temp rate for basal delivery. Note that ControlIQ must be off before setting a temp rate.
 */
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

    public static int MIN_MINUTES = 15;
    public static int MAX_MINUTES = 72 * 60;

    public static int MIN_PERCENT = 0;
    public static int MAX_PERCENT = 250;


    /**
     * Sets a temp rate for basal delivery.
     *
     * @param minutes between 15 minutes and 72 hours. durations shorter than 15 minutes are not supported and the pump will return an error
     * @param percent between 0 percent and 250 percent. values above 250% are not supported and the pump will return an error
     */
    public SetTempRateRequest(int minutes, int percent) {
        Validate.isTrue(minutes >= MIN_MINUTES && minutes <= MAX_MINUTES, "duration of temp rate must be between 15 and 4,320 minutes (72 hours)");
        Validate.isTrue(percent >= MIN_PERCENT && percent <= MAX_PERCENT, "percent temp rate must be between 0-250%");
        this.cargo = buildCargo(minutes, percent);
        this.minutes = minutes;
        this.percent = percent;
    }

    public void parse(byte[] raw) {
        raw = this.removeSignedRequestHmacBytes(raw);
        Validate.isTrue(raw.length == props().size());
        this.cargo = raw;
        this.minutes = Math.toIntExact(Bytes.readUint32(raw, 0) / 1000 / 60);
        this.percent = Bytes.readShort(raw, 4);
    }

    
    public static byte[] buildCargo(int minutes, int percent) {
        return Bytes.combine(
                Bytes.toUint32(minutes * 1000L * 60L),
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