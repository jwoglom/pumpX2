package com.jwoglom.pumpx2.pump.messages.request.control;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.bluetooth.Characteristic;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.models.KnownApiVersion;
import com.jwoglom.pumpx2.pump.messages.response.control.BolusPermissionResponse;
import com.jwoglom.pumpx2.pump.messages.response.control.RemoteBgEntryResponse;
import com.jwoglom.pumpx2.pump.messages.response.currentStatus.TimeSinceResetResponse;

/**
 * Adds the BG entered in the bolus window to the pump as a BG entry.
 *
 * pumpTime is the ACTUAL pump time seconds since boot, and can be returned from a TimeSinceResetResponse
 * via {@link TimeSinceResetResponse#getPumpTimeSecondsSinceReset()}
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

    /**
     * Creates a request to add a data point of the current BG to the provided bolus ID (which must
     * be in progress; in a state between calling {@link BolusPermissionRequest} and {@link InitiateBolusRequest})
     * @param bg the blood glucose value in mg/dL
     * @param isAutopopBg true if the BG was autopopulated from the CGM; false if manually entered
     * @param pumpTimeSecondsSinceBoot the output of {@link TimeSinceResetResponse#getPumpTimeSecondsSinceReset()}
     * @param bolusId the bolus ID returned from {@link BolusPermissionResponse#getBolusId()}
     */
    public RemoteBgEntryRequest(int bg, boolean isAutopopBg, long pumpTimeSecondsSinceBoot, int bolusId) {
        this.cargo = buildCargo(bg, isAutopopBg, pumpTimeSecondsSinceBoot, bolusId);
        this.bg = bg;
        this.isAutopopBg = isAutopopBg;
        this.pumpTime = pumpTimeSecondsSinceBoot;
        this.bolusId = bolusId;
    }

    public void parse(byte[] raw) {
        raw = removeSignedRequestHmacBytes(raw);
        Validate.isTrue(raw.length == props().size());
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