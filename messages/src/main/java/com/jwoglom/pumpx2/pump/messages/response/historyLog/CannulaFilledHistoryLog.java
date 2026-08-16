package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.annotations.HistoryLogProps;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;

import java.math.BigInteger;

@HistoryLogProps(
    opCode = 61,
    displayName = "Cannula Filled",
    internalName = "LID_CANNULA_FILLED",
    usedByTidepool = true
)
public class CannulaFilledHistoryLog extends HistoryLog {

    private float primeSize;
    private long completionStatus;

    public CannulaFilledHistoryLog() {}
    public CannulaFilledHistoryLog(long pumpTimeSec, long sequenceNum, float primeSize, long completionStatus) {
        super(pumpTimeSec, sequenceNum);
        this.cargo = buildCargo(pumpTimeSec, sequenceNum, primeSize, completionStatus);
        this.primeSize = primeSize;
        this.completionStatus = completionStatus;

    }

    public CannulaFilledHistoryLog(float primeSize, long completionStatus) {
        this(0, 0, primeSize, completionStatus);
    }

    public int typeId() {
        return 61;
    }

    public void parse(byte[] raw) {
        Validate.isTrue(raw.length == 26);
        this.cargo = raw;
        parseBase(raw);
        this.primeSize = Bytes.readFloat(raw, 10);
        this.completionStatus = Bytes.readUint32(raw, 14);

    }

    public static byte[] buildCargo(long pumpTimeSec, long sequenceNum, float primeSize, long completionStatus) {
        return HistoryLog.fillCargo(Bytes.combine(
            HistoryLog.typeIdBytes(61, 0),
            Bytes.toUint32(pumpTimeSec),
            Bytes.toUint32(sequenceNum),
            Bytes.toFloat(primeSize),
            Bytes.toUint32(completionStatus)));
    }

    /**
     * @return the number of units used to prime the cannula
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
