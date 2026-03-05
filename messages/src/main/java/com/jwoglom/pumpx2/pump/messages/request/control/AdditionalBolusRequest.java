package com.jwoglom.pumpx2.pump.messages.request.control;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.bluetooth.Characteristic;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.response.control.AdditionalBolusResponse;

/**
 * Requests an additional bolus for a previously-permitted bolusID.
 * OpCode 0xFA (250).
 */
@MessageProps(
    opCode=-6,
    size=4,
    type=MessageType.REQUEST,
    characteristic=Characteristic.CONTROL,
    response=AdditionalBolusResponse.class,
    signed=true,
    modifiesInsulinDelivery=true
)
public class AdditionalBolusRequest extends Message {
    private int bolusID;
    private int reserve;

    public AdditionalBolusRequest() {}

    public AdditionalBolusRequest(int bolusID) {
        this(bolusID, 0);
    }

    public AdditionalBolusRequest(int bolusID, int reserve) {
        this.cargo = buildCargo(bolusID, reserve);
        this.bolusID = bolusID;
        this.reserve = reserve;
    }

    public void parse(byte[] raw) {
        raw = removeSignedRequestHmacBytes(raw);
        Validate.isTrue(raw.length == props().size());
        this.cargo = raw;
        this.bolusID = Bytes.readShort(raw, 0);
        this.reserve = Bytes.readShort(raw, 2);
    }

    public static byte[] buildCargo(int bolusID, int reserve) {
        return Bytes.combine(
            Bytes.firstTwoBytesLittleEndian(bolusID),
            Bytes.firstTwoBytesLittleEndian(reserve)
        );
    }

    public int getBolusID() {
        return bolusID;
    }

    public int getReserve() {
        return reserve;
    }
}
