package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.annotations.HistoryLogProps;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;

/**
 * A press or release of the pump's button, with a millisecond timestamp. Seen on the t:slim X2 and
 * the Tandem Mobi.
 *
 * <p>Tandem's export and its Mobi app do not name this opcode, so the class name and the button
 * interpretation are provisional. The layout is well supported: uint32 {@code timeOfDayMs} at
 * bytes 10-13, {@code state} at byte 14, zeros at 15-25 (1772/1772 records from at least 8 pumps,
 * X2 and Mobi).
 *
 * <p>The button reading rests on three observations. On the Mobi, where tubing is filled by holding
 * the pump button, every tubing-prime start ({@link PrimeInprocessHistoryLog}) was immediately
 * preceded by one of these with state 1 (113/113 on 4 pumps). A triple press (1, 0, 1, 0, 1 within
 * 0.6 s) was immediately followed by a {@link SnoozeActivatedHistoryLog} in the same second. On the
 * X2, whose button wakes the screen, press/release pairs typically last about 0.2 s.
 * See https://github.com/jwoglom/pumpx2/issues/148.
 */
@HistoryLogProps(
    opCode = 17,
    displayName = "Pump Button",
    internalName = ""
)
public class PumpButtonHistoryLog extends HistoryLog {

    private long timeOfDayMs;
    private int state;

    public PumpButtonHistoryLog() {}
    public PumpButtonHistoryLog(long pumpTimeSec, long sequenceNum, long timeOfDayMs, int state) {
        super(pumpTimeSec, sequenceNum);
        this.cargo = buildCargo(pumpTimeSec, sequenceNum, timeOfDayMs, state);
        this.timeOfDayMs = timeOfDayMs;
        this.state = state;
    }

    public int typeId() {
        return 17;
    }

    public void parse(byte[] raw) {
        Validate.isTrue(raw.length == 26);
        this.cargo = raw;
        parseBase(raw);
        this.timeOfDayMs = Bytes.readUint32(raw, 10);
        this.state = raw[14] & 0xFF;
    }

    public static byte[] buildCargo(long pumpTimeSec, long sequenceNum, long timeOfDayMs, int state) {
        return HistoryLog.fillCargo(Bytes.combine(
            HistoryLog.typeIdBytes(17, 0),
            Bytes.toUint32(pumpTimeSec),
            Bytes.toUint32(sequenceNum),
            Bytes.toUint32(timeOfDayMs),
            new byte[]{ (byte) state }));
    }

    /**
     * Milliseconds since midnight on the pump's local clock, the same clock as
     * {@link #getPumpTimeSec()}: {@code timeOfDayMs / 1000 == pumpTimeSec % 86400} in 1772/1772
     * records. It gives sub-second ordering within the record's second; it is not a motor or
     * encoder count.
     */
    public long getTimeOfDayMs() {
        return timeOfDayMs;
    }

    /**
     * Byte 14: 1 or 0, alternating (880 records with 1, 892 with 0). Read as 1 = pressed,
     * 0 = released; see the class comment for why that reading is provisional.
     */
    public int getState() {
        return state;
    }

    public boolean isPressed() {
        return state == 1;
    }
}
