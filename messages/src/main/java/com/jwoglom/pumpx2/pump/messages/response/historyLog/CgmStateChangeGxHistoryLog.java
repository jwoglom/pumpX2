package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.annotations.HistoryLogProps;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;

/**
 * A step of the pump's Dexcom CGM session state machine: the session state before (byte 10) and
 * after (byte 14) the step and the event that caused it (byte 22). Seen on the t:slim X2 and the
 * Tandem Mobi, with G6 and G7 sessions.
 *
 * <p>With no CGM session the pump writes (STOPPED, STOPPED, IDLE_TICK) every 300 s of pump time,
 * on its own schedule, unrelated to the 5-minute BasalDelivery records. With a session it writes
 * CYCLE_START once per 5-minute transmitter cycle, then either DATA_RECEIVED immediately before
 * the {@link DexcomG6CGMHistoryLog} / {@link DexcomG7CGMHistoryLog} reading (same second) or
 * NO_DATA 27 s after CYCLE_START when no reading arrives. The records continue while insulin
 * delivery is suspended and stop only while the pump is off.
 *
 * <p>Evidence (https://github.com/jwoglom/pumpX2/issues/146): byte 10 equals the previous
 * record's byte 14 in 15771 of 15774 consecutive pairs on 11 pumps (the exceptions span pump
 * restarts); byte 14 equals the {@code sessionStateId} of CgmStatusV2Response/CGMStatusResponse
 * polled by a phone app in 276 of 284 polls on 7 pumps; and 4318 of 4324 five-minute CGM
 * readings were immediately preceded by a DATA_RECEIVED record.
 *
 * <p>This is the CGM session state, not Control-IQ's CGM availability: Control-IQ stops treating
 * the CGM as available after about 20 minutes without readings while this state stays ACTIVE.
 * Control-IQ being on or off does not change these records.
 *
 * <p>The class name and the state and event names are provisional. Opcode 219 is not in Tandem's
 * cloud event schema or the Mobi app's history log list, where it is one of two unnamed ids (211
 * and 219) in the block of Gx CGM records (210-220); it is written for G7 sessions too. Bytes
 * 11-13, 15-21 and 23-25 were zero in all 20856 records observed and are exposed raw.
 */
@HistoryLogProps(
    opCode = 219,
    displayName = "CGM State Change"
)
public class CgmStateChangeGxHistoryLog extends HistoryLog {

    private int previousStateId;
    private int stateId;
    private int eventId;
    private int unknown11;
    private int unknown15;
    private long unknown18;
    private int unknown23;

    public CgmStateChangeGxHistoryLog() {}
    public CgmStateChangeGxHistoryLog(long pumpTimeSec, long sequenceNum, int previousStateId, int stateId, int eventId) {
        this(pumpTimeSec, sequenceNum, previousStateId, stateId, eventId, 0, 0, 0, 0);
    }

    public CgmStateChangeGxHistoryLog(long pumpTimeSec, long sequenceNum, int previousStateId, int stateId, int eventId, int unknown11, int unknown15, long unknown18, int unknown23) {
        super(pumpTimeSec, sequenceNum);
        this.cargo = buildCargo(pumpTimeSec, sequenceNum, previousStateId, stateId, eventId, unknown11, unknown15, unknown18, unknown23);
        this.previousStateId = previousStateId;
        this.stateId = stateId;
        this.eventId = eventId;
        this.unknown11 = unknown11;
        this.unknown15 = unknown15;
        this.unknown18 = unknown18;
        this.unknown23 = unknown23;
    }

    public int typeId() {
        return 219;
    }

    public void parse(byte[] raw) {
        Validate.isTrue(raw.length == 26);
        this.cargo = raw;
        parseBase(raw);
        long word10 = Bytes.readUint32(raw, 10);
        long word14 = Bytes.readUint32(raw, 14);
        long word22 = Bytes.readUint32(raw, 22);
        this.previousStateId = (int) (word10 & 0xFF);
        this.unknown11 = (int) (word10 >>> 8);
        this.stateId = (int) (word14 & 0xFF);
        this.unknown15 = (int) (word14 >>> 8);
        this.unknown18 = Bytes.readUint32(raw, 18);
        this.eventId = (int) (word22 & 0xFF);
        this.unknown23 = (int) (word22 >>> 8);
    }

    public static byte[] buildCargo(long pumpTimeSec, long sequenceNum, int previousStateId, int stateId, int eventId) {
        return buildCargo(pumpTimeSec, sequenceNum, previousStateId, stateId, eventId, 0, 0, 0, 0);
    }

