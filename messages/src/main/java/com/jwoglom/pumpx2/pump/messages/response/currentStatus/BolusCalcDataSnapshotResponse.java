package com.jwoglom.pumpx2.pump.messages.response.currentStatus;

import com.google.common.base.Preconditions;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.models.KnownApiVersion;
import com.jwoglom.pumpx2.pump.messages.request.currentStatus.BolusCalcDataSnapshotRequest;

import java.math.BigInteger;

@MessageProps(
    opCode=115,
    size=46,
    type=MessageType.RESPONSE,
    request=BolusCalcDataSnapshotRequest.class,
    minApi=KnownApiVersion.API_V2_5
)
public class BolusCalcDataSnapshotResponse extends Message {
    
    private boolean isUnacked;
    private int correctionFactor;
    private long iob;
    private int cartridgeRemainingInsulin;
    private int targetBg;
    private int isf;
    private boolean carbEntryEnabled;
    private long carbRatio;
    private int maxBolusAmount;
    private long maxBolusHourlyTotal;
    private boolean maxBolusEventsExceeded;
    private boolean maxIobEventsExceeded;
    private boolean isAutopopAllowed;
    
    public BolusCalcDataSnapshotResponse() {}
    
    public BolusCalcDataSnapshotResponse(boolean isUnacked, int correctionFactor, long iob, int cartridgeRemainingInsulin, int targetBg, int isf, boolean carbEntryEnabled, long carbRatio, int maxBolusAmount, long maxBolusHourlyTotal, boolean maxBolusEventsExceeded, boolean maxIobEventsExceeded, boolean isAutopopAllowed) {
        this(isUnacked, correctionFactor, iob, cartridgeRemainingInsulin, targetBg, isf, carbEntryEnabled, carbRatio, maxBolusAmount, maxBolusHourlyTotal, maxBolusEventsExceeded, maxIobEventsExceeded, isAutopopAllowed, new byte[11], new byte[8]);
    }

    public BolusCalcDataSnapshotResponse(boolean isUnacked, int correctionFactor, long iob, int cartridgeRemainingInsulin, int targetBg, int isf, boolean carbEntryEnabled, long carbRatio, int maxBolusAmount, long maxBolusHourlyTotal, boolean maxBolusEventsExceeded, boolean maxIobEventsExceeded, boolean isAutopopAllowed, byte[] unknown11bytes, byte[] unknown8bytes) {
        this.cargo = buildCargo(isUnacked, correctionFactor, iob, cartridgeRemainingInsulin, targetBg, isf, carbEntryEnabled, carbRatio, maxBolusAmount, maxBolusHourlyTotal, maxBolusEventsExceeded, maxIobEventsExceeded, isAutopopAllowed, unknown11bytes, unknown8bytes);
        this.isUnacked = isUnacked;
        this.correctionFactor = correctionFactor;
        this.iob = iob;
        this.cartridgeRemainingInsulin = cartridgeRemainingInsulin;
        this.targetBg = targetBg;
        this.isf = isf;
        this.carbEntryEnabled = carbEntryEnabled;
        this.carbRatio = carbRatio;
        this.maxBolusAmount = maxBolusAmount;
        this.maxBolusHourlyTotal = maxBolusHourlyTotal;
        this.maxBolusEventsExceeded = maxBolusEventsExceeded;
        this.maxIobEventsExceeded = maxIobEventsExceeded;
        this.isAutopopAllowed = isAutopopAllowed;
        
    }

    public void parse(byte[] raw) {
        Preconditions.checkArgument(raw.length == props().size());
        this.cargo = raw;
        this.isUnacked = raw[0] != 0;
        this.correctionFactor = Bytes.readShort(raw, 1);
        this.iob = Bytes.readUint32(raw, 3);
        this.cartridgeRemainingInsulin = Bytes.readShort(raw, 7);
        this.targetBg = Bytes.readShort(raw, 9);
        this.isf = Bytes.readShort(raw, 11);
        this.carbEntryEnabled = raw[13] != 0;
        this.carbRatio = Bytes.readUint32(raw, 14);
        this.maxBolusAmount = Bytes.readShort(raw, 18);
        this.maxBolusHourlyTotal = Bytes.readUint32(raw, 20);
        this.maxBolusEventsExceeded = raw[24] != 0;
        this.maxIobEventsExceeded = raw[25] != 0;
        this.isAutopopAllowed = raw[37] != 0;
        
    }

    
    public static byte[] buildCargo(boolean isUnacked, int correctionFactor, long iob, int cartridgeRemainingInsulin, int targetBg, int isf, boolean carbEntryEnabled, long carbRatio, int maxBolusAmount, long maxBolusHourlyTotal, boolean maxBolusEventsExceeded, boolean maxIobEventsExceeded, boolean isAutopopAllowed, byte[] unknown11bytes, byte[] unknown8bytes) {
        return Bytes.combine(
            new byte[]{ (byte) (isUnacked ? 1 : 0) }, 
            Bytes.firstTwoBytesLittleEndian(correctionFactor), 
            Bytes.toUint32(iob), 
            Bytes.firstTwoBytesLittleEndian(cartridgeRemainingInsulin), 
            Bytes.firstTwoBytesLittleEndian(targetBg), 
            Bytes.firstTwoBytesLittleEndian(isf), 
            new byte[]{ (byte) (carbEntryEnabled ? 1 : 0) }, 
            Bytes.toUint32(carbRatio), 
            Bytes.firstTwoBytesLittleEndian(maxBolusAmount), 
            Bytes.toUint32(maxBolusHourlyTotal), 
            new byte[]{ (byte) (maxBolusEventsExceeded ? 1 : 0) }, 
            new byte[]{ (byte) (maxIobEventsExceeded ? 1 : 0) },
            unknown11bytes, // this contains unknown values
            new byte[]{ (byte) (isAutopopAllowed ? 1 : 0) },
            unknown8bytes); // this contains unknown values
    }
    
    public boolean getIsUnacked() {
        return isUnacked;
    }
    public int getCorrectionFactor() {
        return correctionFactor;
    }
    public long getIob() {
        return iob;
    }
    public int getCartridgeRemainingInsulin() {
        return cartridgeRemainingInsulin;
    }
    public int getTargetBg() {
        return targetBg;
    }
    public int getIsf() {
        return isf;
    }
    public boolean getCarbEntryEnabled() {
        return carbEntryEnabled;
    }
    public long getCarbRatio() {
        return carbRatio;
    }
    public int getMaxBolusAmount() {
        return maxBolusAmount;
    }
    public long getMaxBolusHourlyTotal() {
        return maxBolusHourlyTotal;
    }
    public boolean getMaxBolusEventsExceeded() {
        return maxBolusEventsExceeded;
    }
    public boolean getMaxIobEventsExceeded() {
        return maxIobEventsExceeded;
    }
    public boolean getIsAutopopAllowed() {
        return isAutopopAllowed;
    }
    
}