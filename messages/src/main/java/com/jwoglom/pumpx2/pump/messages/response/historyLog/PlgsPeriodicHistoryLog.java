package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.annotations.HistoryLogProps;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import java.util.Set;
import java.util.TreeSet;

/**
 * Basal-IQ (predictive low glucose suspend) periodic record.
 */
@HistoryLogProps(
    opCode = 140,
    displayName = "PLGS Periodic",
    internalName = "LID_PLGS_PERIODIC"
)
public class PlgsPeriodicHistoryLog extends HistoryLog {

    private long timestamp;
    private int fmr;
    private int pgv;
    private int fmrStatus;
    private boolean pgvValid;
    private int ruleState;
    private int hoMinState;
    private long status;

    public PlgsPeriodicHistoryLog() {}
    public PlgsPeriodicHistoryLog(long pumpTimeSec, long sequenceNum) {
        this(pumpTimeSec, sequenceNum, 0, 0, 0, 0, false, 0, 0, 0);
    }

    public PlgsPeriodicHistoryLog(long pumpTimeSec, long sequenceNum, long timestamp, int fmr, int pgv, int fmrStatus, boolean pgvValid, int ruleState, int hoMinState, long status) {
        super(pumpTimeSec, sequenceNum);
        this.cargo = buildCargo(pumpTimeSec, sequenceNum, timestamp, fmr, pgv, fmrStatus, pgvValid, ruleState, hoMinState, status);
        parse(cargo);
    }

    public PlgsPeriodicHistoryLog(long timestamp, int fmr, int pgv, int fmrStatus, boolean pgvValid, int ruleState, int hoMinState, long status) {
        this(0, 0, timestamp, fmr, pgv, fmrStatus, pgvValid, ruleState, hoMinState, status);
    }

    public int typeId() {
        return 140;
    }

    public void parse(byte[] raw) {
        Validate.isTrue(raw.length == 26);
        this.cargo = raw;
        parseBase(raw);
        this.timestamp = Bytes.readUint32(raw, 10);
        this.fmr = Bytes.readShort(raw, 14);
        this.pgv = Bytes.readShort(raw, 16);
        this.fmrStatus = raw[18] & 0xFF;
        this.pgvValid = raw[19] != 0;
        this.ruleState = raw[20] & 0xFF;
        this.hoMinState = raw[21] & 0xFF;
        this.status = Bytes.readUint32(raw, 22);
    }

    public static byte[] buildCargo(long pumpTimeSec, long sequenceNum) {
        return buildCargo(pumpTimeSec, sequenceNum, 0, 0, 0, 0, false, 0, 0, 0);
    }

    public static byte[] buildCargo(long pumpTimeSec, long sequenceNum, long timestamp, int fmr, int pgv, int fmrStatus, boolean pgvValid, int ruleState, int hoMinState, long status) {
        return HistoryLog.fillCargo(Bytes.combine(
            HistoryLog.typeIdBytes(140, 0),
            Bytes.toUint32(pumpTimeSec),
            Bytes.toUint32(sequenceNum),
            Bytes.toUint32(timestamp),
            Bytes.firstTwoBytesLittleEndian(fmr),
            Bytes.firstTwoBytesLittleEndian(pgv),
            new byte[]{(byte) fmrStatus},
            new byte[]{(byte) (pgvValid ? 1 : 0)},
            new byte[]{(byte) ruleState},
            new byte[]{(byte) hoMinState},
            Bytes.toUint32(status)));
    }

    /**
     * @return timestamp, in seconds
     */
    public long getTimestamp() {
        return timestamp;
    }
    /**
     * @return five minute reading, in mg/dL
     */
    public int getFmr() {
        return fmr;
    }
    /**
     * @return predicted glucose value, in mg/dL
     */
    public int getPgv() {
        return pgv;
    }
    /**
     * @return raw FMR status, see {@link FmrStatus}
     */
    public int getFmrStatus() {
        return fmrStatus;
    }
    public FmrStatus getFmrStatusEnum() {
        return FmrStatus.fromId(fmrStatus);
    }
    /**
     * @return whether the predicted glucose value is valid
     */
    public boolean isPgvValid() {
        return pgvValid;
    }
    /**
     * @return raw rule state bitmask, see {@link RuleState}
     */
    public int getRuleState() {
        return ruleState;
    }
    public Set<RuleState> getRuleStateSet() {
        return RuleState.fromId(ruleState);
    }
    /**
     * @return raw hypo minimizer state, see {@link HoMinState}
     */
    public int getHoMinState() {
        return hoMinState;
    }
    public HoMinState getHoMinStateEnum() {
        return HoMinState.fromId(hoMinState);
    }
    /**
     * @return raw status bitmask, see {@link Status}
     */
    public long getStatus() {
        return status;
    }
    public Set<Status> getStatusSet() {
        return Status.fromId(status);
    }

    public enum FmrStatus {
        NO_FMR(0),
        PERIODIC_GLUCOSE_READING(1),
        CALIBRATION_RESPONSE_GLUCOSE_READING(2),
        SENSOR_SESSION_STOPPED(3),
        SENSOR_SESSION_STARTED(4),
        CALIBRATION_ENTERED(5),
        PUMPING_EVENT(6)

        ;
        private final int id;
        FmrStatus(int id) {
            this.id = id;
        }

        public static FmrStatus fromId(int id) {
            for (FmrStatus r : values()) {
                if (r.id == id) {
                    return r;
                }
            }
            return null;
        }

        public int getId() {
            return id;
        }
    }

    public enum RuleState {
        HO_SUSPEND_RULE(1),
        HO_RECOVERY_RULE(2),
        HO_UNAVAILABLE_RULE(4)

        ;
        private final int id;
        RuleState(int id) {
            this.id = id;
        }

        public static Set<RuleState> fromId(int mask) {
            Set<RuleState> items = new TreeSet<>();
            for (RuleState i : values()) {
                if ((mask & i.getId()) != 0) {
                    items.add(i);
                }
            }
            return items;
        }

        public int getId() {
            return id;
        }
    }

    public enum HoMinState {
        ON_AND_AVAILABLE(0),
        ON_AND_SUSPENDED(1),
        OFF(2),
        ON_AND_NOT_AVAILABLE(3)

        ;
        private final int id;
        HoMinState(int id) {
            this.id = id;
        }

        public static HoMinState fromId(int id) {
            for (HoMinState r : values()) {
                if (r.id == id) {
                    return r;
                }
            }
            return null;
        }

        public int getId() {
            return id;
        }
    }
}
