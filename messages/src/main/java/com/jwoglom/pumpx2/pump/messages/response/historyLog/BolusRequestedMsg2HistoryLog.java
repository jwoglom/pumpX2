package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.annotations.HistoryLogProps;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;

@HistoryLogProps(
    opCode = 65,
    displayName = "Bolus Requested 2/3",
    internalName = "LID_BOLUS_REQUESTED_MSG2",
    usedByAndroid = true,
    usedByTidepool = true
)
public class BolusRequestedMsg2HistoryLog extends HistoryLog {
    
    private int bolusId;
    private int options;
    private int standardPercent;
    private int duration;
    private int spare1;
    private int isf;
    private int targetBG;
    private boolean userOverride;
    private boolean declinedCorrection;
    private int selectedIOB;
    private int spare2;
    
    public BolusRequestedMsg2HistoryLog() {}
    
    public BolusRequestedMsg2HistoryLog(long pumpTimeSec, long sequenceNum, int bolusId, int options, int standardPercent, int duration, int spare1, int isf, int targetBG, boolean userOverride, boolean declinedCorrection, int selectedIOB, int spare2) {
        this(pumpTimeSec, sequenceNum, bolusId, options, standardPercent, duration, spare1, isf, targetBG, userOverride, declinedCorrection, selectedIOB, spare2, 0);
    }

    public BolusRequestedMsg2HistoryLog(long pumpTimeSec, long sequenceNum, int bolusId, int options, int standardPercent, int duration, int spare1, int isf, int targetBG, boolean userOverride, boolean declinedCorrection, int selectedIOB, int spare2, int headerHighNibble) {
        this.cargo = buildCargo(pumpTimeSec, sequenceNum, bolusId, options, standardPercent, duration, spare1, isf, targetBG, userOverride, declinedCorrection, selectedIOB, spare2, headerHighNibble);
        this.pumpTimeSec = pumpTimeSec;
        this.sequenceNum = sequenceNum;
        this.bolusId = bolusId;
        this.options = options;
        this.standardPercent = standardPercent;
        this.duration = duration;
        this.spare1 = spare1;
        this.isf = isf;
        this.targetBG = targetBG;
        this.userOverride = userOverride;
        this.declinedCorrection = declinedCorrection;
        this.selectedIOB = selectedIOB;
        this.spare2 = spare2;
    }

    public BolusRequestedMsg2HistoryLog(int bolusId, int options, int standardPercent, int duration, int spare1, int isf, int targetBG, boolean userOverride, boolean declinedCorrection, int selectedIOB, int spare2) {
        this(0, 0, bolusId, options, standardPercent, duration, spare1, isf, targetBG, userOverride, declinedCorrection, selectedIOB, spare2);
    }

    public int typeId() {
        return 65;
    }

    public void parse(byte[] raw) {
        Validate.isTrue(raw.length == 26);
        this.cargo = raw;
        parseBase(raw);
        this.bolusId = Bytes.readShort(raw, 10);
        this.options = raw[12];
        this.standardPercent = raw[13];
        this.duration = Bytes.readShort(raw, 14);
        this.spare1 = Bytes.readShort(raw, 16);
        this.isf = Bytes.readShort(raw, 18);
        this.targetBG = Bytes.readShort(raw, 20);
        this.userOverride = raw[22] != 0;
        this.declinedCorrection = raw[23] != 0;
        this.selectedIOB = raw[24];
        this.spare2 = raw[25];
        
    }

    
    public static byte[] buildCargo(long pumpTimeSec, long sequenceNum, int bolusId, int options, int standardPercent, int duration, int spare1, int isf, int targetBG, boolean userOverride, boolean declinedCorrection, int selectedIOB, int spare2) {
        return buildCargo(pumpTimeSec, sequenceNum, bolusId, options, standardPercent, duration, spare1, isf, targetBG, userOverride, declinedCorrection, selectedIOB, spare2, 0);
    }

