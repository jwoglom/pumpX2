package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.annotations.HistoryLogProps;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;

/**
 * Version of the BLE SoftDevice (the Bluetooth stack on the pump's BLE processor), stored twice.
 * Bytes 10-17 and 18-25 each hold four uint16 values in the layout of bytes 0-7 of
 * {@link com.jwoglom.pumpx2.pump.messages.response.currentStatus.BleSoftwareInfoResponse}:
 * softDeviceId, major, minor and bugfix version. In all 77 records from seven pumps (t:slim X2 on
 * two firmware versions and Tandem Mobi 7.6 to 7.9.0.2, 2023-2026), both blocks are either
 * 132 / 7 / 2 / 0, the same values the pumps return in BleSoftwareInfoResponse, or all zero.
 * What distinguishes the two blocks is not known; they have always been equal.
 *
 * Written right after the last 449 record in the block that follows NewDay (at midnight, and when
 * the clock is set to a different day), and once on every restart, about 8-10 seconds after
 * ArmInit. When a Mobi restarts on a later calendar day than it was shut down, it writes the
 * NewDay block before the BLE processor has reported its versions: that 450 is all zero (see
 * {@link #isKnown()}), and a filled-in 450 follows 2-3 seconds later.
 *
 * The class name is provisional: opcode 450 is not in Tandem's cloud event schema or the Mobi
 * app's history log list.
 */
@HistoryLogProps(
    opCode = 450,
    displayName = "BLE SoftDevice Version"
)
public class BleSoftDeviceVersionHistoryLog extends HistoryLog {

    private int softDeviceId;
    private int softDeviceMajorVersion;
    private int softDeviceMinorVersion;
    private int softDeviceBugfixVersion;
    private int softDeviceId2;
    private int softDeviceMajorVersion2;
    private int softDeviceMinorVersion2;
    private int softDeviceBugfixVersion2;

    public BleSoftDeviceVersionHistoryLog() {}
    public BleSoftDeviceVersionHistoryLog(long pumpTimeSec, long sequenceNum, int softDeviceId, int softDeviceMajorVersion, int softDeviceMinorVersion, int softDeviceBugfixVersion, int softDeviceId2, int softDeviceMajorVersion2, int softDeviceMinorVersion2, int softDeviceBugfixVersion2) {
        super(pumpTimeSec, sequenceNum);
        this.cargo = buildCargo(pumpTimeSec, sequenceNum, softDeviceId, softDeviceMajorVersion, softDeviceMinorVersion, softDeviceBugfixVersion, softDeviceId2, softDeviceMajorVersion2, softDeviceMinorVersion2, softDeviceBugfixVersion2);
        this.softDeviceId = softDeviceId;
        this.softDeviceMajorVersion = softDeviceMajorVersion;
        this.softDeviceMinorVersion = softDeviceMinorVersion;
        this.softDeviceBugfixVersion = softDeviceBugfixVersion;
        this.softDeviceId2 = softDeviceId2;
        this.softDeviceMajorVersion2 = softDeviceMajorVersion2;
        this.softDeviceMinorVersion2 = softDeviceMinorVersion2;
        this.softDeviceBugfixVersion2 = softDeviceBugfixVersion2;
    }

    public int typeId() {
        return 450;
    }

    public void parse(byte[] raw) {
        Validate.isTrue(raw.length == 26);
        this.cargo = raw;
        parseBase(raw);
        this.softDeviceId = Bytes.readShort(raw, 10);
        this.softDeviceMajorVersion = Bytes.readShort(raw, 12);
        this.softDeviceMinorVersion = Bytes.readShort(raw, 14);
        this.softDeviceBugfixVersion = Bytes.readShort(raw, 16);
        this.softDeviceId2 = Bytes.readShort(raw, 18);
        this.softDeviceMajorVersion2 = Bytes.readShort(raw, 20);
        this.softDeviceMinorVersion2 = Bytes.readShort(raw, 22);
        this.softDeviceBugfixVersion2 = Bytes.readShort(raw, 24);
    }

