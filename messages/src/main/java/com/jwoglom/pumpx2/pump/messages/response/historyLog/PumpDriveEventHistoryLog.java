package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.annotations.HistoryLogProps;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;

/**
 * Mobi drive-mechanism record, written at the start and end of each bolus, periodically during
 * longer ones, and during cartridge retraction and fills; not seen on t:slim X2. Tandem publishes
 * no name for opcode 277, so this class name is provisional. Only the drive counter is
 * understood; the other fields are exposed raw. See https://github.com/jwoglom/pumpX2/issues/144.
 */
@HistoryLogProps(
    opCode = 277,
    displayName = "Pump Drive Event"
)
public class PumpDriveEventHistoryLog extends HistoryLog {

    private long driveCounter;
    private int unknownI16At14;
    private int unknownI16At16;
    private int unknownU8At18;
    private int unknownU8At19;
    private int unknownU8At20;
    private int unknownU8At21;
    private int unknownU16At22;
    private int unknownU16At24;

    public PumpDriveEventHistoryLog() {}

    public PumpDriveEventHistoryLog(long pumpTimeSec, long sequenceNum, long driveCounter, int unknownI16At14, int unknownI16At16, int unknownU8At18, int unknownU8At19, int unknownU8At20, int unknownU8At21, int unknownU16At22, int unknownU16At24) {
        this(pumpTimeSec, sequenceNum, driveCounter, unknownI16At14, unknownI16At16, unknownU8At18, unknownU8At19, unknownU8At20, unknownU8At21, unknownU16At22, unknownU16At24, 0);
    }

    public PumpDriveEventHistoryLog(long pumpTimeSec, long sequenceNum, long driveCounter, int unknownI16At14, int unknownI16At16, int unknownU8At18, int unknownU8At19, int unknownU8At20, int unknownU8At21, int unknownU16At22, int unknownU16At24, int headerHighNibble) {
        super(pumpTimeSec, sequenceNum);
        this.cargo = buildCargo(pumpTimeSec, sequenceNum, driveCounter, unknownI16At14, unknownI16At16, unknownU8At18, unknownU8At19, unknownU8At20, unknownU8At21, unknownU16At22, unknownU16At24, headerHighNibble);
        this.driveCounter = driveCounter;
        this.unknownI16At14 = unknownI16At14;
        this.unknownI16At16 = unknownI16At16;
        this.unknownU8At18 = unknownU8At18;
        this.unknownU8At19 = unknownU8At19;
        this.unknownU8At20 = unknownU8At20;
        this.unknownU8At21 = unknownU8At21;
        this.unknownU16At22 = unknownU16At22;
        this.unknownU16At24 = unknownU16At24;
    }

    public int typeId() {
        return 277;
    }

    public void parse(byte[] raw) {
        Validate.isTrue(raw.length == 26);
        this.cargo = raw;
        parseBase(raw);
        this.driveCounter = Bytes.readUint32(raw, 10);
        this.unknownI16At14 = (short) Bytes.readShort(raw, 14);
        this.unknownI16At16 = (short) Bytes.readShort(raw, 16);
        this.unknownU8At18 = raw[18] & 0xFF;
        this.unknownU8At19 = raw[19] & 0xFF;
        this.unknownU8At20 = raw[20] & 0xFF;
        this.unknownU8At21 = raw[21] & 0xFF;
        this.unknownU16At22 = Bytes.readShort(raw, 22);
        this.unknownU16At24 = Bytes.readShort(raw, 24);
    }

    public static byte[] buildCargo(long pumpTimeSec, long sequenceNum, long driveCounter, int unknownI16At14, int unknownI16At16, int unknownU8At18, int unknownU8At19, int unknownU8At20, int unknownU8At21, int unknownU16At22, int unknownU16At24, int headerHighNibble) {
        return HistoryLog.fillCargo(Bytes.combine(
            HistoryLog.typeIdBytes(277, headerHighNibble),
            Bytes.toUint32(pumpTimeSec),
            Bytes.toUint32(sequenceNum),
            Bytes.toUint32(driveCounter),
            Bytes.firstTwoBytesLittleEndian(unknownI16At14),
            Bytes.firstTwoBytesLittleEndian(unknownI16At16),
            new byte[]{ (byte) unknownU8At18, (byte) unknownU8At19, (byte) unknownU8At20, (byte) unknownU8At21 },
            Bytes.firstTwoBytesLittleEndian(unknownU16At22),
            Bytes.firstTwoBytesLittleEndian(unknownU16At24)));
    }

    /**
     * @return the drive position counter. Between cartridge changes it only increases, by about
     * 1,073-1,080 counts per unit delivered (bolus plus basal), so one count is roughly 0.93 mU;
     * a bolus moves it by 54 counts per 0.05 U after the first 0.05 U. It runs backwards while the
     * cartridge is retracted and forwards during fills. The same counter appears in
     * PrimeInprocess (348) and opcodes 19 and 231.
     */
    public long getDriveCounter() {
        return driveCounter;
    }

    /**
     * @return bytes 14-15 as a signed int16. Meaning unknown: usually equal to bytes 16-17; at
     * most 60 during bolus and basal steps (54 is one 0.05 U step), about 300 during fills,
     * about -2000 during retraction.
     */
    public int getUnknownI16At14() {
        return unknownI16At14;
    }

    /**
     * @return bytes 16-17 as a signed int16. See {@link #getUnknownI16At14()}.
     */
    public int getUnknownI16At16() {
        return unknownI16At16;
    }

    /**
     * @return byte 18. Meaning unknown: 0x64, 0xC4 or 0x9C seen.
     */
    public int getUnknownU8At18() {
        return unknownU8At18;
    }

    /**
     * @return byte 19. Meaning unknown: 4 or 5, 0 in a few records.
     */
    public int getUnknownU8At19() {
        return unknownU8At19;
    }

    /**
     * @return byte 20. Meaning unknown: 2, or 12 in the last records of a retraction.
     */
    public int getUnknownU8At20() {
        return unknownU8At20;
    }

    /**
     * @return byte 21. Meaning unknown: 15, rarely 5.
     */
    public int getUnknownU8At21() {
        return unknownU8At21;
    }

    /**
     * @return bytes 22-23. Meaning unknown: 0xFFFF during most bolus and basal steps; roughly
     * 250-490 during fills and retraction.
     */
    public int getUnknownU16At22() {
        return unknownU16At22;
    }

    /**
     * @return bytes 24-25. Meaning unknown: small values (mostly 5-20 during bolus steps, up to
     * about 90 during retraction).
     */
    public int getUnknownU16At24() {
        return unknownU16At24;
    }
}
