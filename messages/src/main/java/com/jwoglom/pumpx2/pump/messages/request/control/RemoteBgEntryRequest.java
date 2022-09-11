package com.jwoglom.pumpx2.pump.messages.request.control;

import com.google.common.base.Preconditions;
import com.jwoglom.pumpx2.pump.messages.bluetooth.Characteristic;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.models.KnownApiVersion;
import com.jwoglom.pumpx2.pump.messages.response.control.RemoteBgEntryResponse;

/**
 * Adds the BG entered in the bolus window to the pump as a BG entry.
 * pumpTime is the ACTUAL pump time seconds since boot, not the timesincereset EPOCH value which is
 * used for signing messages.
 *
 */
@MessageProps(
    opCode=-74,
    size=11, // 35 with 24 byte padding
    type=MessageType.REQUEST,
    characteristic=Characteristic.CONTROL,
    response=RemoteBgEntryResponse.class,
    signed=true,
    minApi=KnownApiVersion.API_V2_5
)
public class RemoteBgEntryRequest extends Message {
    private int bg;
    private boolean isAutopopBg;
    private long pumpTime;
    private int bolusId;
    
    public RemoteBgEntryRequest() {}

    public RemoteBgEntryRequest(int bg, boolean isAutopopBg, long pumpTimeSecondsSinceBoot, int bolusId) {
        this.cargo = buildCargo(bg, isAutopopBg, pumpTimeSecondsSinceBoot, bolusId);
        this.bg = bg;
        this.isAutopopBg = isAutopopBg;
        this.pumpTime = pumpTimeSecondsSinceBoot;
        this.bolusId = bolusId;
        
    }

    public void parse(byte[] raw) {
        raw = removeSignedRequestHmacBytes(raw);
        Preconditions.checkArgument(raw.length == props().size());
        this.cargo = raw;
        this.bg = Bytes.readShort(raw, 0);
        this.isAutopopBg = (raw[4] == 1);
        this.pumpTime = Bytes.readUint32(raw, 5);
        this.bolusId = Bytes.readShort(raw, 9);
    }


    public static byte[] buildCargo(int bg, boolean isAutopopBg, long pumpTime, int bolusId) {
        return Bytes.combine(
            Bytes.firstTwoBytesLittleEndian(bg),
            new byte[]{0, 0},
            new byte[]{(byte) (isAutopopBg ? 1 : 0)},
            Bytes.toUint32(pumpTime),
            Bytes.firstTwoBytesLittleEndian(bolusId)
        );
    }

    public int getBG() {
        return bg;
    }

    public boolean getIsAutopopBg() {
        return isAutopopBg;
    }

    public long getPumpTime() {
        return pumpTime;
    }

    public int getBolusId() {
        return bolusId;
    }
    
    
}