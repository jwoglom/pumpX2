package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;
import static org.junit.Assert.assertEquals;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class ChargingStoppedHistoryLogTest {
    @Test
    public void testChargingStopped_mobi() throws DecoderException {
        // Maintainer's Tandem Mobi. The CurrentBatteryV2Response read in the same second changed
        // chargingStatus 1 -> 0 with abc 59, ibc 50, unknown1 156, unknown2 91, unknown3 3967.
        ChargingStoppedHistoryLog expected = (ChargingStoppedHistoryLog) new ChargingStoppedHistoryLog(
                // long pumpTimeSec, long sequenceNum, int currentBatteryAbc, int unknown12, int unknown14, int currentBatteryIbc, int unknown18, int lipoMv, int unknown22, int unknown24
                590518431L, 692768L, 59, 156, 3058, 50, 91, 3967, 0, 33
        ).withHeaderHighNibble(1);

        ChargingStoppedHistoryLog parsedRes = (ChargingStoppedHistoryLog) HistoryLogMessageTester.testSingle(
                "23109f98322320920a003b009c00f20b32005b007f0f00002100",
                expected
        );
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
        assertEquals(59, parsedRes.getCurrentBatteryAbc());
        assertEquals(50, parsedRes.getCurrentBatteryIbc());
        assertEquals(3967, parsedRes.getLipoMv());
    }

    @Test
    public void testChargingStopped_mobiEndOfSession() throws DecoderException {
        // 22 minutes after the ChargingStarted at 32%; the battery had reached abc 79, ibc 80.
        ChargingStoppedHistoryLog expected = (ChargingStoppedHistoryLog) new ChargingStoppedHistoryLog(
                590519030L, 692802L, 79, 156, 3079, 80, 123, 4105, 0, 33
        ).withHeaderHighNibble(1);

        ChargingStoppedHistoryLog parsedRes = (ChargingStoppedHistoryLog) HistoryLogMessageTester.testSingle(
                "2310f69a322342920a004f009c00070c50007b00091000002100",
                expected
        );
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }

    @Test
    public void testChargingStopped_mobiIssueExampleFull() throws DecoderException {
        ChargingStoppedHistoryLog expected = (ChargingStoppedHistoryLog) new ChargingStoppedHistoryLog(
                589554219L, 641961L, 100, 157, 3083, 100, 157, 4193, 0, 35
        ).withHeaderHighNibble(1);

        ChargingStoppedHistoryLog parsedRes = (ChargingStoppedHistoryLog) HistoryLogMessageTester.testSingle(
                "23102be22323a9cb090064009d000b0c64009d00611000002300",
                expected
        );
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }

    @Test
    public void testChargingStopped_tslimX2() throws DecoderException {
        // Maintainer's t:slim X2, written 3 seconds after a UsbDisconnected record.
        ChargingStoppedHistoryLog expected = new ChargingStoppedHistoryLog(
                534435776L, 1062525L, 87, 380, 3071, 90, 330, 4096, 0, 0
        );

        ChargingStoppedHistoryLog parsedRes = (ChargingStoppedHistoryLog) HistoryLogMessageTester.testSingle(
                "2300c0d7da1f7d36100057007c01ff0b5a004a01001000000000",
                expected
        );
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }
}
