package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.annotations.HistoryLogProps;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;

import java.util.Set;
import java.util.TreeSet;

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
        this.swUpdateStatus = Bytes.readShort(raw, 10);
        this.metadataAndVersionStatus = Bytes.readShort(raw, 12);
        this.fullDlAndCrcStatus = Bytes.readShort(raw, 14);
        this.fileDlAndSideloadStatus = Bytes.readShort(raw, 16);
        this.externalFlashStatus = Bytes.readShort(raw, 18);
        this.updateSuccessful = raw[20] & 0xFF;
        this.swPartNum = Bytes.readUint32(raw, 22);

    }

    public static byte[] buildCargo(long pumpTimeSec, long sequenceNum, int swUpdateStatus, int metadataAndVersionStatus, int fullDlAndCrcStatus, int fileDlAndSideloadStatus, int externalFlashStatus, int updateSuccessful, long swPartNum) {
        return HistoryLog.fillCargo(Bytes.combine(
            HistoryLog.typeIdBytes(203, 0),
            Bytes.toUint32(pumpTimeSec),
            Bytes.toUint32(sequenceNum),
            Bytes.firstTwoBytesLittleEndian(swUpdateStatus),
            Bytes.firstTwoBytesLittleEndian(metadataAndVersionStatus),
            Bytes.firstTwoBytesLittleEndian(fullDlAndCrcStatus),
            Bytes.firstTwoBytesLittleEndian(fileDlAndSideloadStatus),
            Bytes.firstTwoBytesLittleEndian(externalFlashStatus),
            new byte[]{(byte) updateSuccessful, 0},
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

    /**
     * @return raw updateSuccessful status; see {@link UpdateSuccessful} for known values
     */
    public int getUpdateSuccessful() {
        return updateSuccessful;
    }

    public UpdateSuccessful getUpdateSuccessfulEnum() {
        return UpdateSuccessful.fromId(updateSuccessful);
    }

    public long getSwPartNum() {
        return swPartNum;
    }

    public Set<SwUpdateStatus> getSwUpdateStatusSet() {
        return SwUpdateStatus.fromBitmask(swUpdateStatus);
    }
    public Set<MetadataAndVersionStatus> getMetadataAndVersionStatusSet() {
        return MetadataAndVersionStatus.fromBitmask(metadataAndVersionStatus);
    }
    public Set<FullDlAndCrcStatus> getFullDlAndCrcStatusSet() {
        return FullDlAndCrcStatus.fromBitmask(fullDlAndCrcStatus);
    }
    public Set<FileDlAndSideloadStatus> getFileDlAndSideloadStatusSet() {
        return FileDlAndSideloadStatus.fromBitmask(fileDlAndSideloadStatus);
    }

    public enum UpdateSuccessful {
        NOT_SUCCESSFUL(0),
        SUCCESSFUL(1),

        ;
        private final int id;
        UpdateSuccessful(int id) {
            this.id = id;
        }

        static UpdateSuccessful fromId(int id) {
            for (UpdateSuccessful r : values()) {
                if (r.id == id) {
                    return r;
                }
            }
            return null;
        }
    }

    /**
     * Bits 0-7: component update succeeded; bits 8-15: component update failed (Tandem schema SwUpdateStatus).
     */
    public enum SwUpdateStatus {
        ARM_UPDATE_SUCCESS(1),
        MSP_UPDATE_SUCCESS(2),
        RADIO_PROCESSOR_UPDATE_SUCCESS(4),
        ALPHAMASKS_UPDATE_SUCCESS(8),
        RASTER_UPDATE_SUCCESS(16),
        RADIO_APPLICATION_UPDATE_SUCCESS(32),
        RADIO_BOOTLOADER_UPDATE_SUCCESS(64),
        RADIO_STACK_UPDATE_SUCCESS(128),
        ARM_UPDATE_FAILED(256),
        MSP_UPDATE_FAILED(512),
        RADIO_PROCESSOR_UPDATE_FAILED(1024),
        ALPHAMASKS_UPDATE_FAILED(2048),
        RASTER_UPDATE_FAILED(4096),
        RADIO_APPLICATION_UPDATE_FAILED(8192),
        RADIO_BOOTLOADER_UPDATE_FAILED(16384),
        RADIO_STACK_UPDATE_FAILED(32768),
        ;

        private final int mask;
        SwUpdateStatus(int mask) {
            this.mask = mask;
        }

        public static Set<SwUpdateStatus> fromBitmask(int bitmask) {
            Set<SwUpdateStatus> items = new TreeSet<>();
            for (SwUpdateStatus i : values()) {
                if ((bitmask & i.mask) != 0) {
                    items.add(i);
                }
            }
            return items;
        }

        public int getMask() {
            return mask;
        }
    }

    /**
     * Bits 0-7: component metadata CRC failed; bits 8-15: component version check failed (Tandem schema MetadataAndVersionStatus).
     */
    public enum MetadataAndVersionStatus {
        ARM_METADATA_CRC_FAILED(1),
        MSP_METADATA_CRC_FAILED(2),
        RADIO_PROCESSOR_METADATA_CRC_FAILED(4),
        ALPHAMASKS_METADATA_CRC_FAILED(8),
        RASTER_METADATA_CRC_FAILED(16),
        RADIO_APPLICATION_METADATA_CRC_FAILED(32),
        RADIO_BOOTLOADER_METADATA_CRC_FAILED(64),
        RADIO_STACK_METADATA_CRC_FAILED(128),
        ARM_VERSION_FAILED(256),
        MSP_VERSION_FAILED(512),
        RADIO_PROCESSOR_VERSION_FAILED(1024),
        ALPHAMASKS_VERSION_FAILED(2048),
        RASTER_VERSION_FAILED(4096),
        RADIO_APPLICATION_VERSION_FAILED(8192),
        RADIO_BOOTLOADER_VERSION_FAILED(16384),
        RADIO_STACK_VERSION_FAILED(32768),
        ;

        private final int mask;
        MetadataAndVersionStatus(int mask) {
            this.mask = mask;
        }

        public static Set<MetadataAndVersionStatus> fromBitmask(int bitmask) {
            Set<MetadataAndVersionStatus> items = new TreeSet<>();
            for (MetadataAndVersionStatus i : values()) {
                if ((bitmask & i.mask) != 0) {
                    items.add(i);
                }
            }
            return items;
        }

        public int getMask() {
            return mask;
        }
    }

    /**
     * Bits 0-7: component full download failed; bits 8-15: component CRC failed (Tandem schema FullDlAndCrcStatus).
     */
    public enum FullDlAndCrcStatus {
        ARM_FULL_DOWNLOAD_FAILED(1),
        MSP_FULL_DOWNLOAD_FAILED(2),
        RADIO_PROCESSOR_FULL_DOWNLOAD_FAILED(4),
        ALPHAMASKS_FULL_DOWNLOAD_FAILED(8),
        RASTER_FULL_DOWNLOAD_FAILED(16),
        RADIO_APPLICATION_FULL_DOWNLOAD_FAILED(32),
        RADIO_BOOTLOADER_FULL_DOWNLOAD_FAILED(64),
        RADIO_STACK_FULL_DOWNLOAD_FAILED(128),
        ARM_CRC_FAILED(256),
        MSP_CRC_FAILED(512),
        RADIO_PROCESSOR_CRC_FAILED(1024),
        ALPHAMASKS_CRC_FAILED(2048),
        RASTER_CRC_FAILED(4096),
        RADIO_APPLICATION_CRC_FAILED(8192),
        RADIO_BOOTLOADER_CRC_FAILED(16384),
        RADIO_STACK_CRC_FAILED(32768),
        ;

        private final int mask;
        FullDlAndCrcStatus(int mask) {
            this.mask = mask;
        }

        public static Set<FullDlAndCrcStatus> fromBitmask(int bitmask) {
            Set<FullDlAndCrcStatus> items = new TreeSet<>();
            for (FullDlAndCrcStatus i : values()) {
                if ((bitmask & i.mask) != 0) {
                    items.add(i);
                }
            }
            return items;
        }

        public int getMask() {
            return mask;
        }
    }

    /**
     * Bits 0-7: component file downloaded; bits 8-15: component sideload started (Tandem schema FileDlAndSideloadStatus).
     */
    public enum FileDlAndSideloadStatus {
        ARM_FILE_DOWNLOADED(1),
        MSP_FILE_DOWNLOADED(2),
        RADIO_PROCESSOR_FILE_DOWNLOADED(4),
        ALPHAMASKS_FILE_DOWNLOADED(8),
        RASTER_FILE_DOWNLOADED(16),
        RADIO_APPLICATION_FILE_DOWNLOADED(32),
        RADIO_BOOTLOADER_FILE_DOWNLOADED(64),
        RADIO_STACK_FILE_DOWNLOADED(128),
        ARM_SIDELOAD_STARTED(256),
        MSP_SIDELOAD_STARTED(512),
        RADIO_PROCESSOR_SIDELOAD_STARTED(1024),
        ALPHAMASKS_SIDELOAD_STARTED(2048),
        RASTER_SIDELOAD_STARTED(4096),
        RADIO_APPLICATION_SIDELOAD_STARTED(8192),
        RADIO_BOOTLOADER_SIDELOAD_STARTED(16384),
        RADIO_STACK_SIDELOAD_STARTED(32768),
        ;

        private final int mask;
        FileDlAndSideloadStatus(int mask) {
            this.mask = mask;
        }

        public static Set<FileDlAndSideloadStatus> fromBitmask(int bitmask) {
            Set<FileDlAndSideloadStatus> items = new TreeSet<>();
            for (FileDlAndSideloadStatus i : values()) {
                if ((bitmask & i.mask) != 0) {
                    items.add(i);
                }
            }
            return items;
        }

        public int getMask() {
            return mask;
        }
    }
}
