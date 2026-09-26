package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;
import static org.junit.Assert.assertEquals;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class ChargingStartedHistoryLogTest {
    @Test
    public void testChargingStarted_mobi() throws DecoderException {
        // Maintainer's Tandem Mobi. The CurrentBatteryV2Response read 1 second later changed
        // chargingStatus 0 -> 1 with abc 32, ibc 20, unknown1 155, unknown2 49, unknown3 3762.
        ChargingStartedHistoryLog expected = (ChargingStartedHistoryLog) new ChargingStartedHistoryLog(
                // long pumpTimeSec, long sequenceNum, int currentBatteryAbc, int unknown12, int unknown14, int currentBatteryIbc, int unknown18, int lipoMv, int unknown22, int unknown24
                590517712L, 692754L, 32, 155, 3027, 20, 49, 3762, 0, 29
        ).withHeaderHighNibble(1);

        ChargingStartedHistoryLog parsedRes = (ChargingStartedHistoryLog) HistoryLogMessageTester.testSingle(
                "2210d095322312920a0020009b00d30b14003100b20e00001d00",
                expected
        );
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
        assertEquals(32, parsedRes.getCurrentBatteryAbc());
        assertEquals(20, parsedRes.getCurrentBatteryIbc());
        assertEquals(3762, parsedRes.getLipoMv());
        assertEquals(32, Math.round(100.0 * parsedRes.getUnknown18() / parsedRes.getUnknown12()));
    }

    @Test
    public void testChargingStarted_mobiIssueExample() throws DecoderException {
        ChargingStartedHistoryLog expected = (ChargingStartedHistoryLog) new ChargingStartedHistoryLog(
                589553337L, 641942L, 79, 157, 3031, 80, 124, 3998, 0, 29
        ).withHeaderHighNibble(1);

        ChargingStartedHistoryLog parsedRes = (ChargingStartedHistoryLog) HistoryLogMessageTester.testSingle(
                "2210b9de232396cb09004f009d00d70b50007c009e0f00001d00",
                expected
        );
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }

    @Test
    public void testChargingStarted_tslimX2() throws DecoderException {
        // Maintainer's t:slim X2, written 4 seconds after a UsbConnected record. Bytes 22-25 are 0 on the X2.
        ChargingStartedHistoryLog expected = new ChargingStartedHistoryLog(
                534434756L, 1062478L, 70, 380, 3047, 65, 263, 3924, 0, 0
        );

        ChargingStartedHistoryLog parsedRes = (ChargingStartedHistoryLog) HistoryLogMessageTester.testSingle(
                "2200c4d3da1f4e36100046007c01e70b41000701540f00000000",
                expected
        );
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
        assertEquals(parsedRes.getCurrentBatteryAbc(), 100.0 * parsedRes.getUnknown18() / parsedRes.getUnknown12(), 1.0);
    }
}
