package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.helpers.Dates;
import com.jwoglom.pumpx2.shared.JavaHelpers;

import java.time.Instant;
import java.util.Arrays;
import java.util.HashSet;

public abstract class HistoryLog {
    protected byte[] cargo = null;

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
        Preconditions.checkState(typeId == typeId(), "found typeId " + typeId + " in message but expected " + typeId());

        this.pumpTimeSec = Bytes.readUint32(raw, 2);
        this.sequenceNum = Bytes.readUint32(raw, 6);
    }

    public byte[] getCargo() {
        return this.cargo;
    }

    public String toString() {
        return JavaHelpers.autoToString(this, new HashSet<>());
    }

    public String verboseToString() {
        return JavaHelpers.autoToStringVerbose(this, ImmutableSet.of("cargo"));
    }
}
