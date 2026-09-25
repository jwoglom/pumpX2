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
            HistoryLog.typeIdBytes(16, 0),
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
     * @return 1 if this BG was used to calibrate the CGM, 0 if not (Tandem's cloud schema; Tandem's
     * Mobi app reads it as a boolean). On the wire, each of 3 records with 1 was written in the same
     * second as a CGM calibration history log carrying the same BG, and none of 199 records with 0
     * was.
     */
    public int getCgmCalibration() {
        return cgmCalibration;
    }

    /**
     * @return whether this BG was used to calibrate the CGM; see {@link #getCgmCalibration()}
     */
    public boolean isUsedForCgmCalibration() {
        return cgmCalibration != 0;
    }

    public int getBgSourceId() {
        return bgSourceId;
    }

    /**
     * @return the source of the BG entry (CGM or manual). Tandem's cloud schema names this byte
     * {@code bgEntryType}: 0 = entered manually on the numpad, 1 = auto-populated from the
     * Dexcom EGV, which agrees with {@link LastBGResponse.BgSource#MANUAL}/{@link LastBGResponse.BgSource#CGM}.
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
     * @return the raw IOB-algorithm id; see {@link #getSelectedIOBType()}
     */
    public int getSelectedIOB() {
        return selectedIOB;
    }

    /**
     * @return the IOB algorithm in use when the BG was taken, or null if the raw value is not
     * recognized
     */
    public BolusRequestedMsg2HistoryLog.SelectedIOBType getSelectedIOBType() {
        return BolusRequestedMsg2HistoryLog.SelectedIOBType.fromId(selectedIOB);
    }

    /**
     * @return the raw BG source type id; see {@link #getBgSourceTypeEnum()}
     */
    public int getBgSourceType() {
        return bgSourceType;
    }

    /**
     * @return whether the BG was entered on the pump or remotely, or null if the raw value is
     * not recognized
     */
    public BgSourceType getBgSourceTypeEnum() {
        return BgSourceType.fromId(bgSourceType);
    }

    /**
     * Where the BG entry was made. Value names come from Tandem's cloud schema (tconnectsync
     * {@code bgSourceType}). In captures, BGs with {@link #REMOTE_ENTRY} preceded Bluetooth (app)
     * boluses (97 of the 98 that were followed by a bolus) and BGs with {@link #LOCAL_PUMP_ENTRY}
     * never did (0 of 81).
     */
    public enum BgSourceType {
        LOCAL_PUMP_ENTRY(0),
        REMOTE_ENTRY(1),
        ;

        private final int id;
        BgSourceType(int id) {
            this.id = id;
        }

        public int id() {
            return id;
        }

        public static BgSourceType fromId(int id) {
            for (BgSourceType b : values()) {
                if (b.id() == id) {
                    return b;
                }
            }
            return null;
        }
    }

    public int getSpare() {
        return spare;
    }
    
}