package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.annotations.HistoryLogProps;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;

/**
 * The pump's running estimate of the insulin in the cartridge. Seen on the Tandem Mobi only.
 *
 * <p>Tandem's export and its Mobi app do not name this opcode, so the class name is provisional.
 * The Mobi writes one of these every ~4 U delivered from the cartridge (consecutive records step
 * by 3.8-4.2 U) and several more while tubing is being filled. It is not written each time the
 * displayed amount changes.
 *
 * <p>Layout, confirmed on 553 records from 5 Mobi pumps (the maintainer's two and three other
 * people's): float {@code insulinDelivered} at bytes 10-13, float {@code insulinRemaining} at
 * 14-17, {@code displayedInsulinRemaining} at 18, zeros at 19-21, uint32 {@code driveCounter} at
 * 22-25. See https://github.com/jwoglom/pumpx2/issues/148.
 */
@HistoryLogProps(
    opCode = 231,
    displayName = "Reservoir Estimate",
    internalName = ""
)
public class ReservoirEstimateHistoryLog extends HistoryLog {

    /**
     * {@link #getInsulinDelivered()} + {@link #getInsulinRemaining()} equalled this value (float bits
     * 0x434beff2, about 203.94 U) to within 0.00002 U in every observed record (553/553).
     */
    public static final float OBSERVED_START_ESTIMATE = Float.intBitsToFloat(0x434beff2);

    private float insulinDelivered;
    private float insulinRemaining;
    private int displayedInsulinRemaining;
    private long driveCounter;

    public ReservoirEstimateHistoryLog() {}
    public ReservoirEstimateHistoryLog(long pumpTimeSec, long sequenceNum, float insulinDelivered, float insulinRemaining, int displayedInsulinRemaining, long driveCounter) {
        super(pumpTimeSec, sequenceNum);
        this.cargo = buildCargo(pumpTimeSec, sequenceNum, insulinDelivered, insulinRemaining, displayedInsulinRemaining, driveCounter);
        this.insulinDelivered = insulinDelivered;
        this.insulinRemaining = insulinRemaining;
        this.displayedInsulinRemaining = displayedInsulinRemaining;
        this.driveCounter = driveCounter;
    }

    public int typeId() {
        return 231;
    }

    public void parse(byte[] raw) {
        Validate.isTrue(raw.length == 26);
        this.cargo = raw;
        parseBase(raw);
        this.insulinDelivered = Bytes.readFloat(raw, 10);
        this.insulinRemaining = Bytes.readFloat(raw, 14);
        this.displayedInsulinRemaining = raw[18] & 0xFF;
        this.driveCounter = Bytes.readUint32(raw, 22);
    }

    public static byte[] buildCargo(long pumpTimeSec, long sequenceNum, float insulinDelivered, float insulinRemaining, int displayedInsulinRemaining, long driveCounter) {
        return HistoryLog.fillCargo(Bytes.combine(
            HistoryLog.typeIdBytes(231, 0),
            Bytes.toUint32(pumpTimeSec),
            Bytes.toUint32(sequenceNum),
            Bytes.toFloat(insulinDelivered),
            Bytes.toFloat(insulinRemaining),
            new byte[]{ (byte) displayedInsulinRemaining, 0, 0, 0 },
            Bytes.toUint32(driveCounter)));
    }

    /**
     * Units of insulin moved out of the cartridge since it was inserted, priming included.
     *
     * <p>It reads 0.00 in the record written with every {@link CartridgeInsertedHistoryLog} (4/4
     * insertions in the maintainer's capture) and keeps counting through a tubing fill done without
     * a cartridge change. It moves in lockstep with {@link #getDriveCounter()}: within one
     * cartridge, {@code driveCounter == a + 1073.52 * insulinDelivered} to within 3 counts on every
     * pump observed.
     */
    public float getInsulinDelivered() {
        return insulinDelivered;
    }

    /**
     * The pump's estimate of the units of insulin left in the cartridge.
     *
     * <p>This is not measured separately. It was {@link #OBSERVED_START_ESTIMATE} (about
     * 203.94 U) minus {@link #getInsulinDelivered()} in 553/553 records across 5 Mobi pumps and
     * many cartridges, so the pump appears to start every cartridge from the same 203.94 U figure
     * (why, and whether that ever differs, is unknown). The record written at cartridge insertion
     * reads 0.00 / 203.94, the same 203.94 U as the {@link FillEstimateFinalHistoryLog} written in
     * that second. Treat it as an estimate, not a measurement.
     */
    public float getInsulinRemaining() {
        return insulinRemaining;
    }

    /**
     * The whole-unit amount the pump reports as remaining, or 0 while a fill is in progress.
     *
     * <p>In every non-zero record (300/300) this was {@link #getInsulinRemaining()} rounded to the
     * nearest 5 U when it is at least 40 U, and rounded down to a whole unit below 40 U (e.g. 43.90
     * reads 45, 39.91 reads 39). The {@code InsulinStatusResponse.currentInsulinAmount} polled
     * nearest to the record (within 60 s) matched it in 100 of 113 non-fill records. In the other
     * 13 the poll came 11-43 s later, read 1-5 U lower, and a bolus was logged within about a
     * minute of the record.
     */
    public int getDisplayedInsulinRemaining() {
        return displayedInsulinRemaining;
    }

    /**
     * A drive position counter, about 1073.5 counts per unit (unit unknown beyond that).
     *
     * <p>It sits near 524,288 (2^19) when a cartridge is inserted and increases with delivery. The
     * {@link PrimeInprocessHistoryLog} records written during a fill carry the same counter at
     * bytes 14-17 (e.g. both read 524274 right after an insertion).
     */
    public long getDriveCounter() {
        return driveCounter;
    }
}
