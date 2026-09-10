package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class BasalDeliveryHistoryLogTest {
    // Bytes 12-13 are decoded as basalDeliveryFlags (see BasalDeliveryHistoryLog#getBasalDeliveryFlags
    // and https://github.com/jwoglom/pumpx2/issues/77). The first three fixtures below predate that
    // and carry 0 there, so they use the constructor without the flags argument; the later fixtures
    // exercise the flags = 3 case directly.
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
        assertEquals(0, parsedRes.getBasalDeliveryFlags());
        assertEquals(BasalDeliveryHistoryLog.CommandedRateSource.ALGORITHM, parsedRes.getCommandedRateSourceEnum());
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
        assertEquals(0, parsedRes.getBasalDeliveryFlags());
        assertEquals(BasalDeliveryHistoryLog.CommandedRateSource.PROFILE, parsedRes.getCommandedRateSourceEnum());
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
        assertEquals(0, parsedRes.getBasalDeliveryFlags());
        assertEquals(BasalDeliveryHistoryLog.CommandedRateSource.SUSPENDED, parsedRes.getCommandedRateSourceEnum());
    }

    // The fixtures below were observed on a Tandem Mobi driven by Trio (Control-IQ off, no CGM
    // paired), Sept 2026 BLE capture. Bytes 12-13 read 3 whenever a rate field changed or a
    // temp-rate/suspend/resume event fell in the preceding 5-minute interval, and 0 for
    // steady-state delivery.

    @Test
    public void testBasalDeliveryHistoryLog_tempRateStart_flags3() throws DecoderException {
        // temp rate 160% (1600 mU/hr on a 1000 mU/hr profile) activated in the interval
        BasalDeliveryHistoryLog expected = (BasalDeliveryHistoryLog) new BasalDeliveryHistoryLog(
            // long pumpTimeSec, long sequenceNum, int commandedRateSource, int basalDeliveryFlags, int commandedRate, int profileBasalRate, int algorithmRate, int tempRate
            589526924L, 640766L, 2, 3, 1600, 1000, 65535, 1600
        ).withHeaderHighNibble(1);

        BasalDeliveryHistoryLog parsedRes = (BasalDeliveryHistoryLog) HistoryLogMessageTester.testSingle(
                "17118c772323fec60900020003004006e803ffff400600000000",
                expected
        );
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
        assertEquals(2, parsedRes.getCommandedRateSource());
        assertEquals(BasalDeliveryHistoryLog.CommandedRateSource.TEMP_RATE, parsedRes.getCommandedRateSourceEnum());
        assertEquals(3, parsedRes.getBasalDeliveryFlags());
        assertEquals(1600, parsedRes.getCommandedRate());
        assertEquals(1000, parsedRes.getProfileBasalRate());
        assertEquals(65535, parsedRes.getAlgorithmRate());
        assertEquals(1600, parsedRes.getTempRate());
    }

    @Test
    public void testBasalDeliveryHistoryLog_tempRateZero_flags3() throws DecoderException {
        // temp rate changed to 0% in the interval
        BasalDeliveryHistoryLog expected = (BasalDeliveryHistoryLog) new BasalDeliveryHistoryLog(
            // long pumpTimeSec, long sequenceNum, int commandedRateSource, int basalDeliveryFlags, int commandedRate, int profileBasalRate, int algorithmRate, int tempRate
            589527224L, 640778L, 2, 3, 0, 1000, 65535, 0
        ).withHeaderHighNibble(1);

        BasalDeliveryHistoryLog parsedRes = (BasalDeliveryHistoryLog) HistoryLogMessageTester.testSingle(
                "1711b87823230ac70900020003000000e803ffff000000000000",
                expected
        );
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
        assertEquals(BasalDeliveryHistoryLog.CommandedRateSource.TEMP_RATE, parsedRes.getCommandedRateSourceEnum());
        assertEquals(3, parsedRes.getBasalDeliveryFlags());
        assertEquals(0, parsedRes.getCommandedRate());
        assertEquals(0, parsedRes.getTempRate());
    }

    @Test
    public void testBasalDeliveryHistoryLog_tempRateSteadyState_flags0() throws DecoderException {
        // same 0% temp rate still active, nothing changed: flags are 0 even though a temp rate is active
        BasalDeliveryHistoryLog expected = (BasalDeliveryHistoryLog) new BasalDeliveryHistoryLog(
            // long pumpTimeSec, long sequenceNum, int commandedRateSource, int basalDeliveryFlags, int commandedRate, int profileBasalRate, int algorithmRate, int tempRate
            589527525L, 640786L, 2, 0, 0, 1000, 65535, 0
        ).withHeaderHighNibble(1);

        BasalDeliveryHistoryLog parsedRes = (BasalDeliveryHistoryLog) HistoryLogMessageTester.testSingle(
                "1711e579232312c70900020000000000e803ffff000000000000",
                expected
        );
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
        assertEquals(BasalDeliveryHistoryLog.CommandedRateSource.TEMP_RATE, parsedRes.getCommandedRateSourceEnum());
        assertEquals(0, parsedRes.getBasalDeliveryFlags());
        assertEquals(0, parsedRes.getCommandedRate());
        assertEquals(0, parsedRes.getTempRate());
    }

    @Test
    public void testBasalDeliveryHistoryLog_tempRateActiveSteadyState_flags0() throws DecoderException {
        // 90% temp rate (900 mU/hr) active and unchanged: flags 0 with commandedRate == tempRate
        BasalDeliveryHistoryLog expected = (BasalDeliveryHistoryLog) new BasalDeliveryHistoryLog(
            // long pumpTimeSec, long sequenceNum, int commandedRateSource, int basalDeliveryFlags, int commandedRate, int profileBasalRate, int algorithmRate, int tempRate
            589535633L, 641098L, 2, 0, 900, 1000, 65535, 900
        ).withHeaderHighNibble(1);

        BasalDeliveryHistoryLog parsedRes = (BasalDeliveryHistoryLog) HistoryLogMessageTester.testSingle(
                "1711919923234ac80900020000008403e803ffff840300000000",
                expected
        );
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
        assertEquals(BasalDeliveryHistoryLog.CommandedRateSource.TEMP_RATE, parsedRes.getCommandedRateSourceEnum());
        assertEquals(0, parsedRes.getBasalDeliveryFlags());
        assertEquals(900, parsedRes.getCommandedRate());
        assertEquals(900, parsedRes.getTempRate());
    }

    @Test
    public void testBasalDeliveryHistoryLog_profileSegmentChange_flags3() throws DecoderException {
        // first record after the 08:00 profile segment change (1000 -> 1200 mU/hr) with a 90% temp rate active:
        // flags are 3 because profileBasalRate changed, with no temp-rate event in the interval
        BasalDeliveryHistoryLog expected = (BasalDeliveryHistoryLog) new BasalDeliveryHistoryLog(
            // long pumpTimeSec, long sequenceNum, int commandedRateSource, int basalDeliveryFlags, int commandedRate, int profileBasalRate, int algorithmRate, int tempRate
            589536234L, 641124L, 2, 3, 1080, 1200, 65535, 1080
        ).withHeaderHighNibble(1);

        BasalDeliveryHistoryLog parsedRes = (BasalDeliveryHistoryLog) HistoryLogMessageTester.testSingle(
                "1711ea9b232364c80900020003003804b004ffff380400000000",
                expected
        );
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
        assertEquals(BasalDeliveryHistoryLog.CommandedRateSource.TEMP_RATE, parsedRes.getCommandedRateSourceEnum());
        assertEquals(3, parsedRes.getBasalDeliveryFlags());
        assertEquals(1080, parsedRes.getCommandedRate());
        assertEquals(1200, parsedRes.getProfileBasalRate());
        assertEquals(1080, parsedRes.getTempRate());
    }

    @Test
    public void testBasalDeliveryHistoryLog_suspended_flags3() throws DecoderException {
        // suspended: commandedRateSource 0, commandedRate 0, algorithm/temp rate both "n/a" (0xffff), flags 3
        BasalDeliveryHistoryLog expected = (BasalDeliveryHistoryLog) new BasalDeliveryHistoryLog(
            // long pumpTimeSec, long sequenceNum, int commandedRateSource, int basalDeliveryFlags, int commandedRate, int profileBasalRate, int algorithmRate, int tempRate
            589553351L, 641943L, 0, 3, 0, 1000, 65535, 65535
        ).withHeaderHighNibble(1);

        BasalDeliveryHistoryLog parsedRes = (BasalDeliveryHistoryLog) HistoryLogMessageTester.testSingle(
                "1711c7de232397cb0900000003000000e803ffffffff00000000",
                expected
        );
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
        assertEquals(0, parsedRes.getCommandedRateSource());
        assertEquals(BasalDeliveryHistoryLog.CommandedRateSource.SUSPENDED, parsedRes.getCommandedRateSourceEnum());
        assertEquals(3, parsedRes.getBasalDeliveryFlags());
        assertEquals(0, parsedRes.getCommandedRate());
        assertEquals(1000, parsedRes.getProfileBasalRate());
        assertEquals(65535, parsedRes.getAlgorithmRate());
        assertEquals(65535, parsedRes.getTempRate());
    }

    @Test
    public void testCommandedRateSourceEnum() {
        assertEquals(BasalDeliveryHistoryLog.CommandedRateSource.SUSPENDED, BasalDeliveryHistoryLog.CommandedRateSource.fromId(0));
        assertEquals(BasalDeliveryHistoryLog.CommandedRateSource.PROFILE, BasalDeliveryHistoryLog.CommandedRateSource.fromId(1));
        assertEquals(BasalDeliveryHistoryLog.CommandedRateSource.TEMP_RATE, BasalDeliveryHistoryLog.CommandedRateSource.fromId(2));
        assertEquals(BasalDeliveryHistoryLog.CommandedRateSource.ALGORITHM, BasalDeliveryHistoryLog.CommandedRateSource.fromId(3));
        assertEquals(BasalDeliveryHistoryLog.CommandedRateSource.TEMP_RATE_AND_ALGORITHM, BasalDeliveryHistoryLog.CommandedRateSource.fromId(4));
        assertNull(BasalDeliveryHistoryLog.CommandedRateSource.fromId(5));
        assertNull(new BasalDeliveryHistoryLog(9, 0, 1000, 65535, 65535).getCommandedRateSourceEnum());
    }
}