    public static byte[] buildCargo(long pumpTimeSec, long sequenceNum, int bolusId, int options, int standardPercent, int duration, int spare1, int isf, int targetBG, boolean userOverride, boolean declinedCorrection, int selectedIOB, int spare2, int headerHighNibble) {
        return Bytes.combine(
            HistoryLog.typeIdBytes(65, headerHighNibble),
            Bytes.toUint32(pumpTimeSec),
            Bytes.toUint32(sequenceNum),
            Bytes.firstTwoBytesLittleEndian(bolusId), 
            new byte[]{ (byte) options }, 
            new byte[]{ (byte) standardPercent }, 
            Bytes.firstTwoBytesLittleEndian(duration), 
            Bytes.firstTwoBytesLittleEndian(spare1), 
            Bytes.firstTwoBytesLittleEndian(isf), 
            Bytes.firstTwoBytesLittleEndian(targetBG), 
            new byte[]{ (byte) (userOverride ? 1 : 0) }, 
            new byte[]{ (byte) (declinedCorrection ? 1 : 0) }, 
            new byte[]{ (byte) selectedIOB }, 
            new byte[]{ (byte) spare2 });
    }

    /**
     * @return bolus ID
     */
    public int getBolusId() {
        return bolusId;
    }
    /**
     * @return the raw value of the options byte, see {@link #getBolusOption()}
     */
    public int getOptions() {
        return options;
    }

    /**
     * @return how the bolus was requested, or null if the raw value is not recognized
     */
    public BolusOption getBolusOption() {
        return BolusOption.fromId(options);
    }

    /**
     * How the bolus was requested.
     *
     * <p><b>Value names ported from the tconnectsync Python implementation and not verified
     * here.</b> No record committed to this repository exercises them. Confirm against a capture
     * before relying on these labels.
     */
    public enum BolusOption {
        STANDARD(0),
        EXTENDED(1),
        QUICK(2),
        AUTOMATIC(3),
        BLE_STANDARD(4),
        BLE_EXTENDED(5),
        EATING_SOON_AUTOMATIC(6),
        LATE_BOLUS(7),
        ;

        private final int id;
        BolusOption(int id) {
            this.id = id;
        }

        public int id() {
            return id;
        }

        public static BolusOption fromId(int id) {
            for (BolusOption o : values()) {
                if (o.id() == id) {
                    return o;
                }
            }
            return null;
        }
    }

    /**
     * @return 100 if a standard bolus, otherwise the percent of the bolus which is performed up
     * front in an extended bolus
     */
    public int getStandardPercent() {
        return standardPercent;
    }

    /**
     * @return 0 if a standard bolus, otherwise the duration between the standard and extended bolus
     */
    public int getDuration() {
        return duration;
    }

    public int getSpare1() {
        return spare1;
    }

    /**
     * @return insulin sensitivity factor in the insulin delivery profile
     */
    public int getIsf() {
        return isf;
    }

    /**
     * @return target BG in mg/dL in the insulin delivery profile
     */
    public int getTargetBG() {
        return targetBG;
    }

    /**
     * @return true if the user overrode the insulin delivery amount (i.e., not calculated just
     * from the combination of carbs + BG correction)
     */
    public boolean getUserOverride() {
        return userOverride;
    }

    /**
     * @return true if the correction amount due to the current BG+IOB was declined
     */
    public boolean getDeclinedCorrection() {
        return declinedCorrection;
    }

    /**
     * @return the raw value identifying which IOB algorithm the bolus calculator used,
     * see {@link #getSelectedIOBType()}
     */
    public int getSelectedIOB() {
        return selectedIOB;
    }

    /**
     * @return the IOB algorithm the bolus calculator used, or null if the raw value is not
     * recognized
     */
    public SelectedIOBType getSelectedIOBType() {
        return SelectedIOBType.fromId(selectedIOB);
    }

    /**
     * The IOB algorithm used when calculating the bolus.
     *
     * <p><b>Value names ported from the tconnectsync Python implementation and not verified
     * here.</b> The field previously carried a {@code TODO(unknown)} javadoc; these names replace
     * that with a secondhand attribution, not with a confirmed one.
     */
    public enum SelectedIOBType {
        MUDALIAR_IOB(0),
        SWAN_IOB_MEAL(1),
        ;

        private final int id;
        SelectedIOBType(int id) {
            this.id = id;
        }

        public int id() {
            return id;
        }

        public static SelectedIOBType fromId(int id) {
            for (SelectedIOBType s : values()) {
                if (s.id() == id) {
                    return s;
                }
            }
            return null;
        }
    }
    public int getSpare2() {
        return spare2;
    }
    
}