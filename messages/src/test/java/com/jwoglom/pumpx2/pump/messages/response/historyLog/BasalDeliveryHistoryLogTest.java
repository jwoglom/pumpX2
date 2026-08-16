package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class BasalDeliveryHistoryLogTest {
    // Bytes 12-13 carry an as-yet-unidentified field in ~58% of captured LID_BASAL_DELIVERY
    // records (no reference source defines them); these fixtures use captures where those bytes
    // are zero so the round-trip below is honest.
    @Test
    public void testBasalDeliveryHistoryLog1() throws DecoderException {
        BasalDeliveryHistoryLog expected = (BasalDeliveryHistoryLog) new BasalDeliveryHistoryLog(
            // long pumpTimeSec, long sequenceNum, int commandedRateSource, int commandedRate, int profileBasalRate, int algorithmRate, int tempRate
            580771325L, 490788L, 3, 1000, 1000, 1000, 65535
        ).withHeaderHighNibble(1);

        BasalDeliveryHistoryLog parsedRes = (BasalDeliveryHistoryLog) HistoryLogMessageTester.testSingle(
                "1711fddd9d22247d070003000000e803e803e803ffff00000000",
                expected
        );
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }

    @Test
    public void testBasalDeliveryHistoryLog2() throws DecoderException {
        BasalDeliveryHistoryLog expected = (BasalDeliveryHistoryLog) new BasalDeliveryHistoryLog(
            // long pumpTimeSec, long sequenceNum, int commandedRateSource, int commandedRate, int profileBasalRate, int algorithmRate, int tempRate
            580640885L, 485442L, 1, 1200, 1200, 65535, 65535
        ).withHeaderHighNibble(1);

        BasalDeliveryHistoryLog parsedRes = (BasalDeliveryHistoryLog) HistoryLogMessageTester.testSingle(
                "171175e09b224268070001000000b004b004ffffffff00000000",
                expected
        );
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }

    @Test
    public void testBasalDeliveryHistoryLog3() throws DecoderException {
        // suspend record: no commanded rate, algorithm/temp rate both "n/a" (0xffff)
        BasalDeliveryHistoryLog expected = (BasalDeliveryHistoryLog) new BasalDeliveryHistoryLog(
            // long pumpTimeSec, long sequenceNum, int commandedRateSource, int commandedRate, int profileBasalRate, int algorithmRate, int tempRate
            580601530L, 483924L, 0, 0, 1000, 65535, 65535
        ).withHeaderHighNibble(1);

        BasalDeliveryHistoryLog parsedRes = (BasalDeliveryHistoryLog) HistoryLogMessageTester.testSingle(
                "1711ba469b2254620700000000000000e803ffffffff00000000",
                expected
        );
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }
}
