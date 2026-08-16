package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class BasalDeliveryHistoryLogTest {
    @Test
    public void testBasalDeliveryHistoryLog1() throws DecoderException {
        // algorithmRate=65535 (0xffff) here represents "n/a" (Control-IQ not commanding a rate).
        // Byte 12-13 of this capture (0x0003 LE = 3) is unparsed: parse() reads commandedRateSource
        // as a 2-byte short at offset 10 (bytes 10-11), then jumps straight to commandedRate at
        // offset 14, so bytes 12-13 are skipped and never asserted here.
        BasalDeliveryHistoryLog expected = new BasalDeliveryHistoryLog(
            // long pumpTimeSec, long sequenceNum, int commandedRateSource, int commandedRate, int profileBasalRate, int algorithmRate, int tempRate
            580777627L, 491072L, 0, 0, 1000, 65535, 65535
        );

        BasalDeliveryHistoryLog parsedRes = (BasalDeliveryHistoryLog) HistoryLogMessageTester.testSingle(
                "17119bf69d22407e0700000003000000e803ffffffff00000000",
                expected
        );
        // no cargo round-trip: bytes 12-13 carry unparsed data (0x0003) that buildCargo zero-fills
    }

    @Test
    public void testBasalDeliveryHistoryLog2() throws DecoderException {
        // Byte 12-13 of this capture (0x0003 LE = 3) is unparsed, same as above.
        BasalDeliveryHistoryLog expected = new BasalDeliveryHistoryLog(
            // long pumpTimeSec, long sequenceNum, int commandedRateSource, int commandedRate, int profileBasalRate, int algorithmRate, int tempRate
            580769524L, 490724L, 3, 1000, 1000, 1000, 65535
        );

        BasalDeliveryHistoryLog parsedRes = (BasalDeliveryHistoryLog) HistoryLogMessageTester.testSingle(
                "1711f4d69d22e47c070003000300e803e803e803ffff00000000",
                expected
        );
        // no cargo round-trip: bytes 12-13 carry unparsed data (0x0003) that buildCargo zero-fills
    }
}
