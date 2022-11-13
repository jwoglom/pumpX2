package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.bluetooth.BTResponseParser;
import com.jwoglom.pumpx2.pump.messages.bluetooth.CharacteristicUUID;
import com.jwoglom.pumpx2.pump.messages.bluetooth.TronMessageWrapper;
import com.jwoglom.pumpx2.pump.messages.bluetooth.models.PumpResponseMessage;
import com.jwoglom.pumpx2.shared.L;

import org.apache.commons.codec.DecoderException;
import com.jwoglom.pumpx2.shared.Hex;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class HistoryLogMessageTester {
    private static final String TAG = "HistoryLogMessageTester";

    public static HistoryLogStreamResponse test(String rawHex, int streamId, List<HistoryLog> expectedLogs) throws DecoderException {
        byte[] initialRead = Hex.decodeHex(rawHex);
        List<byte[]> expectedBytes = expectedLogs.stream().map(HistoryLog::getCargo).collect(Collectors.toList());
        HistoryLogStreamResponse expected = new HistoryLogStreamResponse(1, streamId, expectedBytes);

        UUID uuid = CharacteristicUUID.determine(expected);
        assertEquals(uuid, CharacteristicUUID.HISTORY_LOG_CHARACTERISTICS);

        MessageType messageType = expected.type();

        TronMessageWrapper tron = new TronMessageWrapper(expected, (byte) 0);
        PumpResponseMessage resp = BTResponseParser.parse(tron, initialRead, messageType, uuid);
        assertTrue("Response message returned from parser: " + resp, resp.message().isPresent());

        Message parsedMessage = resp.message().get();
        L.d(TAG, String.format("Parsed: %s\nExpected: %s", parsedMessage, expected));
        assertEquals(expected.getClass(), parsedMessage.getClass());

        HistoryLogStreamResponse streamResp = (HistoryLogStreamResponse) parsedMessage;
        L.d(TAG, "historyLogstoString: " +streamResp.getHistoryLogsToString());
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


    // Tests just the historylog parsing without the HistoryLogStreamResponse wrapper
    public static HistoryLog testSingleIgnoringBaseFields(String rawHex, HistoryLog expected) throws DecoderException {
        HistoryLog parsed = HistoryLogParser.parse(Hex.decodeHex(rawHex));
        parsed.clearBaseFieldsForTesting();

        assertEquals(expected.typeId(), parsed.typeId());
        assertEquals(expected.verboseToString(), parsed.verboseToString());

        return parsed;
    }
}
