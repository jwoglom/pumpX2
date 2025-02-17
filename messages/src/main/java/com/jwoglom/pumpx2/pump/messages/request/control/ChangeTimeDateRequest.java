package com.jwoglom.pumpx2.pump.messages.request.control;

import com.google.common.base.Preconditions;
import com.jwoglom.pumpx2.pump.messages.bluetooth.Characteristic;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.helpers.Dates;
import com.jwoglom.pumpx2.pump.messages.response.control.ChangeTimeDateResponse;

import java.time.Instant;

@MessageProps(
    opCode=-42,
    size=4,
    type=MessageType.REQUEST,
    characteristic=Characteristic.CONTROL,
    signed=true,
    response=ChangeTimeDateResponse.class
)
public class ChangeTimeDateRequest extends Message {

    private long tandemEpochTime;
    private Instant instant;

    public ChangeTimeDateRequest() {
        this.cargo = EMPTY;
    }

    public ChangeTimeDateRequest(long tandemEpochTime) {
        this.cargo = buildCargo(tandemEpochTime);
        parse(cargo);
    }

    public ChangeTimeDateRequest(Instant timestamp) {
        this(Dates.fromInstantToJan12008EpochSeconds(timestamp));
    }

    public ChangeTimeDateRequest(byte[] raw) {
        parse(raw);
    }

    public void parse(byte[] raw) { 
        raw = this.removeSignedRequestHmacBytes(raw);
        Preconditions.checkArgument(raw.length == props().size());
        this.cargo = raw;
        this.tandemEpochTime = Bytes.readUint32(raw, 0);
        this.instant = getInstant();
        
    }


    public static byte[] buildCargo(long tandemEpochTime) {
        return Bytes.combine(
                Bytes.toUint32(tandemEpochTime)
        );
    }


    public long getTandemEpochTime() {
        return tandemEpochTime;
    }


    public Instant getInstant() {
        return Dates.fromJan12008EpochSecondsToDate(tandemEpochTime);
    }

}