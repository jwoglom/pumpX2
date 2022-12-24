package com.jwoglom.pumpx2.pump.messages.request.control;

import com.google.common.base.Preconditions;
import com.jwoglom.pumpx2.pump.messages.bluetooth.Characteristic;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.models.KnownApiVersion;
import com.jwoglom.pumpx2.pump.messages.response.control.RemoteCarbEntryResponse;

/**
 * Saves the entered carbs as a history log entry which populates it in the pump's graph.
 * If not called, then the bolus appears in the mobile app / t:connect without the carb amount.
 *
 * pumpTime is the ACTUAL pump time seconds since boot, not the timesincereset EPOCH value which is
 * used for signing messages.
 *
 * called before InitiateBolusRequest
 */
@MessageProps(
    opCode=-14,
    size=9,
    type=MessageType.REQUEST,
    characteristic=Characteristic.CONTROL,
    response=RemoteCarbEntryResponse.class,
    signed=true,
    minApi=KnownApiVersion.API_V2_5
)
public class RemoteCarbEntryRequest extends Message {
    private int carbs;
    private int unknown;
    private long pumpTime;
    private int bolusId;

    public RemoteCarbEntryRequest() {}

    public RemoteCarbEntryRequest(int carbs, long pumpTimeSecondsSinceBoot, int bolusId) {
        this(carbs, 1, pumpTimeSecondsSinceBoot, bolusId);
    }

    public RemoteCarbEntryRequest(int carbs, int unknown, long pumpTimeSecondsSinceBoot, int bolusId) {
        this.cargo = buildCargo(carbs, unknown, pumpTimeSecondsSinceBoot, bolusId);
        this.carbs = carbs;
        this.unknown = unknown; // from examples, always 1
        this.pumpTime = pumpTimeSecondsSinceBoot;
        this.bolusId = bolusId;
    }

    public static byte[] buildCargo(int carbs, int unknown, long pumpTime, int bolusId) {
        return Bytes.combine(
                Bytes.firstTwoBytesLittleEndian(carbs),
                new byte[]{(byte) unknown},
                Bytes.toUint32(pumpTime),
                Bytes.firstTwoBytesLittleEndian(bolusId)
        );
    }

    public void parse(byte[] raw) {
        raw = removeSignedRequestHmacBytes(raw);
        Preconditions.checkArgument(raw.length == props().size(), "size " + raw.length);
        this.cargo = raw;
        this.carbs = Bytes.readShort(raw, 0);
        this.unknown = raw[2];
        this.pumpTime = Bytes.readUint32(raw, 3);
        this.bolusId = Bytes.readShort(raw, 7);
    }

    public int getCarbs() {
        return carbs;
    }

    public long getPumpTime() {
        return pumpTime;
    }

    public int getBolusId() {
        return bolusId;
    }
}