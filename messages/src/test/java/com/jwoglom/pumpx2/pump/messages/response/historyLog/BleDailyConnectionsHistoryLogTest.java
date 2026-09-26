package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class BleDailyConnectionsHistoryLogTest {

    @Test
    public void testBleDailyConnections_noUnauthenticatedConnections() throws DecoderException {
        // Maintainer's Tandem Mobi (7.9.0.2). Header high nibble 1.
        BleDailyConnectionsHistoryLog expected = (BleDailyConnectionsHistoryLog) new BleDailyConnectionsHistoryLog(
                // long pumpTimeSec, long sequenceNum, int connectedPercent, int unknown11, int connections, int unauthenticatedConnections
                590716558L, 703738L, 100, 6, 32, 0
        ).withHeaderHighNibble(1);

        BleDailyConnectionsHistoryLog parsedRes = (BleDailyConnectionsHistoryLog) HistoryLogMessageTester.testSingle(
                "da118e9e3523fabc0a0064062000000000000000000000000000",
                expected
        );
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
        assertEquals(100, parsedRes.getConnectedPercent());
        assertEquals(32, parsedRes.getConnections());
        assertEquals(0, parsedRes.getUnauthenticatedConnections());
        assertEquals(32, parsedRes.getAuthenticatedConnections());
        assertArrayEquals(new byte[10], parsedRes.getUnknownTail());
    }

    @Test
    public void testBleDailyConnections_mostConnections() throws DecoderException {
        // Maintainer's Tandem Mobi (7.9.0.2). Header high nibble 1.
        BleDailyConnectionsHistoryLog expected = (BleDailyConnectionsHistoryLog) new BleDailyConnectionsHistoryLog(
                590543757L, 693854L, 99, 6, 66, 3
        ).withHeaderHighNibble(1);

        BleDailyConnectionsHistoryLog parsedRes = (BleDailyConnectionsHistoryLog) HistoryLogMessageTester.testSingle(
                "da118dfb32235e960a0063064200030000000000000000000000",
                expected
        );
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
        assertEquals(99, parsedRes.getConnectedPercent());
        assertEquals(66, parsedRes.getConnections());
        assertEquals(3, parsedRes.getUnauthenticatedConnections());
        assertEquals(63, parsedRes.getAuthenticatedConnections());
    }

    @Test
    public void testBleDailyConnections_longOutage() throws DecoderException {
        // Maintainer's Tandem Mobi (7.9.0.2). Header high nibble 1.
        // About 40 minutes without a connection that day.
        BleDailyConnectionsHistoryLog expected = (BleDailyConnectionsHistoryLog) new BleDailyConnectionsHistoryLog(
                590802958L, 708039L, 97, 6, 35, 5
        ).withHeaderHighNibble(1);

        BleDailyConnectionsHistoryLog parsedRes = (BleDailyConnectionsHistoryLog) HistoryLogMessageTester.testSingle(
                "da110ef03623c7cd0a0061062300050000000000000000000000",
                expected
        );
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
        assertEquals(97, parsedRes.getConnectedPercent());
        assertEquals(35, parsedRes.getConnections());
        assertEquals(5, parsedRes.getUnauthenticatedConnections());
        assertEquals(30, parsedRes.getAuthenticatedConnections());
    }

    @Test
    public void testBleDailyConnections_singleConnectionAllDay() throws DecoderException {
        // Maintainer's Tandem Mobi (7.9.0.1). Header high nibble 1.
        // One connection was held for the whole 24 hours, so no new connections were made.
        BleDailyConnectionsHistoryLog expected = (BleDailyConnectionsHistoryLog) new BleDailyConnectionsHistoryLog(
                585137729L, 629401L, 100, 6, 0, 0
        ).withHeaderHighNibble(1);

        BleDailyConnectionsHistoryLog parsedRes = (BleDailyConnectionsHistoryLog) HistoryLogMessageTester.testSingle(
                "da11417ee022999a090064060000000000000000000000000000",
                expected
        );
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
        assertEquals(100, parsedRes.getConnectedPercent());
        assertEquals(0, parsedRes.getConnections());
        assertEquals(0, parsedRes.getUnauthenticatedConnections());
    }
}