    public static byte[] buildCargo(long pumpTimeSec, long sequenceNum, int previousStateId, int stateId, int eventId, int unknown11, int unknown15, long unknown18, int unknown23) {
        return HistoryLog.fillCargo(Bytes.combine(
            HistoryLog.typeIdBytes(219, 0),
            Bytes.toUint32(pumpTimeSec),
            Bytes.toUint32(sequenceNum),
            Bytes.toUint32((previousStateId & 0xFF) | ((long) unknown11 << 8)),
            Bytes.toUint32((stateId & 0xFF) | ((long) unknown15 << 8)),
            Bytes.toUint32(unknown18),
            Bytes.toUint32((eventId & 0xFF) | ((long) unknown23 << 8))));
    }

    /**
     * @return raw byte 10: the session state before this step. It equalled the previous record's
     * {@link #getStateId()} in every observed pair except across pump restarts, which reset the
     * state to STOPPED without writing a record.
     */
    public int getPreviousStateId() {
        return previousStateId;
    }

    public State getPreviousState() {
        return State.fromId(previousStateId);
    }

    /**
     * @return raw byte 14: the session state after this step
     */
    public int getStateId() {
        return stateId;
    }

    public State getState() {
        return State.fromId(stateId);
    }

    /**
     * @return raw byte 22: what caused this step
     */
    public int getEventId() {
        return eventId;
    }

    public Event getEvent() {
        return Event.fromId(eventId);
    }

    /**
     * @return true if the state changed in this step. Most records repeat the state.
     */
    public boolean isStateChange() {
        return previousStateId != stateId;
    }

    /**
     * Zero in every record observed.
     *
     * @return raw bytes 11-13 as a little-endian uint24
     */
    public int getUnknown11() {
        return unknown11;
    }

    /**
     * Zero in every record observed.
     *
     * @return raw bytes 15-17 as a little-endian uint24
     */
    public int getUnknown15() {
        return unknown15;
    }

    /**
     * Zero in every record observed.
     *
     * @return raw uint32 at offset 18
     */
    public long getUnknown18() {
        return unknown18;
    }

    /**
     * Zero in every record observed.
     *
     * @return raw bytes 23-25 as a little-endian uint24
     */
    public int getUnknown23() {
        return unknown23;
    }

    /**
     * CGM session state. Values 0-2 match the {@code sessionStateId} the pump reports in
     * CgmStatusV2Response. Value 3 is rare (39 of 20856 records) and was always entered from
     * START_PENDING on a NO_DATA step while CgmStatusV2Response still reported START_PENDING; its
     * meaning is unknown, so it maps to UNKNOWN (use the raw id).
     */
    public enum State {
        STOPPED(0),
        /**
         * Session started but not yet producing readings. During a long signal loss an ACTIVE
         * session also drops to this state for one cycle every 30 minutes.
         */
        START_PENDING(1),
        ACTIVE(2),
        UNKNOWN(-1),

        ;

        private final int id;
        State(int id) {
            this.id = id;
        }

        public static State fromId(int id) {
            for (State s : values()) {
                if (s.id == id && s != UNKNOWN) {
                    return s;
                }
            }
            return UNKNOWN;
        }

        public int getId() {
            return id;
        }
    }

    /**
     * What caused a step. Provisional names.
     */
    public enum Event {
        /**
         * Start of a transmitter cycle: once per 5-minute cycle while ACTIVE, 0-5 s before
         * DATA_RECEIVED; several in a burst while START_PENDING. Also the event of the
         * STOPPED to START_PENDING transition when a session starts.
         */
        CYCLE_START(1),
        /**
         * Transmitter data received. The CGM reading record, when there is one, follows in the
         * same second.
         */
        DATA_RECEIVED(2),
        /**
         * No reading this cycle: written 27 s after CYCLE_START while ACTIVE, or on the 5-minute
         * schedule when the transmitter was not heard at all.
         */
        NO_DATA(3),
        /**
         * A session command such as a sensor start or stop, transmitter id or G7 pairing code.
         * Every transition to STOPPED has this event.
         */
        SESSION_COMMAND(4),
        /**
         * Follows, within 10 s, a pump-button release or (Mobi) a signed control request from a
         * BLE app. Written only while a CGM is set up; what the pump does at this step is unknown.
         */
        USER_ACTIVITY(5),
        /**
         * 300-second tick while there is no CGM session.
         */
        IDLE_TICK(6),
        UNKNOWN(-1),

        ;

        private final int id;
        Event(int id) {
            this.id = id;
        }

        public static Event fromId(int id) {
            for (Event e : values()) {
                if (e.id == id && e != UNKNOWN) {
                    return e;
                }
            }
            return UNKNOWN;
        }

        public int getId() {
            return id;
        }
    }
}
