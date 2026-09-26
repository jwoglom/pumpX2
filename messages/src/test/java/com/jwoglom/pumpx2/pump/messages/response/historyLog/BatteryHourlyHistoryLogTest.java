package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;
import static org.junit.Assert.assertEquals;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class BatteryHourlyHistoryLogTest {
    // Maintainer's Tandem Mobi, Sept 2026 BLE captures. Header high nibble 1.

    @Test
    public void testBatteryHourly_90percent() throws DecoderException {
        BatteryHourlyHistoryLog expected = (BatteryHourlyHistoryLog) new BatteryHourlyHistoryLog(
                // long pumpTimeSec, long sequenceNum, int currentBatteryIbc, int unknown12, int lipoMv, int unknown16, int unknown18, int unknown20, int unknown22, int unknown24
                589528574L, 640810L, 90, 0x0128, 4057, 0, 136, 157, 135, 156
        ).withHeaderHighNibble(1);

        BatteryHourlyHistoryLog parsedRes = (BatteryHourlyHistoryLog) HistoryLogMessageTester.testSingle(
                "0f11fe7d23232ac709005a002801d90f000088009d0087009c00",
                expected
        );
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
        assertEquals(90, parsedRes.getCurrentBatteryIbc());
        assertEquals(4057, parsedRes.getLipoMv());
    }

    @Test
    public void testBatteryHourly_negativeUnknown16() throws DecoderException {
        // CurrentBatteryV2Response 33 seconds earlier: abc 92, ibc 95, unknown1 157, unknown2 144, unknown3 4108
        BatteryHourlyHistoryLog expected = (BatteryHourlyHistoryLog) new BatteryHourlyHistoryLog(
                590115462L, 671001L, 95, 0x0128, 4106, -6, 144, 157, 144, 157
        ).withHeaderHighNibble(1);

        BatteryHourlyHistoryLog parsedRes = (BatteryHourlyHistoryLog) HistoryLogMessageTester.testSingle(
                "0f1186722c23193d0a005f0028010a10faff90009d0090009d00",
                expected
        );
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
        assertEquals(95, parsedRes.getCurrentBatteryIbc());
        assertEquals(-6, parsedRes.getUnknown16());
        assertEquals(92, Math.round(100.0 * parsedRes.getUnknown18() / parsedRes.getUnknown20()));
    }

    @Test
    public void testBatteryHourly_whileCharging() throws DecoderException {
        // CurrentBatteryV2Response 10 seconds later: chargingStatus 1, unknown4 207
        BatteryHourlyHistoryLog expected = (BatteryHourlyHistoryLog) new BatteryHourlyHistoryLog(
                590518738L, 692795L, 65, 0x0128, 4024, 209, 107, 157, 106, 156
        ).withHeaderHighNibble(1);

        BatteryHourlyHistoryLog parsedRes = (BatteryHourlyHistoryLog) HistoryLogMessageTester.testSingle(
                "0f11d29932233b920a0041002801b80fd1006b009d006a009c00",
                expected
        );
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
        assertEquals(209, parsedRes.getUnknown16());
    }

    @Test
    public void testBatteryHourly_lowBatteryFlag() throws DecoderException {
        BatteryHourlyHistoryLog expected = (BatteryHourlyHistoryLog) new BatteryHourlyHistoryLog(
                590882400L, 712415L, 20, 0x012C, 3774, 0, 54, 158, 53, 157
        ).withHeaderHighNibble(1);

        BatteryHourlyHistoryLog parsedRes = (BatteryHourlyHistoryLog) HistoryLogMessageTester.testSingle(
                "0f1160263823dfde0a0014002c01be0e000036009e0035009d00",
                expected
        );
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
        assertEquals(0x0004, parsedRes.getUnknown12() & 0x0004);
    }

    @Test
    public void testBatteryHourly_fullAfterCharge() throws DecoderException {
        BatteryHourlyHistoryLog expected = (BatteryHourlyHistoryLog) new BatteryHourlyHistoryLog(
                590615957L, 697799L, 100, 0x0228, 4174, 0, 157, 157, 157, 157
        ).withHeaderHighNibble(1);

        BatteryHourlyHistoryLog parsedRes = (BatteryHourlyHistoryLog) HistoryLogMessageTester.testSingle(
                "0f1195153423c7a50a00640028024e1000009d009d009d009d00",
                expected
        );
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
        assertEquals(100, parsedRes.getCurrentBatteryIbc());
    }
}
