package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.annotations.HistoryLogProps;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.response.currentStatus.LastBGResponse;

@HistoryLogProps(
    opCode = 16,
    displayName = "BG Taken",
    internalName = "LID_BG_READING_TAKEN",
    usedByAndroid = true,
    usedByTidepool = true
)
public class BGHistoryLog extends HistoryLog {

    private int bg;
    private int cgmCalibration;
    private int bgSourceId;
    private float iob;
    private int targetBG;
    private int isf;
    private int selectedIOB;
    private int bgSourceType;
    private int spare;

    public BGHistoryLog() {}

    public BGHistoryLog(long pumpTimeSec, long sequenceNum, int bg, int cgmCalibration, int bgSourceId, float iob, int targetBG, int isf, int selectedIOB, int bgSourceType, int spare) {
        this.cargo = buildCargo(pumpTimeSec, sequenceNum, bg, cgmCalibration, bgSourceId, iob, targetBG, isf, selectedIOB, bgSourceType, spare);
        this.pumpTimeSec = pumpTimeSec;
        this.sequenceNum = sequenceNum;
        this.bg = bg;
        this.cgmCalibration = cgmCalibration;
        this.bgSourceId = bgSourceId;
        this.iob = iob;
        this.targetBG = targetBG;
        this.isf = isf;
        this.selectedIOB = selectedIOB;
        this.bgSourceType = bgSourceType;
        this.spare = spare;

    }

    public int typeId() {
        return 16;
    }

    public void parse(byte[] raw) {
        Validate.isTrue(raw.length == 26);
        parseBase(raw);
        this.cargo = raw;
        this.bg = Bytes.readShort(raw, 10);
        this.cgmCalibration = raw[12];
        this.bgSourceId = raw[13];
        this.iob = Bytes.readFloat(raw, 14);
        this.targetBG = Bytes.readShort(raw, 18);
        this.isf = Bytes.readShort(raw, 20);
        this.selectedIOB = raw[22];
        this.bgSourceType = raw[23];
        this.spare = Bytes.readShort(raw, 24);
        
    }

    
    public static byte[] buildCargo(long pumpTimeSec, long sequenceNum, int bg, int cgmCalibration, int bgSource, float iob, int targetBG, int isf, int selectedIOB, int bgSourceType, int spare) {
        return Bytes.combine(
            new byte[]{ 16, 0 },
            Bytes.toUint32(pumpTimeSec),
            Bytes.toUint32(sequenceNum),
            Bytes.firstTwoBytesLittleEndian(bg),
            new byte[]{ (byte) cgmCalibration },
            new byte[]{ (byte) bgSource },
            Bytes.toFloat(iob),
            Bytes.firstTwoBytesLittleEndian(targetBG),
            Bytes.firstTwoBytesLittleEndian(isf),
            new byte[]{ (byte) selectedIOB },
            new byte[]{ (byte) bgSourceType },
            Bytes.firstTwoBytesLittleEndian(spare));
    }

    /**
     * @return the BG entry in mg/dL
     */
    public int getBg() {
        return bg;
    }

    /**
     * @return TODO(unknown)
     */
    public int getCgmCalibration() {
        return cgmCalibration;
    }

    public int getBgSourceId() {
        return bgSourceId;
    }

    /**
     * @return the source of the BG entry (CGM or manual)
     */
    public LastBGResponse.BgSource getBgSource() {
        return LastBGResponse.BgSource.fromId(bgSourceId);
    }

    /**
     * @return the current insulin on board
     */
    public float getIob() {
        return iob;
    }

    /**
     * @return the target BG in the pump profile
     */
    public int getTargetBG() {
        return targetBG;
    }

    /**
     * @return the insulin sensitivity factor in the pump profile
     */
    public int getIsf() {
        return isf;
    }

    public int getSelectedIOB() {
        return selectedIOB;
    }

    public int getBgSourceType() {
        return bgSourceType;
    }

    public int getSpare() {
        return spare;
    }
    
}