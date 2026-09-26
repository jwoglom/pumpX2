package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.annotations.HistoryLogProps;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;

/**
 * One slot of the pump's 64-bit software ID. The high word is the firmware version word also
 * found in {@link VersionsAHistoryLog#getArmSwVersion()}, {@link ArmInitHistoryLog#getVersion()}
 * and {@code PumpVersionResponse.armSwVer}. Printed as {@code %08x%08x} (high word first), the
 * two words are the 16-digit hex string the pump returns in {@code CommonSoftwareInfoResponse}.
 * That was checked on four Tandem Mobi firmware versions, 7.6.0.3 to 7.9.0.2. Both words
 * change together at a firmware update and are the same on every pump running that firmware, so
 * they identify the software, not the device.
 *
 * <p>Written in the block that follows every NewDay (right after {@link VersionsAHistoryLog}, one
 * record per slot), including the NewDay written after a time or date change. Also written at
 * boot, one record per slot as that slot's value becomes available. A boot that crosses midnight
 * first logs the NewDay block before the slots are filled, so those records have both words 0
 * (see {@link #isSoftwareIdKnown()}). Seen in 288 records from two t:slim X2 and five Mobi pumps.
 * t:slim X2 firmware older than the late-2023 update does not write it.
 *
 * <p>The class name is provisional: opcode 449 is not in Tandem's cloud event schema or the Mobi
 * app's history log list. Fields not named here have not been identified and are exposed raw.
 * See https://github.com/jwoglom/pumpx2/issues/152.
 */
@HistoryLogProps(
    opCode = 449,
    displayName = "Software ID"
)
public class SoftwareIdHistoryLog extends HistoryLog {

    private int slot;
    private int unknown11;
    private long armSwVersion;
    private long softwareIdLow;
    private long unknown22;

    public SoftwareIdHistoryLog() {}
    public SoftwareIdHistoryLog(long pumpTimeSec, long sequenceNum, int slot, int unknown11, long armSwVersion, long softwareIdLow, long unknown22) {
        super(pumpTimeSec, sequenceNum);
        this.cargo = buildCargo(pumpTimeSec, sequenceNum, slot, unknown11, armSwVersion, softwareIdLow, unknown22);
        this.slot = slot;
        this.unknown11 = unknown11;
        this.armSwVersion = armSwVersion;
        this.softwareIdLow = softwareIdLow;
        this.unknown22 = unknown22;
    }

    public SoftwareIdHistoryLog(long pumpTimeSec, long sequenceNum, int slot, long armSwVersion, long softwareIdLow) {
        this(pumpTimeSec, sequenceNum, slot, 0, armSwVersion, softwareIdLow, 0);
    }

    public int typeId() {
        return 449;
    }

    public void parse(byte[] raw) {
        Validate.isTrue(raw.length == 26);
        this.cargo = raw;
        parseBase(raw);
        this.slot = raw[10] & 0xFF;
        this.unknown11 = (raw[11] & 0xFF) | ((raw[12] & 0xFF) << 8) | ((raw[13] & 0xFF) << 16);
        this.armSwVersion = Bytes.readUint32(raw, 14);
        this.softwareIdLow = Bytes.readUint32(raw, 18);
        this.unknown22 = Bytes.readUint32(raw, 22);
    }

    public static byte[] buildCargo(long pumpTimeSec, long sequenceNum, int slot, int unknown11, long armSwVersion, long softwareIdLow, long unknown22) {
        return HistoryLog.fillCargo(Bytes.combine(
            HistoryLog.typeIdBytes(449, 0),
            Bytes.toUint32(pumpTimeSec),
            Bytes.toUint32(sequenceNum),
            new byte[]{
                (byte) slot,
                (byte) (unknown11 & 0xFF),
                (byte) ((unknown11 >> 8) & 0xFF),
                (byte) ((unknown11 >> 16) & 0xFF)
            },
            Bytes.toUint32(armSwVersion),
            Bytes.toUint32(softwareIdLow),
            Bytes.toUint32(unknown22)));
    }

    /**
     * Raw slot index. Seen values are 0-3 on the Tandem Mobi and 0 and 2 on the t:slim X2. The
     * daily block writes them in the order 0, 2, 1, 3 (Mobi) or 0, 2 (X2). At a Mobi boot, slots 0
     * and 1 are written first and slots 2 and 3 about 2-3 seconds later, together with opcode 450.
     * Every filled slot has carried the same ID so far, so what each slot stands for is unknown.
     *
     * @return raw uint8 at offset 10
     */
    public int getSlot() {
        return slot;
    }

    /**
     * Always 0 so far. May be the high bytes of a 32-bit slot index.
     *
     * @return raw uint24 (little endian) at offsets 11-13
     */
    public int getUnknown11() {
        return unknown11;
    }

    /**
     * High word of the software ID. Equal to {@link VersionsAHistoryLog#getArmSwVersion()} of the
     * same pump and firmware. 0 in a slot that has not been filled yet.
     *
     * @return uint32 at offset 14
     */
    public long getArmSwVersion() {
        return armSwVersion;
    }

    /**
     * Low word of the software ID. One value per firmware version. Not
     * {@link VersionInfoHistoryLog#getArmCrc()} and not a part number. What it is derived from is
     * unknown. 0 in a slot that has not been filled yet.
     *
     * @return uint32 at offset 18
     */
    public long getSoftwareIdLow() {
        return softwareIdLow;
    }

    /**
     * Always 0 so far.
     *
     * @return raw uint32 at offset 22
     */
    public long getUnknown22() {
        return unknown22;
    }

    /**
     * @return false if both ID words are 0, meaning this slot was logged before its value was known
     */
    public boolean isSoftwareIdKnown() {
        return armSwVersion != 0 || softwareIdLow != 0;
    }

    /**
     * @return the software ID as 16 lowercase hex digits, high word first, as in
     *         {@code CommonSoftwareInfoResponse}; an empty string if {@link #isSoftwareIdKnown()} is false
     */
    public String getSoftwareId() {
        if (!isSoftwareIdKnown()) {
            return "";
        }
        return String.format("%08x%08x", armSwVersion, softwareIdLow);
    }
}
