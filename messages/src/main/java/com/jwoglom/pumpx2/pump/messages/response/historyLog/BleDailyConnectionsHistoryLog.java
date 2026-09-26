package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import java.util.Arrays;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.annotations.HistoryLogProps;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;

/**
 * Once-a-day Tandem Mobi summary of the BLE connections made to the pump. The pump writes it every
 * 24 hours counted from its last restart, not at midnight, so its time of day differs between pumps
 * and each record covers the 24 hours since the previous one. The header timestamp is the end of
 * that period.
 *
 * <p>The counts were compared with the connections and JPAKE authentications TandemKit logged in
 * 12 day-long windows on two pumps (the maintainer's and a tester's).
 * {@link #getConnections()} minus {@link #getUnauthenticatedConnections()} equalled the number of
 * authenticated sessions in all 12 windows. The class name is provisional: opcode 474 is not in
 * Tandem's cloud event schema or the Mobi app's history log list. Fields not named here have not
 * been identified and are exposed raw. See https://github.com/jwoglom/pumpx2/issues/152.
 */
@HistoryLogProps(
    opCode = 474,
    displayName = "BLE Daily Connections"
)
public class BleDailyConnectionsHistoryLog extends HistoryLog {

    private int connectedPercent;
    private int unknown11;
    private int connections;
    private int unauthenticatedConnections;
    private byte[] unknownTail;

    public BleDailyConnectionsHistoryLog() {}
    public BleDailyConnectionsHistoryLog(long pumpTimeSec, long sequenceNum, int connectedPercent, int unknown11, int connections, int unauthenticatedConnections, byte[] unknownTail) {
        super(pumpTimeSec, sequenceNum);
        this.cargo = buildCargo(pumpTimeSec, sequenceNum, connectedPercent, unknown11, connections, unauthenticatedConnections, unknownTail);
        this.connectedPercent = connectedPercent;
        this.unknown11 = unknown11;
        this.connections = connections;
        this.unauthenticatedConnections = unauthenticatedConnections;
        this.unknownTail = Arrays.copyOfRange(this.cargo, 16, 26);
    }

    public BleDailyConnectionsHistoryLog(long pumpTimeSec, long sequenceNum, int connectedPercent, int unknown11, int connections, int unauthenticatedConnections) {
        this(pumpTimeSec, sequenceNum, connectedPercent, unknown11, connections, unauthenticatedConnections, new byte[10]);
    }

    public int typeId() {
        return 474;
    }

    public void parse(byte[] raw) {
        Validate.isTrue(raw.length == 26);
        this.cargo = raw;
        parseBase(raw);
        this.connectedPercent = raw[10] & 0xFF;
        this.unknown11 = raw[11] & 0xFF;
        this.connections = Bytes.readShort(raw, 12);
        this.unauthenticatedConnections = Bytes.readShort(raw, 14);
        this.unknownTail = Arrays.copyOfRange(raw, 16, 26);
    }

    public static byte[] buildCargo(long pumpTimeSec, long sequenceNum, int connectedPercent, int unknown11, int connections, int unauthenticatedConnections, byte[] unknownTail) {
        Validate.isTrue(unknownTail.length == 10, "unknownTail must be 10 bytes");
        return HistoryLog.fillCargo(Bytes.combine(
            HistoryLog.typeIdBytes(474, 0),
            Bytes.toUint32(pumpTimeSec),
            Bytes.toUint32(sequenceNum),
            Bytes.firstByteLittleEndian(connectedPercent),
            Bytes.firstByteLittleEndian(unknown11),
            Bytes.firstTwoBytesLittleEndian(connections),
            Bytes.firstTwoBytesLittleEndian(unauthenticatedConnections),
            unknownTail));
    }

    /**
     * The share of the 24 hours during which the pump had a BLE connection, apparently rounded to
     * the nearest percent. Values of 97-100 were observed. It matched the phone-side connected time
     * in 11 of 12 windows; the twelfth was 0.02 points below the rounding threshold. It read 100
     * with {@link #getConnections()} 0 on a day with no reconnection.
     * Medium confidence.
     *
     * @return percent of the period with a BLE connection, 0-100
     */
    public int getConnectedPercent() {
        return connectedPercent;
    }

    /**
     * Constant per pump: 6 on both of the maintainer's Mobis, 4 on a tester's. Its meaning is
     * unknown, and it is not a firmware or model code.
     *
     * @return raw uint8 at offset 11
     */
    public int getUnknown11() {
        return unknown11;
    }

    /**
     * BLE connections the pump accepted during the period, including ones that failed encryption
     * or never authenticated. Values of 0-66 were observed. This equalled the connections the phone
     * logged (connects plus encryption failures) in 8 of 12 windows. In the other 4 it was exactly
     * 2 higher, from connections the phone never reported. Byte 13 was 0 in every record, so it is
     * read as a uint16; byte 12 alone gives the same values.
     *
     * @return number of BLE connections in the period
     */
    public int getConnections() {
        return connections;
    }

    /**
     * Connections during the period that never completed JPAKE authentication (key confirmation),
     * including failed encryption and links dropped during setup. Values of 0-11 were observed. The
     * value was 0 exactly on the days the phone logged no failed, cancelled or timed-out
     * connection. Byte 15 was always 0, so it is read as a uint16, like {@link #getConnections()}.
     *
     * @return number of connections that did not authenticate
     */
    public int getUnauthenticatedConnections() {
        return unauthenticatedConnections;
    }

    /**
     * Equal to the authenticated sessions TandemKit logged in 12 of 12 windows on two pumps.
     *
     * @return {@link #getConnections()} minus {@link #getUnauthenticatedConnections()}
     */
    public int getAuthenticatedConnections() {
        return connections - unauthenticatedConnections;
    }

    /**
     * Bytes 16-25. Zero in every record examined. No new pairing happened in any window
     * examined, so a pairing counter here would also have read 0.
     *
     * @return a copy of bytes 16-25
     */
    public byte[] getUnknownTail() {
        return Arrays.copyOf(unknownTail, unknownTail.length);
    }
}
