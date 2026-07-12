package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.annotations.HistoryLogProps;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;

import java.math.BigInteger;

@HistoryLogProps(
    opCode = 63,
    displayName = "Tubing Filled",
    internalName = "LID_TUBING_FILLED",
    usedByTidepool = true
)
public class TubingFilledHistoryLog extends HistoryLog {

    private float primeSize;
    private long completionStatus;
    private long position;

    public TubingFilledHistoryLog() {}
    public TubingFilledHistoryLog(long pumpTimeSec, long sequenceNum, float primeSize, long completionStatus, long position) {
        super(pumpTimeSec, sequenceNum);
        this.cargo = buildCargo(pumpTimeSec, sequenceNum, primeSize, completionStatus, position);
        this.primeSize = primeSize;
        this.completionStatus = completionStatus;
        this.position = position;

    }

    public TubingFilledHistoryLog(float primeSize, long completionStatus, long position) {
        this(0, 0, primeSize, completionStatus, position);
    }

    public int typeId() {
        return 63;
    }

    public void parse(byte[] raw) {
        Validate.isTrue(raw.length == 26);
        this.cargo = raw;
        parseBase(raw);
        this.primeSize = Bytes.readFloat(raw, 10);
        this.completionStatus = Bytes.readUint32(raw, 14);
        this.position = Bytes.readUint32(raw, 18);

    }

    public static byte[] buildCargo(long pumpTimeSec, long sequenceNum, float primeSize, long completionStatus, long position) {
        return HistoryLog.fillCargo(Bytes.combine(
            new byte[]{63, 0},
            Bytes.toUint32(pumpTimeSec),
            Bytes.toUint32(sequenceNum),
            Bytes.toFloat(primeSize),
            Bytes.toUint32(completionStatus),
            Bytes.toUint32(position)));
    }

    /**
     * @return the number of units used to prime the tubing
     */
    public float getPrimeSize() {
        return primeSize;
    }

    public long getCompletionStatusRaw() {
        return completionStatus;
    }
    public CompletionStatus getCompletionStatus() {
        return CompletionStatus.fromId((int) completionStatus);
    }

    /**
     * @return the position (counts) at which tubing fill completed
     */
    public long getPosition() {
        return position;
    }

    public enum CompletionStatus {
        USER_ABORTED(0),
        TERMINATED_BY_ALARM(1),
        TERMINATED_BY_MALFUNCTION(2),
        COMPLETED(3),

        ;
        private final int id;
        CompletionStatus(int id) {
            this.id = id;
        }

        static CompletionStatus fromId(int id) {
            for (CompletionStatus r : values()) {
                if (r.id == id) {
                    return r;
                }
            }
            return null;
        }
    }

}
