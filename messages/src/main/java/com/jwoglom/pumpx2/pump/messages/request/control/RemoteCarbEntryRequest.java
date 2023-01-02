package com.jwoglom.pumpx2.pump.messages.request.control;

import com.google.common.base.Preconditions;
import com.jwoglom.pumpx2.pump.messages.bluetooth.Characteristic;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.helpers.Dates;
import com.jwoglom.pumpx2.pump.messages.models.KnownApiVersion;
import com.jwoglom.pumpx2.pump.messages.response.control.BolusPermissionResponse;
import com.jwoglom.pumpx2.pump.messages.response.control.RemoteCarbEntryResponse;
import com.jwoglom.pumpx2.pump.messages.response.currentStatus.TimeSinceResetResponse;

/**
 * Saves the entered carbs as a history log entry which populates it in the pump's graph.
 * If not called, then the bolus appears in the mobile app / t:connect without the carb amount.
 *
 * pumpTime is the ACTUAL pump time seconds since boot, and can be returned from a TimeSinceResetResponse
 * via {@link TimeSinceResetResponse#getPumpTimeSecondsSinceReset()}
 *
 * This should be called before {@link InitiateBolusRequest}
 */
@MessageProps(
    opCode=-14,
    size=9, // 33 with 24 byte padding
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

    /**
     * Creates a request to affix information on the given number of carbs (in grams) to the
     * bolus ID (which must be in progress; in a state between calling {@link BolusPermissionRequest}
     * and {@link InitiateBolusRequest})
     * @param carbs the number of carbs in grams
     * @param pumpTimeSecondsSinceBoot the output of {@link TimeSinceResetResponse#getPumpTimeSecondsSinceReset()}
     * @param bolusId the bolus ID returned from {@link BolusPermissionResponse#getBolusId()}
     */
    public RemoteCarbEntryRequest(int carbs, long pumpTimeSecondsSinceBoot, int bolusId) {
        this(carbs, 1, pumpTimeSecondsSinceBoot, bolusId);
    }

    public RemoteCarbEntryRequest(int carbs, int unknown, long pumpTimeSecondsSinceBoot, int bolusId) {
        Preconditions.checkArgument(pumpTimeSecondsSinceBoot < Dates.JANUARY_1_2008_UNIX_EPOCH, "pumpTimeSecondsSinceBoot ("+pumpTimeSecondsSinceBoot+") should be seconds since boot; a unix epoch was provided instead");
        this.cargo = buildCargo(carbs, unknown, pumpTimeSecondsSinceBoot, bolusId);
        this.carbs = carbs;
        this.unknown = unknown; // from examples, always 1
        this.pumpTime = pumpTimeSecondsSinceBoot;
        this.bolusId = bolusId;
    }

    public static byte[] buildCargo(int carbs, int unknown, long pumpTimeSecondsSinceBoot, int bolusId) {
        return Bytes.combine(
                Bytes.firstTwoBytesLittleEndian(carbs),
                new byte[]{(byte) unknown},
                Bytes.toUint32(pumpTimeSecondsSinceBoot),
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