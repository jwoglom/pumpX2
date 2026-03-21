package com.jwoglom.pumpx2.pump.messages.request.control;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.bluetooth.Characteristic;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.models.KnownApiVersion;
import com.jwoglom.pumpx2.pump.messages.response.control.BolusPermissionReleaseResponse;

/**
 * This occurs after BolusCalcDataSnapshotRequest and has an input of a bolusId which was returned
 * by BolusPermissionRequest. It returns a status of 0 if the bolus permission was successfully
 * released (i.e. relinquished), otherwise 1 if the bolus isn't an ID which was requested.
 */
@MessageProps(
    opCode=-16,
    size=4, // plus 24 byte padding
    type=MessageType.REQUEST,
    characteristic=Characteristic.CONTROL,
    response= BolusPermissionReleaseResponse.class,
    signed=true,
    minApi=KnownApiVersion.API_V2_5
)
public class BolusPermissionReleaseRequest extends Message {
    private int bolusID;
    private int reserve;

    public BolusPermissionReleaseRequest() {}

    public BolusPermissionReleaseRequest(int bolusID) {
        this(bolusID, 0);
    }

    public BolusPermissionReleaseRequest(int bolusID, int reserve) {
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