package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import com.google.common.base.Preconditions;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.request.NonexistentErrorRequest;
import com.jwoglom.pumpx2.pump.messages.request.historyLog.NonexistentHistoryLogStreamRequest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Multiple HistoryLogStreamResponse's can be received after initiating a HistoryLogRequest.
 * The available sequence numbers can be checked on with a HistoryLogStatusRequest.
 * This response message is received via the HISTORY_LOG CharacteristicUUID,
 * and all other Response objects in the historyLog package are wrapped inside of
 * HistoryLogStreamResponse (they contain their own opcodes like standard requests).
 */
@MessageProps(
    opCode=-127,
    size=28,
    variableSize=true,
    stream=true,
    type=MessageType.RESPONSE,
    request=NonexistentHistoryLogStreamRequest.class
)
public class HistoryLogStreamResponse extends Message {
    
    private int numberOfHistoryLogs;
    private int streamId;
    private List<byte[]> historyLogStreamBytes;
    private List<HistoryLog> historyLogs;
    
    public HistoryLogStreamResponse() {}
    
    public HistoryLogStreamResponse(int numberOfHistoryLogs, int streamId, List<byte[]> historyLogStreamBytes) {
        this.cargo = buildCargo(numberOfHistoryLogs, streamId, historyLogStreamBytes);
        this.numberOfHistoryLogs = numberOfHistoryLogs;
        this.streamId = streamId;
        this.historyLogStreamBytes = historyLogStreamBytes;
        this.historyLogs = parseHistoryLogStream(historyLogStreamBytes);
        
    }

    public void parse(byte[] raw) {
        // We do NOT want this check, the size is variable!
        //Preconditions.checkArgument(raw.length == props().size());
        this.cargo = raw;
        this.numberOfHistoryLogs = raw[0];
        this.streamId = raw[1];

        this.historyLogStreamBytes = new ArrayList<>();
        for (int i=2; i < raw.length; i += 26) {
            // Each HistoryLog message is 26 bytes long.
            historyLogStreamBytes.add(Arrays.copyOfRange(raw, i, i+26));
        }
        this.historyLogs = parseHistoryLogStream(historyLogStreamBytes);

        if (raw.length != (numberOfHistoryLogs*26) + 2) {
            throw new IllegalArgumentException("numberOfHistoryLogs*26 + 2 should equal the raw length: " + verboseToString());
        }
        
    }

    
    public static byte[] buildCargo(int numberOfHistoryLogs, int streamId, List<byte[]> historyLogStreams) {
        byte[] historyLogStreamBytes = new byte[0];
        for (byte[] stream : historyLogStreams) {
            historyLogStreamBytes = Bytes.combine(historyLogStreamBytes, stream);
        }

        return Bytes.combine(
            new byte[]{ (byte) numberOfHistoryLogs },
            new byte[]{ (byte) streamId },
            historyLogStreamBytes
            );
    }

    public static List<HistoryLog> parseHistoryLogStream(List<byte[]> historyLogStreamBytes) {
        return historyLogStreamBytes.stream()
                .map(HistoryLogParser::parse)
                .collect(Collectors.toList());
    }

    public int getStreamId() {
        return streamId;
    }
    
    public int getNumberOfHistoryLogs() {
        return numberOfHistoryLogs;
    }

    public List<byte[]> getHistoryLogStreamBytes() {
        return historyLogStreamBytes;
    }

    public List<HistoryLog> getHistoryLogs() {
        return historyLogs;
    }

    public String getHistoryLogsToString() {
        if (historyLogs == null) {
            return null;
        }
        return historyLogs.stream().map(HistoryLog::verboseToString).collect(Collectors.joining(", "));
    }
    
}