package com.jwoglom.pumpx2.pump.messages.request.control;

import com.google.common.base.Preconditions;
import com.jwoglom.pumpx2.pump.messages.bluetooth.Characteristic;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.models.KnownApiVersion;
import com.jwoglom.pumpx2.pump.messages.response.control.OpcodeNegative16Response;

/**
 * This occurs after BolusCalcDataSnapshotRequest
 */
@MessageProps(
    opCode=-16,
    size=4, // plus 24 byte padding
    type=MessageType.REQUEST,
    characteristic=Characteristic.CONTROL,
    response=OpcodeNegative16Response.class,
    signed=true,
    minApi=KnownApiVersion.API_V2_5
)
public class OpcodeNegative16Request extends Message {
    private long bolusId;

    public OpcodeNegative16Request() {}

    public OpcodeNegative16Request(long bolusId) {
        this.cargo = buildCargo(bolusId);
        this.bolusId = bolusId;
    }

    public static byte[] buildCargo(long bolusId) {
        return Bytes.combine(
                Bytes.toUint32(bolusId)
        );
    }

    public void parse(byte[] raw) {
        raw = removeSignedRequestHmacBytes(raw);
        Preconditions.checkArgument(raw.length == props().size());
        this.cargo = raw;
        this.bolusId = Bytes.readUint32(raw, 0);
    }

    public long getBolusId() {
        return bolusId;
    }
}