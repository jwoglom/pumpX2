package com.jwoglom.pumpx2.pump.messages.request.control;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.bluetooth.Characteristic;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.models.BloodGlucoseReadingSource;
import com.jwoglom.pumpx2.pump.messages.models.BloodGlucoseReadingType;
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
    private boolean useForCgmCalibration;
    private BloodGlucoseReadingType entryType;
    private BloodGlucoseReadingSource source;
    private long pumpTime;
    private int bolusId;

    public RemoteBgEntryRequest() {}

    /**
     * Creates a request to add a data point of the current BG to the provided bolus ID (which must
     * be in progress; in a state between calling {@link BolusPermissionRequest} and {@link InitiateBolusRequest})
     * @param bg the blood glucose value in mg/dL
     * @param useForCgmCalibration if the BG should be used to calibrate the active G6 or G7 CGM sensor
     * @param entryType the type of BG reading (MANUAL or AUTO from CGM)
     * @param source the source of the BG reading (PUMP or REMOTE via BLE)
     * @param pumpTimeSecondsSinceBoot the output of {@link TimeSinceResetResponse#getPumpTimeSecondsSinceReset()}
     * @param bolusId the bolus ID returned from {@link BolusPermissionResponse#getBolusId()}
     */
    public RemoteBgEntryRequest(int bg, boolean useForCgmCalibration, BloodGlucoseReadingType entryType, BloodGlucoseReadingSource source, long pumpTimeSecondsSinceBoot, int bolusId) {
        this.cargo = buildCargo(bg, useForCgmCalibration, entryType, source, pumpTimeSecondsSinceBoot, bolusId);
        this.bg = bg;
        this.useForCgmCalibration = useForCgmCalibration;
        this.entryType = entryType;
        this.source = source;
        this.pumpTime = pumpTimeSecondsSinceBoot;
        this.bolusId = bolusId;
    }

    /**
     * @deprecated Use {@link #RemoteBgEntryRequest(int, boolean, BloodGlucoseReadingType, BloodGlucoseReadingSource, long, int)} instead.
     * This constructor hardcodes entryType to MANUAL and maps isAutopopBg to source (true=REMOTE, false=PUMP).
     */
    @Deprecated
    public RemoteBgEntryRequest(int bg, boolean useForCgmCalibration, boolean isAutopopBg, long pumpTimeSecondsSinceBoot, int bolusId) {
        this(bg, useForCgmCalibration,
             BloodGlucoseReadingType.MANUAL,
             isAutopopBg ? BloodGlucoseReadingSource.REMOTE : BloodGlucoseReadingSource.PUMP,
             pumpTimeSecondsSinceBoot, bolusId);
    }

    public void parse(byte[] raw) {
        raw = removeSignedRequestHmacBytes(raw);
        Validate.isTrue(raw.length == props().size());
        this.cargo = raw;
        this.bg = Bytes.readShort(raw, 0);
        this.useForCgmCalibration = (raw[2] == 1);
        this.entryType = BloodGlucoseReadingType.fromId(raw[3]);
        this.source = BloodGlucoseReadingSource.fromId(raw[4]);
        this.pumpTime = Bytes.readUint32(raw, 5);
        this.bolusId = Bytes.readShort(raw, 9);
    }


    public static byte[] buildCargo(int bg, boolean useForCgmCalibration, BloodGlucoseReadingType entryType, BloodGlucoseReadingSource source, long pumpTime, int bolusId) {
        return Bytes.combine(
            Bytes.firstTwoBytesLittleEndian(bg),
            new byte[]{(byte) (useForCgmCalibration ? 1 : 0)},
            new byte[]{(byte) entryType.id()},
            new byte[]{(byte) source.id()},
            Bytes.toUint32(pumpTime),
            Bytes.firstTwoBytesLittleEndian(bolusId)
        );
    }

    /**
     * @deprecated Use {@link #buildCargo(int, boolean, BloodGlucoseReadingType, BloodGlucoseReadingSource, long, int)} instead.
     */
    @Deprecated
    public static byte[] buildCargo(int bg, boolean useForCgmCalibration, boolean isAutopopBg, long pumpTime, int bolusId) {
        return buildCargo(bg, useForCgmCalibration,
            BloodGlucoseReadingType.MANUAL,
            isAutopopBg ? BloodGlucoseReadingSource.REMOTE : BloodGlucoseReadingSource.PUMP,
            pumpTime, bolusId);
    }

    public int getBG() {
        return bg;
    }

    public BloodGlucoseReadingType getEntryType() {
        return entryType;
    }

    public BloodGlucoseReadingSource getSource() {
        return source;
    }

    /**
     * @deprecated Use {@link #getSource()} instead. Returns true if source is REMOTE.
     */
    @Deprecated
    public boolean getIsAutopopBg() {
        return source == BloodGlucoseReadingSource.REMOTE;
    }

    /**
     * @deprecated Use {@link #getSource()} instead. Returns true if source is REMOTE.
     */
    @Deprecated
    public boolean isAutopopBg() {
        return source == BloodGlucoseReadingSource.REMOTE;
    }

    public boolean isUseForCgmCalibration() {
        return useForCgmCalibration;
    }

    public boolean getUseForCgmCalibration() {
        return useForCgmCalibration;
    }

    public long getPumpTime() {
        return pumpTime;
    }

    public int getBolusId() {
        return bolusId;
    }


}
