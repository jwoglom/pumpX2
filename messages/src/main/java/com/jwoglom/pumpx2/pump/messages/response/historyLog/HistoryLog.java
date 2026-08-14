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
    /**
     * Tolerance, in units of insulin, for comparing the float insulin amounts reported across
     * history logs.
     *
     * <p>Values which are arithmetically equal can differ by one unit in the last place across
     * logs, so consumers reconciling or deduplicating boluses should compare within this tolerance
     * rather than testing float equality. The observation motivating this (a requested amount
     * reading 0.25 in one log and 0.2500000298 in another, attributed to the pump recomputing
     * rather than copying the value) is reported in the analysis attached to the linked issue and
     * is not reproduced by any record committed to this repository.
     */
    public static final float INSULIN_FLOAT_EPSILON = 1e-6f;

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
     * The first two bytes of a history log are a little-endian uint16 whose low 12 bits are the
     * typeId, leaving these top 4 bits over.
     *
     * <p>The 12/4 split is confirmed by Tandem's own Mobi Android app, which reads the first two
     * bytes little endian and masks with 4095 before looking up the log type
     * ({@code HistoryLogStreamResponse$HistoryLogStreamCargo}).
     *
     * <p><b>What the top 4 bits mean is unknown, and Tandem's app does not appear to care.</b> It
     * masks them off and discards them: its {@code HistoryLog} model stores the raw bytes, the
     * masked type, the timestamp, the sequence number and four 4-byte payload fields, with nothing
     * corresponding to these bits, and nothing in the decompiled app reads them back. The only
     * records in this repository carrying a nonzero value here are the
     * {@link DexcomG7CGMHistoryLog} fixtures, which carry 1.
     *
     * <p>The value is read and preserved only so that {@code buildCargo} can reproduce such a
     * record byte for byte. Do not infer a pump model, a firmware version, or a log format from it.
     *
     * @return the top 4 bits of the first two cargo bytes, or 0 if the cargo is not populated
     */
    public int getHeaderHighNibble() {
        if (cargo == null || cargo.length < 2) {
            return 0;
        }
        return (Bytes.readShort(cargo, 0) >>> 12) & 0x0F;
    }

    /**
     * Builds the leading two cargo bytes from a typeId and the high nibble described in
     * {@link #getHeaderHighNibble()}. With a nibble of 0 this is identical to the historical
     * {@code new byte[]{typeId, 0}}, except that it does not truncate a typeId above 255.
     *
     * @param typeId the history log typeId, which occupies the low 12 bits
     * @param headerHighNibble the top 4 bits, of unknown meaning, see {@link #getHeaderHighNibble()}
     * @return the first two bytes of the cargo, little endian
     */
    public static byte[] typeIdBytes(int typeId, int headerHighNibble) {
        return new byte[]{
            (byte) (typeId & 0xFF),
            (byte) (((typeId >> 8) & 0x0F) | ((headerHighNibble & 0x0F) << 4))
        };
    }

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
