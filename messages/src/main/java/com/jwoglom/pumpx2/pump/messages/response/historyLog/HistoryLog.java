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
     * <p>The pump recomputes these amounts rather than copying them, so values which are
     * arithmetically equal can differ by one unit in the last place: a bolus can report a
     * requested amount of 0.25 in one log and 0.2500000298 in another. Consumers reconciling or
     * deduplicating boluses across logs should compare within this tolerance rather than testing
     * float equality.
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
     * typeId. The remaining high nibble is 0 on t:slim X2 logs and 1 on Tandem Mobi logs, so it
     * appears to be a log format or pump generation discriminator.
     *
     * <p>Its exact meaning is not confirmed. It is exposed so that consumers can distinguish the
     * two encodings, and so that {@code buildCargo} can round-trip a Mobi record byte-for-byte.
     *
     * @return the high nibble of the first two cargo bytes, or 0 if the cargo is not populated
     */
    public int getLogGeneration() {
        if (cargo == null || cargo.length < 2) {
            return 0;
        }
        return (Bytes.readShort(cargo, 0) >>> 12) & 0x0F;
    }

    /**
     * Builds the leading two cargo bytes from a typeId and a log generation nibble.
     * With a generation of 0 this is identical to the historical {@code new byte[]{typeId, 0}},
     * except that it does not truncate a typeId above 255.
     *
     * @param typeId the history log typeId, which occupies the low 12 bits
     * @param logGeneration the log generation nibble, see {@link #getLogGeneration()}
     * @return the first two bytes of the cargo, little endian
     */
    public static byte[] typeIdBytes(int typeId, int logGeneration) {
        return new byte[]{
            (byte) (typeId & 0xFF),
            (byte) (((typeId >> 8) & 0x0F) | ((logGeneration & 0x0F) << 4))
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
