package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.annotations.HistoryLogProps;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.response.currentStatus.AlarmStatusResponse;
import com.jwoglom.pumpx2.pump.messages.response.currentStatus.AlertStatusResponse;

/**
 * Written when the pump annunciates an active alert, alarm or CGM alert: in the same second as
 * the activation for most ids, then again at multiples of about 5 minutes for as long as the
 * notification stays unacknowledged.
 *
 * <p>For alerts and alarms, all 1489 records examined (six pumps, Mobi and t:slim X2) follow an
 * AlertActivated / AlarmActivated record with the same {@link #getCategory() category} and
 * {@link #getNotificationId() id} on the same pump, and none comes after that notification's
 * AlertAck / AlarmAck. CGM alerts ({@link AamCategory#CGM_ALERT}, seen only on the t:slim X2)
 * follow a CGM alert activation with the same id; after an acknowledgement they resume later
 * (60 minutes later for CGM alert 2, then every 5 minutes), consistent with a CGM alert repeat
 * setting. The 2022 t:slim X2 captures contain no records of this type.
 *
 * <p>The class name is provisional: Tandem's cloud export and the Mobi app's HistoryLogType enum
 * do not name this opcode. Bytes 11, 12-13, 18 and 19 are not understood and are exposed raw;
 * bytes 16-17 and 20-25 were zero in all 1680 records examined.
 * See https://github.com/jwoglom/pumpx2/issues/147.
 */
@HistoryLogProps(
    opCode = 434,
    displayName = "Alert/Alarm Annunciation",
    internalName = ""
)
public class AamAnnunciationHistoryLog extends HistoryLog {

    private int categoryId;
    private int unknown11;
    private int unknown12;
    private long notificationId;
    private int unknown18;
    private int unknown19;

    public AamAnnunciationHistoryLog() {}
    public AamAnnunciationHistoryLog(long pumpTimeSec, long sequenceNum, int categoryId, int unknown11, int unknown12, long notificationId, int unknown18, int unknown19) {
        super(pumpTimeSec, sequenceNum);
        this.cargo = buildCargo(pumpTimeSec, sequenceNum, categoryId, unknown11, unknown12, notificationId, unknown18, unknown19);
        this.categoryId = categoryId;
        this.unknown11 = unknown11;
        this.unknown12 = unknown12;
        this.notificationId = notificationId;
        this.unknown18 = unknown18;
        this.unknown19 = unknown19;
    }

    public AamAnnunciationHistoryLog(int categoryId, int unknown11, int unknown12, long notificationId, int unknown18, int unknown19) {
        this(0, 0, categoryId, unknown11, unknown12, notificationId, unknown18, unknown19);
    }

    public int typeId() {
        return 434;
    }

    public void parse(byte[] raw) {
        Validate.isTrue(raw.length == 26);
        this.cargo = raw;
        parseBase(raw);
        this.categoryId = raw[10] & 0xFF;
        this.unknown11 = raw[11] & 0xFF;
        this.unknown12 = Bytes.readShort(raw, 12);
        this.notificationId = Bytes.readUint32(raw, 14);
        this.unknown18 = raw[18] & 0xFF;
        this.unknown19 = raw[19] & 0xFF;
    }

    public static byte[] buildCargo(long pumpTimeSec, long sequenceNum, int categoryId, int unknown11, int unknown12, long notificationId, int unknown18, int unknown19) {
        return HistoryLog.fillCargo(Bytes.combine(
            HistoryLog.typeIdBytes(434, 0),
            Bytes.toUint32(pumpTimeSec),
            Bytes.toUint32(sequenceNum),
            new byte[]{ (byte) categoryId, (byte) unknown11 },
            Bytes.firstTwoBytesLittleEndian(unknown12),
            Bytes.toUint32(notificationId),
            new byte[]{ (byte) unknown18, (byte) unknown19 }));
    }

    /**
     * @return byte 10, the raw notification category (see {@link #getCategory()})
     */
    public int getCategoryId() {
        return categoryId;
    }

    /**
     * @return the notification category, or null for a value not observed yet
     */
    public AamCategory getCategory() {
        return AamCategory.fromId(categoryId);
    }

    /**
     * The id of the notification being annunciated, in the id space of its
     * {@link #getCategory() category}: {@link AlertStatusResponse.AlertResponseType} for alerts,
     * {@link AlarmStatusResponse.AlarmResponseType} for alarms, and the alertId of
     * {@link CgmAlertActivatedDexHistoryLog} for CGM alerts. Every id matched the activation
     * record it followed.
     *
     * <p>Id 0 is a real alert, LOW_INSULIN_ALERT, not an empty slot: it is frequent because the
     * low-insulin alert is re-annunciated every few minutes until it is acknowledged.
     *
     * @return the alert, alarm or CGM alert id (bytes 14-17)
     */
    public long getNotificationId() {
        return notificationId;
    }

    /**
     * @return the alert type when {@link #getCategory()} is {@link AamCategory#ALERT}, else null
     */
    public AlertStatusResponse.AlertResponseType getAlertResponseType() {
        if (getCategory() != AamCategory.ALERT) {
            return null;
        }
        return AlertStatusResponse.AlertResponseType.fromSingularId(notificationId);
    }

    /**
     * @return the alarm type when {@link #getCategory()} is {@link AamCategory#ALARM}, else null
     */
    public AlarmStatusResponse.AlarmResponseType getAlarmResponseType() {
        if (getCategory() != AamCategory.ALARM) {
            return null;
        }
        return AlarmStatusResponse.AlarmResponseType.fromSingularId(notificationId);
    }

    /**
     * Byte 11, meaning unknown. Together with {@link #getUnknown12()} and {@link #getUnknown18()}
     * it is constant for a given category and id across all 1680 records: 1 for alerts 0, 2, 3,
     * 17, 26 and 54; 2 for alerts 4, 11, 13, 14, 50 and 51; 3 for alarms; 0, 1 or 2 for CGM
     * alerts depending on the id.
     *
     * @return byte 11
     */
    public int getUnknown11() {
        return unknown11;
    }

    /**
     * Bytes 12-13, meaning unknown: 0 for alerts, 10 for alarms 3, 12 and 18 but 0 for alarm 23,
     * and 20, 30 or 40 for CGM alerts depending on the id.
     *
     * @return the uint16 at bytes 12-13
     */
    public int getUnknown12() {
        return unknown12;
    }

    /**
     * Byte 18, meaning unknown: 4 or 5 for alerts (following {@link #getUnknown11()}), 6 for
     * alarms, 8-14 for CGM alerts depending on the id.
     *
     * @return byte 18
     */
    public int getUnknown18() {
        return unknown18;
    }

    /**
     * Byte 19, meaning unknown. For alerts it matched the pump's alert annunciation setting
     * ({@code PumpGlobalsResponse.alertAnnun}) wherever one was polled near the record, on three
     * pumps; for alarms it did not track {@code alarmAnnun} on the t:slim X2, so it stays raw.
     *
     * @return byte 19
     */
    public int getUnknown19() {
        return unknown19;
    }

    /**
     * Values of byte 10, confirmed by matching every record's id against the activation record
     * of that category it followed.
     */
    public enum AamCategory {
        ALERT(3),
        ALARM(4),
        CGM_ALERT(6),
        ;

        private final int id;
        AamCategory(int id) {
            this.id = id;
        }
        public int id() {
            return id;
        }
        public static AamCategory fromId(int id) {
            for (AamCategory c : values()) {
                if (c.id == id) return c;
            }
            return null;
        }
    }
}