    public static byte[] buildCargo(long pumpTimeSec, long sequenceNum, int softDeviceId, int softDeviceMajorVersion, int softDeviceMinorVersion, int softDeviceBugfixVersion, int softDeviceId2, int softDeviceMajorVersion2, int softDeviceMinorVersion2, int softDeviceBugfixVersion2) {
        return HistoryLog.fillCargo(Bytes.combine(
            HistoryLog.typeIdBytes(450, 0),
            Bytes.toUint32(pumpTimeSec),
            Bytes.toUint32(sequenceNum),
            Bytes.firstTwoBytesLittleEndian(softDeviceId),
            Bytes.firstTwoBytesLittleEndian(softDeviceMajorVersion),
            Bytes.firstTwoBytesLittleEndian(softDeviceMinorVersion),
            Bytes.firstTwoBytesLittleEndian(softDeviceBugfixVersion),
            Bytes.firstTwoBytesLittleEndian(softDeviceId2),
            Bytes.firstTwoBytesLittleEndian(softDeviceMajorVersion2),
            Bytes.firstTwoBytesLittleEndian(softDeviceMinorVersion2),
            Bytes.firstTwoBytesLittleEndian(softDeviceBugfixVersion2)));
    }

    /**
     * 132 in every non-zero record; equals BleSoftwareInfoResponse.softDeviceId.
     *
     * @return the SoftDevice identifier from the first block (uint16 at offset 10)
     */
    public int getSoftDeviceId() {
        return softDeviceId;
    }

    /**
     * @return the SoftDevice major version from the first block (uint16 at offset 12)
     */
    public int getSoftDeviceMajorVersion() {
        return softDeviceMajorVersion;
    }

    /**
     * @return the SoftDevice minor version from the first block (uint16 at offset 14)
     */
    public int getSoftDeviceMinorVersion() {
        return softDeviceMinorVersion;
    }

    /**
     * @return the SoftDevice bugfix version from the first block (uint16 at offset 16)
     */
    public int getSoftDeviceBugfixVersion() {
        return softDeviceBugfixVersion;
    }

    /**
     * Second block, same layout as the first; equal to it in every observed record. Its meaning
     * is unknown.
     *
     * @return the SoftDevice identifier from the second block (uint16 at offset 18)
     */
    public int getSoftDeviceId2() {
        return softDeviceId2;
    }

    /**
     * @return the SoftDevice major version from the second block (uint16 at offset 20)
     */
    public int getSoftDeviceMajorVersion2() {
        return softDeviceMajorVersion2;
    }

    /**
     * @return the SoftDevice minor version from the second block (uint16 at offset 22)
     */
    public int getSoftDeviceMinorVersion2() {
        return softDeviceMinorVersion2;
    }

    /**
     * @return the SoftDevice bugfix version from the second block (uint16 at offset 24)
     */
    public int getSoftDeviceBugfixVersion2() {
        return softDeviceBugfixVersion2;
    }

    /**
     * False for an all-zero record: a Mobi writes one when it records the NewDay block during a
     * restart, before the BLE processor has reported its versions. The filled-in record follows a
     * few seconds later. Treat an all-zero record as "not yet known", not as version 0.0.0.
     *
     * @return true if either block holds a non-zero value
     */
    public boolean isKnown() {
        return softDeviceId != 0 || softDeviceMajorVersion != 0 || softDeviceMinorVersion != 0 || softDeviceBugfixVersion != 0
            || softDeviceId2 != 0 || softDeviceMajorVersion2 != 0 || softDeviceMinorVersion2 != 0 || softDeviceBugfixVersion2 != 0;
    }

    /**
     * @return the first block's version as "major.minor.bugfix" (e.g. "7.2.0"), or null if the
     *         first block is all zero
     */
    public String getVersionString() {
        return versionString(softDeviceId, softDeviceMajorVersion, softDeviceMinorVersion, softDeviceBugfixVersion);
    }

    /**
     * @return the second block's version as "major.minor.bugfix" (e.g. "7.2.0"), or null if the
     *         second block is all zero
     */
    public String getVersionString2() {
        return versionString(softDeviceId2, softDeviceMajorVersion2, softDeviceMinorVersion2, softDeviceBugfixVersion2);
    }

    private static String versionString(int id, int major, int minor, int bugfix) {
        if (id == 0 && major == 0 && minor == 0 && bugfix == 0) {
            return null;
        }
        return major + "." + minor + "." + bugfix;
    }
}
