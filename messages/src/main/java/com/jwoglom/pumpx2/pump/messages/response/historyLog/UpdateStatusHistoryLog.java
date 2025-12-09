package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.annotations.HistoryLogProps;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;

@HistoryLogProps(
    opCode = 203,
    displayName = "Update Status",
    internalName = "LID_UPDATE_STATUS"
)
public class UpdateStatusHistoryLog extends HistoryLog {

    private int swUpdateStatus;
    private int metadataAndVersionStatus;
    private int fullDlAndCrcStatus;
    private int fileDlAndSideloadStatus;
    private int externalFlashStatus;
    private int updateSuccessful;
    private long swPartNum;

    public UpdateStatusHistoryLog() {}
    public UpdateStatusHistoryLog(long pumpTimeSec, long sequenceNum, int swUpdateStatus, int metadataAndVersionStatus, int fullDlAndCrcStatus, int fileDlAndSideloadStatus, int externalFlashStatus, int updateSuccessful, long swPartNum) {
        super(pumpTimeSec, sequenceNum);
        this.cargo = buildCargo(pumpTimeSec, sequenceNum, swUpdateStatus, metadataAndVersionStatus, fullDlAndCrcStatus, fileDlAndSideloadStatus, externalFlashStatus, updateSuccessful, swPartNum);
        this.swUpdateStatus = swUpdateStatus;
        this.metadataAndVersionStatus = metadataAndVersionStatus;
        this.fullDlAndCrcStatus = fullDlAndCrcStatus;
        this.fileDlAndSideloadStatus = fileDlAndSideloadStatus;
        this.externalFlashStatus = externalFlashStatus;
        this.updateSuccessful = updateSuccessful;
        this.swPartNum = swPartNum;

    }

    public UpdateStatusHistoryLog(int swUpdateStatus, int metadataAndVersionStatus, int fullDlAndCrcStatus, int fileDlAndSideloadStatus, int externalFlashStatus, int updateSuccessful, long swPartNum) {
        this(0, 0, swUpdateStatus, metadataAndVersionStatus, fullDlAndCrcStatus, fileDlAndSideloadStatus, externalFlashStatus, updateSuccessful, swPartNum);
    }

    public int typeId() {
        return 203;
    }

    public void parse(byte[] raw) {
        Validate.isTrue(raw.length == 26);
        this.cargo = raw;
        parseBase(raw);
        this.metadataAndVersionStatus = Bytes.readShort(raw, 10);
        this.swUpdateStatus = Bytes.readShort(raw, 12);
        this.fileDlAndSideloadStatus = Bytes.readShort(raw, 14);
        this.fullDlAndCrcStatus = Bytes.readShort(raw, 16);
        this.updateSuccessful = raw[19];
        this.externalFlashStatus = Bytes.readShort(raw, 20);
        this.swPartNum = Bytes.readUint32(raw, 22);

    }

    public static byte[] buildCargo(long pumpTimeSec, long sequenceNum, int swUpdateStatus, int metadataAndVersionStatus, int fullDlAndCrcStatus, int fileDlAndSideloadStatus, int externalFlashStatus, int updateSuccessful, long swPartNum) {
        return HistoryLog.fillCargo(Bytes.combine(
            new byte[]{(byte) 203, 0},
            Bytes.toUint32(pumpTimeSec),
            Bytes.toUint32(sequenceNum),
            Bytes.firstTwoBytesLittleEndian(metadataAndVersionStatus),
            Bytes.firstTwoBytesLittleEndian(swUpdateStatus),
            Bytes.firstTwoBytesLittleEndian(fileDlAndSideloadStatus),
            Bytes.firstTwoBytesLittleEndian(fullDlAndCrcStatus),
            new byte[]{0, (byte) updateSuccessful},
            Bytes.firstTwoBytesLittleEndian(externalFlashStatus),
            Bytes.toUint32(swPartNum)));
    }

    public int getSwUpdateStatus() {
        return swUpdateStatus;
    }

    public int getMetadataAndVersionStatus() {
        return metadataAndVersionStatus;
    }

    public int getFullDlAndCrcStatus() {
        return fullDlAndCrcStatus;
    }

    public int getFileDlAndSideloadStatus() {
        return fileDlAndSideloadStatus;
    }

    public int getExternalFlashStatus() {
        return externalFlashStatus;
    }

    public int getUpdateSuccessful() {
        return updateSuccessful;
    }

    public long getSwPartNum() {
        return swPartNum;
    }
}
