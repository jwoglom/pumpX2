package com.jwoglom.pumpx2.pump.messages.response.currentStatus;

import org.apache.commons.lang3.Validate;
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
        Validate.isTrue(raw.length == props().size());
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

    /**
     * @return true if the data could not be retrieved successfully or is blank
     */
    public boolean getIsUnacked() {
        return isUnacked;
    }

    /**
     * @return the current BG from the CGM if present in mg/dL
     */
    public int getCorrectionFactor() {
        return correctionFactor;
    }

    /**
     * @return the insulin on board in 1000-units
     */
    public long getIob() {
        return iob;
    }

    /**
     * @return the units of insulin remaining in the cartridge
     */
    public int getCartridgeRemainingInsulin() {
        return cartridgeRemainingInsulin;
    }

    /**
     * @return the target BG from the pump profile
     */
    public int getTargetBg() {
        return targetBg;
    }

    /**
     * @return the insulin sensitivity factor, i.e. 1 unit : __ mg/dL correction factor
     */
    public int getIsf() {
        return isf;
    }

    /**
     * @return if carb entry is enabled in the pump profile settings
     */
    public boolean getCarbEntryEnabled() {
        return carbEntryEnabled;
    }

    /**
     * @return the ratio from 1 milliunit : __ grams, or 1 unit : (__ * 1000) grams. If the carb
     * ratio in the pump is 1u : 6 grams, then the long value returned is 6000
     */
    public long getCarbRatio() {
        return carbRatio;
    }

    /**
     * @return the maximum amount allowed in one bolus insulin delivery, in milliunits
     */
    public int getMaxBolusAmount() {
        return maxBolusAmount;
    }

    /**
     * @return the maximum amount allowed for boluses in a single hour, OR 0 if not configured
     */
    public long getMaxBolusHourlyTotal() {
        return maxBolusHourlyTotal;
    }

    /**
     * @return true if the maximum bolus has been exceeded for the interval
     */
    public boolean getMaxBolusEventsExceeded() {
        return maxBolusEventsExceeded;
    }

    /**
     * @return true if the maximum iob has been reached for the interval (i.e. max hourly bolus)
     */
    public boolean getMaxIobEventsExceeded() {
        return maxIobEventsExceeded;
    }

    /**
     * @return true if the {@link #getCorrectionFactor()} amount can be used to fill in the current
     * BG from the CGM to automatically add a correction to the insulin amount
     */
    public boolean getIsAutopopAllowed() {
        return isAutopopAllowed;
    }
    
}