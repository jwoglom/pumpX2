package com.jwoglom.pumpx2.pump.messages.response.currentStatus;

import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.response.historyLog.BolusDeliveryHistoryLog;

import java.time.Instant;
import java.util.Set;

/**
 * Contains everything shared between {@link LastBolusStatusResponse} and {@link LastBolusStatusV2Response}
 * except that the latter contains requestedVolume
 *
 * NOTE: Intentionally not a subclass of StatusMessage because status seems to be equal to 1 even
 * when partially valid data is returned about the last bolus.
 */
public abstract class LastBolusStatusAbstractResponse extends Message {
    public abstract int getStatus();
    public abstract int getBolusId();
    public abstract long getTimestamp();
    public abstract Instant getTimestampInstant();
    public abstract long getDeliveredVolume();
    public abstract int getBolusStatusId();
    /**
     * Bolus completion status. Ids 0-6 and their meanings match Tandem's own names for the
     * completionStatus field of history logs 20 (BolusCompleted) and 21 (BolexCompleted):
     * USER_ABORTED, TERMINATED_BY_ALARM, TERMINATED_BY_MALFUNCTION, COMPLETED,
     * ABORTED_VIA_WIRELESS_LINK, REJECTED_REQUEST_VIA_WIRELESS_LINK, ABORTED_BY_PLGS.
     *
     * <p>Seen in history logs so far: {@link #COMPLETE}, {@link #STOPPED_USER_TERMINATED} (also
     * written when the bolus is cancelled over Bluetooth with CancelBolusRequest) and
     * {@link #STOPPED_ALARM}. 2, 4, 5 and 6 have not been observed.
     */
    public enum BolusStatus {
        STOPPED_USER_TERMINATED(0),
        STOPPED_ALARM(1),
        STOPPED_MALFUNCTION(2),
        COMPLETE(3),
        STOPPED_WIRELESS(4),
        REJECTED_WIRELESS(5),
        TERMINATED_PLGS(6),
        ;

        private final int id;
        BolusStatus(int id) {
            this.id = id;
        }

        public int id() {
            return id;
        }

        public static BolusStatus fromId(int id) {
            for (BolusStatus s : values()) {
                if (s.id() == id) {
                    return s;
                }
            }
            return null;
        }
    }

    public BolusStatus getBolusStatus() {
        return BolusStatus.fromId(getBolusStatusId());
    }
    public abstract int getBolusSourceId();
    public BolusDeliveryHistoryLog.BolusSource getBolusSource() {
        return BolusDeliveryHistoryLog.BolusSource.fromId(getBolusSourceId());
    }
    public abstract int getBolusTypeBitmask();
    public Set<BolusDeliveryHistoryLog.BolusType> getBolusType() {
        return BolusDeliveryHistoryLog.BolusType.fromBitmask(getBolusTypeBitmask());
    }
    public abstract long getExtendedBolusDuration();
}
