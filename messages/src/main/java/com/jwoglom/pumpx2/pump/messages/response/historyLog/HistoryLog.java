package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.annotations.HistoryLogProps;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.helpers.Dates;
import com.jwoglom.pumpx2.shared.JavaHelpers;

import java.time.Instant;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public abstract class HistoryLog {
    protected byte[] cargo = null;

    public HistoryLog() {}

    HistoryLog(long pumpTimeSec, long sequenceNum) {
        this.pumpTimeSec = pumpTimeSec;
        this.sequenceNum = sequenceNum;
    }

    public abstract int typeId();

    long pumpTimeSec = -1;
    public long getPumpTimeSec() {
        return pumpTimeSec;
    }

    public Instant getPumpTimeSecInstant() {
        return Dates.fromJan12008EpochSecondsToDate(getPumpTimeSec());
    }

    long sequenceNum = -1;
    public long getSequenceNum() {
        return sequenceNum;
    }

    public abstract void parse(byte[] raw);

    /**
     * Parses the typeId (checked against the superclass static typeId),
     * pump time in seconds epoch and sequence number.
     */
    void parseBase(byte[] raw) {
        int typeId = Bytes.readShort(raw, 0) & 4095;
        // skipping due to inconsistencies around signed v unsigned
        // Validate.isTrue(typeId == typeId(), "found typeId " + typeId + " in message but expected " + typeId());

        this.pumpTimeSec = Bytes.readUint32(raw, 2);
        this.sequenceNum = Bytes.readUint32(raw, 6);
    }

    /**
     * Resets the pumpTimeSec and sequenceNum to 0 so they don't have to be validated in every test clause
     */
    void clearBaseFieldsForTesting() {
        this.pumpTimeSec = 0;
        this.sequenceNum = 0;

        for (int i=2; i<10; i++) {
            this.cargo[i] = 0;
        }
    }

    public byte[] getCargo() {
        return this.cargo;
    }

    public String toString() {
        return JavaHelpers.autoToString(this, new HashSet<>());
    }

    public String verboseToString() {
        return JavaHelpers.autoToStringVerbose(this, Set.of("cargo"));
    }

    public static byte[] fillCargo(byte[] cargo) {
        if (cargo.length == 26) {
            return cargo;
        }
        byte[] ret = new byte[26];
        System.arraycopy(cargo, 0, ret, 0, cargo.length);
        return ret;
    }

    public HistoryLogProps props() {
        return getClass().getAnnotation(HistoryLogProps.class);
    }
}
