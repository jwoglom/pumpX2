package com.jwoglom.pumpx2.pump.messages.response.currentStatus;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.bluetooth.Characteristic;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.helpers.Dates;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.request.currentStatus.TempRateStatusRequest;

import java.time.Instant;

@MessageProps(
    opCode=31,
    size=16,
    type=MessageType.RESPONSE,
    characteristic=Characteristic.CURRENT_STATUS,
    request=TempRateStatusRequest.class
)
public class TempRateStatusResponse extends Message {
    private boolean active;
    private int tempRateId;
    private int percentage;
    private long startTimeRaw;
    private long secondsSincePumpReset;
    private long duration;

    public TempRateStatusResponse() {
        this.cargo = EMPTY;
    }


    public TempRateStatusResponse(byte[] raw) {
        this.cargo = raw;
        parse(raw);
    }

    public void parse(byte[] raw) {
        Validate.isTrue(raw.length == props().size());
        this.cargo = raw;
        this.active = raw[0] != 0;
        this.tempRateId = Bytes.readShort(raw, 1);
        this.percentage = (int) (raw[3] & 255);
        this.startTimeRaw = Bytes.readUint32(raw, 4);
        this.secondsSincePumpReset = Bytes.readUint32(raw, 8);
        this.duration = Bytes.readUint32(raw, 12);
    }


    public boolean getActive() {
        return active;
    }

    public int getTempRateId() {
        return tempRateId;
    }

    public int getPercentage() {
        return percentage;
    }

    public long getStartTimeRaw() {
        return startTimeRaw;
    }

    /**
     * @return the evaluation of startTimeRaw against the pump epoch (Jan 1, 2008)
     */
    public Instant getStartTimeInstant() {
        return Dates.fromJan12008EpochSecondsToDate(startTimeRaw);
    }

    public long getSecondsSincePumpReset() {
        return secondsSincePumpReset;
    }

    public long getDuration() {
        return duration;
    }
}
