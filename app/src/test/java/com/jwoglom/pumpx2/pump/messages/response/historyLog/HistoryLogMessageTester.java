package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.google.common.collect.ImmutableList;
import com.jwoglom.pumpx2.pump.bluetooth.BTResponseParser;
import com.jwoglom.pumpx2.pump.bluetooth.CharacteristicUUID;
import com.jwoglom.pumpx2.pump.bluetooth.TronMessageWrapper;
import com.jwoglom.pumpx2.pump.events.PumpResponseMessageEvent;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.shared.L;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import timber.log.Timber;

public class HistoryLogMessageTester {
    private static final String TAG = "X2-HistoryLogMessageTester";

    public static HistoryLogStreamResponse test(String rawHex, int streamId, List<HistoryLog> expectedLogs) throws DecoderException {
        byte[] initialRead = Hex.decodeHex(rawHex);
        List<byte[]> expectedBytes = expectedLogs.stream().map(HistoryLog::getCargo).collect(Collectors.toList());
        HistoryLogStreamResponse expected = new HistoryLogStreamResponse(1, streamId, expectedBytes);

        UUID uuid = CharacteristicUUID.determine(expected);
        assertEquals(uuid, CharacteristicUUID.HISTORY_LOG_CHARACTERISTICS);

        MessageType messageType = expected.type();

        TronMessageWrapper tron = new TronMessageWrapper(expected, (byte) 0);
        PumpResponseMessageEvent resp = BTResponseParser.parse(tron, initialRead, messageType, uuid);
        assertTrue("Response message returned from parser: " + resp, resp.message().isPresent());

        Message parsedMessage = resp.message().get();
        L.w(TAG, String.format("Parsed: %s\nExpected: %s", parsedMessage, expected));
        assertEquals(expected.getClass(), parsedMessage.getClass());

        HistoryLogStreamResponse streamResp = (HistoryLogStreamResponse) parsedMessage;
        L.w(TAG, "historyLogstoString: " +streamResp.getHistoryLogsToString());
        List<HistoryLog> parsedLogs = streamResp.getHistoryLogs();
        assertNotNull(parsedLogs);

        assertEquals(expectedLogs.size(), parsedLogs.size());
        for (int i=0; i<expectedLogs.size(); i++) {
            assertEquals(26, expectedLogs.get(i).getCargo().length);
            assertEquals(26, parsedLogs.get(i).getCargo().length);
            assertEquals(expectedLogs.get(i).verboseToString(), parsedLogs.get(i).verboseToString());
        }

        return fullTest(rawHex, streamId, expectedLogs);

    }

    public static HistoryLogStreamResponse fullTest(String rawHex, int streamId, List<HistoryLog> expected) throws DecoderException {
        List<byte[]> expectedBytes = expected.stream().map(HistoryLog::getCargo).collect(Collectors.toList());
        HistoryLogStreamResponse expectedResp = new HistoryLogStreamResponse(1, streamId, expectedBytes);
        return (HistoryLogStreamResponse) MessageTester.test(rawHex, 0, 2, CharacteristicUUID.HISTORY_LOG_CHARACTERISTICS, expectedResp);
    }

    // Tests just the historylog parsing without the HistoryLogStreamResponse wrapper
    public static HistoryLog testSingle(String rawHex, HistoryLog expected) throws DecoderException {
        HistoryLog parsed = HistoryLogParser.parse(Hex.decodeHex(rawHex));

        assertEquals(expected.typeId(), parsed.typeId());
        assertEquals(expected.verboseToString(), parsed.verboseToString());

        return parsed;
    }
}
