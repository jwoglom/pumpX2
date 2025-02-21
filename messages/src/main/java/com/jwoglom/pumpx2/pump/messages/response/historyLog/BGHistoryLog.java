package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.annotations.HistoryLogProps;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.response.currentStatus.LastBGResponse;

@HistoryLogProps(
    opCode = 16,
    displayName = "BG Taken",
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
    private long spare;
    
    public BGHistoryLog() {}
    
    public BGHistoryLog(long pumpTimeSec, long sequenceNum, int bg, int cgmCalibration, int bgSourceId, float iob, int targetBG, int isf, long spare) {
        this.cargo = buildCargo(pumpTimeSec, sequenceNum, bg, cgmCalibration, bgSourceId, iob, targetBG, isf, spare);
        this.pumpTimeSec = pumpTimeSec;
        this.sequenceNum = sequenceNum;
        this.bg = bg;
        this.cgmCalibration = cgmCalibration;
        this.bgSourceId = bgSourceId;
        this.iob = iob;
        this.targetBG = targetBG;
        this.isf = isf;
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
        this.spare = Bytes.readUint32(raw, 22);
        
    }

    
    public static byte[] buildCargo(long pumpTimeSec, long sequenceNum, int bg, int cgmCalibration, int bgSource, float iob, int targetBG, int isf, long spare) {
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
            Bytes.toUint32(spare));
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

    /**
     * @return TODO(unknown): always 1?
     */
    public long getSpare() {
        return spare;
    }
    
}