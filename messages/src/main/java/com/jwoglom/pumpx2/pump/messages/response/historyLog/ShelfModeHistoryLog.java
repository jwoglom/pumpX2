package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.annotations.HistoryLogProps;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;

@HistoryLogProps(
    opCode = 53,
    displayName = "Shelf Mode",
    internalName = "LID_SHELF_MODE"
)
public class ShelfModeHistoryLog extends HistoryLog {

    private long msecSinceReset;
    private int lipoIbc;
    private int lipoAbc;
    private int lipoCurrent;
    private long lipoRemCap;
    private long lipoMv;

    public ShelfModeHistoryLog() {}
    public ShelfModeHistoryLog(long pumpTimeSec, long sequenceNum, long msecSinceReset, int lipoIbc, int lipoAbc, int lipoCurrent, long lipoRemCap, long lipoMv) {
        super(pumpTimeSec, sequenceNum);
        this.cargo = buildCargo(pumpTimeSec, sequenceNum, msecSinceReset, lipoIbc, lipoAbc, lipoCurrent, lipoRemCap, lipoMv);
        this.msecSinceReset = msecSinceReset;
        this.lipoIbc = lipoIbc;
        this.lipoAbc = lipoAbc;
        this.lipoCurrent = lipoCurrent;
        this.lipoRemCap = lipoRemCap;
        this.lipoMv = lipoMv;

    }

    public ShelfModeHistoryLog(long msecSinceReset, int lipoIbc, int lipoAbc, int lipoCurrent, long lipoRemCap, long lipoMv) {
        this(0, 0, msecSinceReset, lipoIbc, lipoAbc, lipoCurrent, lipoRemCap, lipoMv);
    }

    public int typeId() {
        return 53;
    }

    public void parse(byte[] raw) {
        Validate.isTrue(raw.length == 26);
        this.cargo = raw;
        parseBase(raw);
        this.msecSinceReset = Bytes.readUint32(raw, 10);
        // Layout per the Tandem Source event schema: LiPoCurrent int16 @14, LiPo_ABC u8 @16, LiPo_IBC u8 @17.
        this.lipoCurrent = (short) Bytes.readShort(raw, 14);
        this.lipoAbc = raw[16] & 0xFF;
        this.lipoIbc = raw[17] & 0xFF;
        this.lipoRemCap = Bytes.readUint32(raw, 18);
        this.lipoMv = Bytes.readUint32(raw, 22);

    }

    public static byte[] buildCargo(long pumpTimeSec, long sequenceNum, long msecSinceReset, int lipoIbc, int lipoAbc, int lipoCurrent, long lipoRemCap, long lipoMv) {
        return HistoryLog.fillCargo(Bytes.combine(
            HistoryLog.typeIdBytes(53, 0),
            Bytes.toUint32(pumpTimeSec),
            Bytes.toUint32(sequenceNum),
            Bytes.toUint32(msecSinceReset),
            Bytes.firstTwoBytesLittleEndian(lipoCurrent & 0xFFFF),
            new byte[]{(byte) lipoAbc},
            new byte[]{(byte) lipoIbc},
            Bytes.toUint32(lipoRemCap),
            Bytes.toUint32(lipoMv)));
    }

    public long getMsecSinceReset() {
        return msecSinceReset;
    }

    /**
     * @return battery internal capacity, in percent
     */
    public int getLipoIbc() {
        return lipoIbc;
    }

    /**
     * @return battery available capacity, in percent
     */
    public int getLipoAbc() {
        return lipoAbc;
    }

    /**
     * @return signed battery current, in mA
     */
    public int getLipoCurrent() {
        return lipoCurrent;
    }

    public long getLipoRemCap() {
        return lipoRemCap;
    }

    public long getLipoMv() {
        return lipoMv;
    }
}
