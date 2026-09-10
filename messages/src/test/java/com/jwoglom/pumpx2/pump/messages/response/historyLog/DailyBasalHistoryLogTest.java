package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;

import static org.junit.Assert.assertEquals;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.messages.bluetooth.CharacteristicUUID;
import com.jwoglom.pumpx2.pump.messages.response.historyLog.DailyBasalHistoryLog;
import com.jwoglom.pumpx2.shared.Hex;

import java.util.Arrays;
import org.apache.commons.codec.DecoderException;
import org.junit.Ignore;
import org.junit.Test;

import java.time.Instant;

public class DailyBasalHistoryLogTest {
    @Test
    public void testDailyBasalHistoryLog_0700() throws DecoderException {
        /**
         * {'actualBatteryCharge': '65',
         *  'dailyTotalBasal': '5.661252',
         *  'iob': '0.0',
         *  'lastBasalRate': '0.895',
         *  'lipoMv': '3904',
         *  'cargo': '{81',
         *  'pumpTimeSec': '445935621',
         *  'sequenceNum': '180238'}],
         */
        DailyBasalHistoryLog expected = new DailyBasalHistoryLog(
            // long pumpTimeSec, long sequenceNum, float dailyTotalBasal, float lastBasalRate, float iob, boolean isBatteryCharging, int actualBatteryCharge, int lipoMv
            445935621, 180238, 5.661252F, 0.895F, 0.0F, false, 65, 3904
        );

        DailyBasalHistoryLog parsedRes = (DailyBasalHistoryLog) HistoryLogMessageTester.testSingle(
                "51000570941a0ec00200fa28b540b81e653f000000000041400f",
                expected
        );
        assertEquals(Instant.parse("2022-02-17T07:00:21Z"), parsedRes.getPumpTimeSecInstant());
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }

    @Test
    public void testDailyBasalHistoryLog_0800() throws DecoderException {
        // 3601 seconds later (1 hour)
        /**
         * {'actualBatteryCharge': '64',
         *  'dailyTotalBasal': '6.2405014',
         *  'iob': '0.0',
         *  'lastBasalRate': '0.218',
         *  'lipoMv': '3901',
         *  'cargo': '{81',
         *  'pumpTimeSec': '445939222',
         *  'sequenceNum': '180317'}],
         */
        DailyBasalHistoryLog expected = new DailyBasalHistoryLog(
                // long pumpTimeSec, long sequenceNum, float dailyTotalBasal, float lastBasalRate, float iob, boolean isBatteryCharging, int actualBatteryCharge, int lipoMv
                445939222, 180317, 6.2405014F, 0.218F, 0.0F, false, 64, 3901
        );

        DailyBasalHistoryLog parsedRes = (DailyBasalHistoryLog) HistoryLogMessageTester.testSingle(
                "5100167e941a5dc0020030b2c740643b5f3e0000000000403d0f",
                expected
        );
        assertEquals(Instant.parse("2022-02-17T08:00:22Z"), parsedRes.getPumpTimeSecInstant());
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }

    @Test
    public void testDailyBasalHistoryLog_0900() throws DecoderException {
        // 3599 seconds later (59m59s)
        /**
         * {'actualBatteryCharge': '64',
         *  'dailyTotalBasal': '7.063834',
         *  'iob': '0.0',
         *  'lastBasalRate': '1.25',
         *  'lipoMv': '3898',
         *  'cargo': '{81',
         *  'pumpTimeSec': '445942821',
         *  'sequenceNum': '180397'}],
         */
        DailyBasalHistoryLog expected = new DailyBasalHistoryLog(
                // long pumpTimeSec, long sequenceNum, float dailyTotalBasal, float lastBasalRate, float iob, boolean isBatteryCharging, int actualBatteryCharge, int lipoMv
                445942821, 180397, 7.063834F, 1.25F, 0.0F, false, 64, 3898
        );

        DailyBasalHistoryLog parsedRes = (DailyBasalHistoryLog) HistoryLogMessageTester.testSingle(
                "5100258c941aadc00200ee0ae2400000a03f0000000000403a0f",
                expected
        );
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
        assertEquals(Instant.parse("2022-02-17T09:00:21Z"), parsedRes.getPumpTimeSecInstant());
    }

    @Test
    public void testDailyBasalHistoryLog_1000() throws DecoderException {
        // 3600 seconds later (1h)
        /**
         * {'actualBatteryCharge': '64',
         *  'dailyTotalBasal': '7.5640016',
         *  'iob': '0.0',
         *  'lastBasalRate': '0.366',
         *  'lipoMv': '3895',
         *  'cargo': '{81',
         *  'pumpTimeSec': '445946421',
         *  'sequenceNum': '180477'}],
         */
        DailyBasalHistoryLog expected = new DailyBasalHistoryLog(
                // long pumpTimeSec, long sequenceNum, float dailyTotalBasal, float lastBasalRate, float iob, boolean isBatteryCharging, int actualBatteryCharge, int lipoMv
                445946421, 180477, 7.5640016F, 0.366F, 0.0F, false, 64, 3895
        );

        DailyBasalHistoryLog parsedRes = (DailyBasalHistoryLog) HistoryLogMessageTester.testSingle(
                "5100359a941afdc002004d0cf2405a64bb3e000000000040370f",
                expected
        );
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
        assertEquals(Instant.parse("2022-02-17T10:00:21Z"), parsedRes.getPumpTimeSecInstant());
    }

    @Test
    public void testDailyBasalHistoryLog_1031() throws DecoderException {
        // 1899 seconds later (31m39s)
        /**
         * {'actualBatteryCharge': '63',
         *  'dailyTotalBasal': '8.036001',
         *  'iob': '0.0',
         *  'lastBasalRate': '1.25',
         *  'lipoMv': '3888',
         *  'cargo': '{81',
         *  'pumpTimeSec': '445948320',
         *  'sequenceNum': '180539'}],
         */
        DailyBasalHistoryLog expected = new DailyBasalHistoryLog(
                // long pumpTimeSec, long sequenceNum, float dailyTotalBasal, float lastBasalRate, float iob, boolean isBatteryCharging, int actualBatteryCharge, int lipoMv
                445948320, 180539, 8.036001F, 1.25F, 0.0F, false, 63, 3888
        );

        DailyBasalHistoryLog parsedRes = (DailyBasalHistoryLog) HistoryLogMessageTester.testSingle(
                "5100a0a1941a3bc10200769300410000a03f00000000003f300f",
                expected
        );
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
        assertEquals(Instant.parse("2022-02-17T10:32:00Z"), parsedRes.getPumpTimeSecInstant());
    }

    @Test
    public void testDailyBasalHistoryLog_1056() throws DecoderException {
        // 1461 seconds later (24m21s)
        /**
         * 'actualBatteryCharge': '63',
         * 'dailyTotalBasal': '8.577333',
         * 'iob': '8.102918E-4',
         * 'lastBasalRate': '1.25',
         * 'lipoMv': '3889',
         * 'cargo': '{81',
         * 'pumpTimeSec': '445949781',
         * 'sequenceNum': '180574'}],
         */
        DailyBasalHistoryLog expected = new DailyBasalHistoryLog(
                // long pumpTimeSec, long sequenceNum, float dailyTotalBasal, float lastBasalRate, float iob, boolean isBatteryCharging, int actualBatteryCharge, int lipoMv
                445949781, 180574, 8.577333F, 1.25F, 8.102918E-4F, false, 63, 3889
        );

        DailyBasalHistoryLog parsedRes = (DailyBasalHistoryLog) HistoryLogMessageTester.testSingle(
                "510055a7941a5ec10200c23c09410000a03fc369543a003f310f",
                expected
        );
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
        assertEquals(Instant.parse("2022-02-17T10:56:21Z"), parsedRes.getPumpTimeSecInstant());
    }


    @Test
    public void testDailyBasalHistoryLog_2340() throws DecoderException {
        /**
         * {'actualBatteryCharge': '55',
         *  'dailyTotalBasal': '15.212776',
         *  'iob': '3.2821338',
         *  'lastBasalRate': '0.14',
         *  'lipoMv': '3833',
         *  'cargo': '{81',
         *  'pumpTimeSec': '445995621',
         *  'sequenceNum': '181834'}],
         */
        DailyBasalHistoryLog expected = new DailyBasalHistoryLog(
                // long pumpTimeSec, long sequenceNum, float dailyTotalBasal, float lastBasalRate, float iob, boolean isBatteryCharging, int actualBatteryCharge, int lipoMv
                445995621, 181834, 15.212776F, 0.14F, 3.2821338F, false, 55, 3833
        );

        DailyBasalHistoryLog parsedRes = (DailyBasalHistoryLog) HistoryLogMessageTester.testSingle(
                "5100655a951a4ac6020088677341295c0f3e7b0e52400037f90e",
                expected
        );
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
        assertEquals(Instant.parse("2022-02-17T23:40:21Z"), parsedRes.getPumpTimeSecInstant());
    }

    @Test
    public void testDailyBasalHistoryLog_2350() throws DecoderException {
        // 601 seconds later (10m1s)
        /**
         * {'actualBatteryCharge': '55',
         *  'dailyTotalBasal': '15.260943',
         *  'iob': '2.9004815',
         *  'lastBasalRate': '0.8',
         *  'lipoMv': '3833',
         *  'cargo': '{81',
         *  'pumpTimeSec': '445996222',
         *  'sequenceNum': '181849'}],
         */
        DailyBasalHistoryLog expected = new DailyBasalHistoryLog(
                // long pumpTimeSec, long sequenceNum, float dailyTotalBasal, float lastBasalRate, float iob, boolean isBatteryCharging, int actualBatteryCharge, int lipoMv
                445996222, 181849, 15.260943F, 0.8F, 2.9004815F, false, 55, 3833
        );

        DailyBasalHistoryLog parsedRes = (DailyBasalHistoryLog) HistoryLogMessageTester.testSingle(
                "5100be5c951a59c60200d32c7441cdcc4c3f7da139400037f90e",
                expected
        );
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
        assertEquals(Instant.parse("2022-02-17T23:50:22Z"), parsedRes.getPumpTimeSecInstant());
    }

    @Test
    public void testDailyBasalHistoryLog_2358() throws DecoderException {
        // 480 seconds later (8m)
        /**
         * {'actualBatteryCharge': '55',
         *  'dailyTotalBasal': '15.394278',
         *  'iob': '2.7399874',
         *  'lastBasalRate': '0.8',
         *  'lipoMv': '3837',
         *  'cargo': '{81',
         *  'pumpTimeSec': '445996702',
         *  'sequenceNum': '181859'}],
         */
        DailyBasalHistoryLog expected = new DailyBasalHistoryLog(
                // long pumpTimeSec, long sequenceNum, float dailyTotalBasal, float lastBasalRate, float iob, boolean isBatteryCharging, int actualBatteryCharge, int lipoMv
                445996702, 181859, 15.394278F, 0.8F, 2.7399874F, true, 55, 3837
        );

        DailyBasalHistoryLog parsedRes = (DailyBasalHistoryLog) HistoryLogMessageTester.testSingle(
                "51009e5e951a63c60200f64e7641cdcc4c3ff45b2f400137fd0e",
                expected
        );
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
        assertEquals(Instant.parse("2022-02-17T23:58:22Z"), parsedRes.getPumpTimeSecInstant());
    }

    @Test
    public void testDailyBasalHistoryLog_0000() throws DecoderException {
        // 119 seconds later (1m59s)
        /**
         * {'actualBatteryCharge': '55',
         *  'dailyTotalBasal': '0.0',
         *  'iob': '2.5898643',
         *  'lastBasalRate': '0.8',
         *  'lipoMv': '3837',
         *  'cargo': '{81',
         *  'pumpTimeSec': '445996821',
         *  'sequenceNum': '181863'}],
         */
        DailyBasalHistoryLog expected = new DailyBasalHistoryLog(
                // long pumpTimeSec, long sequenceNum, float dailyTotalBasal, float lastBasalRate, float iob, boolean isBatteryCharging, int actualBatteryCharge, int lipoMv
                445996821, 181863, 0.0F, 0.8F, 2.5898643F, false, 55, 3837
        );

        DailyBasalHistoryLog parsedRes = (DailyBasalHistoryLog) HistoryLogMessageTester.testSingle(
                "5100155f951a67c6020000000000cdcc4c3f56c025400037fd0e",
                expected
        );
        assertEquals(Instant.parse("2022-02-18T00:00:21Z"), parsedRes.getPumpTimeSecInstant());
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }

    @Test
    public void testDailyBasalHistoryLog_0010() throws DecoderException {
        // 601 seconds later (10m1s)
        /**
         * {'actualBatteryCharge': '55',
         *  'dailyTotalBasal': '0.13333334',
         *  'iob': '2.2888513',
         *  'lastBasalRate': '0.8',
         *  'lipoMv': '3837',
         *  'cargo': '{81',
         *  'pumpTimeSec': '445997422',
         *  'sequenceNum': '181877'}],
         */
        DailyBasalHistoryLog expected = new DailyBasalHistoryLog(
                // long pumpTimeSec, long sequenceNum, float dailyTotalBasal, float lastBasalRate, float iob, boolean isBatteryCharging, int actualBatteryCharge, int lipoMv
                445997422, 181877, 0.13333334F, 0.8F, 2.2888513F, false, 55, 3837
        );

        DailyBasalHistoryLog parsedRes = (DailyBasalHistoryLog) HistoryLogMessageTester.testSingle(
                "51006e61951a75c602008988083ecdcc4c3f8a7c12400037fd0e",
                expected
        );
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
        assertEquals(Instant.parse("2022-02-18T00:10:22Z"), parsedRes.getPumpTimeSecInstant());
    }

    @Test
    public void testDailyBasalHistoryLog_0016() throws DecoderException {
        // 363 seconds later (6m3s)
        /**
         * {'actualBatteryCharge': '55',
         *  'dailyTotalBasal': '0.2',
         *  'iob': '2.1372654',
         *  'lastBasalRate': '0.8',
         *  'lipoMv': '3832',
         *  'cargo': '{81',
         *  'pumpTimeSec': '445997785',
         *  'sequenceNum': '181907'}],
         */
        DailyBasalHistoryLog expected = new DailyBasalHistoryLog(
                // long pumpTimeSec, long sequenceNum, float dailyTotalBasal, float lastBasalRate, float iob, boolean isBatteryCharging, int actualBatteryCharge, int lipoMv
                445997785, 181907, 0.2F, 0.8F, 2.1372654F, false, 55, 3832
        );

        DailyBasalHistoryLog parsedRes = (DailyBasalHistoryLog) HistoryLogMessageTester.testSingle(
                "5100d962951a93c60200cdcc4c3ecdcc4c3ff5c808400037f80e",
                expected
        );
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
        assertEquals(Instant.parse("2022-02-18T00:16:25Z"), parsedRes.getPumpTimeSecInstant());
    }

    @Test
    public void testBatteryChargeIsUnscaledPercent() throws DecoderException {
        // byte 23 is the pump's state-of-charge, 0-100, with no scaling applied
        DailyBasalHistoryLog parsedRes = (DailyBasalHistoryLog) HistoryLogMessageTester.testSingle(
                "51000570941a0ec00200fa28b540b81e653f000000000041400f",
                new DailyBasalHistoryLog(445935621, 180238, 5.661252F, 0.895F, 0.0F, false, 65, 3904)
        );
        assertEquals(65, parsedRes.getBatteryChargeRaw());
        assertEquals(65.0, parsedRes.getBatteryChargePercent(), 0.0);
    }

    @Test
    public void testLowBatteryChargeIsReportedAsLow() throws DecoderException {
        // a 20% battery must be reported as 20%, not as the 53.9% the previous scaling emitted
        DailyBasalHistoryLog expected = new DailyBasalHistoryLog(
                445935621, 180238, 5.661252F, 0.895F, 0.0F, false, 20, 3714
        );
        DailyBasalHistoryLog parsedRes = (DailyBasalHistoryLog) HistoryLogMessageTester.testSingle(
                Hex.encodeHexString(expected.getCargo()),
                expected
        );
        assertEquals(20, parsedRes.getBatteryChargeRaw());
        assertEquals(20.0, parsedRes.getBatteryChargePercent(), 0.0);
        assertEquals(3714, parsedRes.getLipoMv());
    }

    @Test
    public void testBatteryChargeAbove127IsReadUnsigned() throws DecoderException {
        // guards against the signed read of byte 23; outside the pump's real 0-100 range but a
        // signed read would return -1 rather than 255
        DailyBasalHistoryLog expected = new DailyBasalHistoryLog(
                445935621, 180238, 5.661252F, 0.895F, 0.0F, false, 255, 3904
        );
        DailyBasalHistoryLog parsedRes = (DailyBasalHistoryLog) HistoryLogMessageTester.testSingle(
                Hex.encodeHexString(expected.getCargo()),
                expected
        );
        assertEquals(255, parsedRes.getBatteryChargeRaw());
    }

    // Observed on a Tandem Mobi driven by Trio (Control-IQ off, no CGM paired), Sept 2026 BLE capture.
    // These three records have header high nibble 1. Float fields are built from their exact bit
    // patterns so the cargo round trip is byte-exact.
    //
    // finalEventForDay was set on two Daily Basal records written on the same evening (23:58:05 and
    // 23:58:48) ahead of a single NewDay at 00:00:00, so the flag marks every Daily Basal written in
    // the last minutes before the rollover rather than one closing record. The battery byte ran
    // 69-100 across the capture, tracking 2-4 points below the live CurrentBatteryV2Response value.

    @Test
    public void testDailyBasalHistoryLog_mobi_2358_05_finalEventForDay() throws DecoderException {
        // 23:58:05, first of two finalEventForDay=1 records that evening
        DailyBasalHistoryLog expected = (DailyBasalHistoryLog) new DailyBasalHistoryLog(
                // long pumpTimeSec, long sequenceNum, float dailyTotalBasal, float lastBasalRate, float iob, boolean finalEventForDay, int actualBatteryCharge, int lipoMv
                589593485L, 644069L,
                Float.intBitsToFloat(0x411c0e5b), // ~9.7535
                Float.intBitsToFloat(0x40200000), // 2.5
                Float.intBitsToFloat(0x40fa6e81), // ~7.826
                true, 92, 4104
        ).withHeaderHighNibble(1);

        DailyBasalHistoryLog parsedRes = (DailyBasalHistoryLog) HistoryLogMessageTester.testSingle(
                "51108d7b2423e5d309005b0e1c4100002040816efa40015c0810",
                expected
        );
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
        assertEquals(Instant.parse("2026-09-06T23:58:05Z"), parsedRes.getPumpTimeSecInstant());
        assertEquals(Float.intBitsToFloat(0x411c0e5b), parsedRes.getDailyTotalBasal(), 0.0F);
        assertEquals(2.5F, parsedRes.getLastBasalRate(), 0.0F);
        assertEquals(Float.intBitsToFloat(0x40fa6e81), parsedRes.getIob(), 0.0F);
        assertEquals(true, parsedRes.getFinalEventForDay());
        assertEquals(92, parsedRes.getBatteryChargeRaw());
        assertEquals(92.0, parsedRes.getBatteryChargePercent(), 0.0);
        assertEquals(4104, parsedRes.getLipoMv());
    }

    @Test
    public void testDailyBasalHistoryLog_mobi_2358_48_finalEventForDay() throws DecoderException {
        // 43 seconds later, same evening: finalEventForDay is still set and dailyTotalBasal is
        // unchanged, so the flag is not unique to a single closing record.
        DailyBasalHistoryLog expected = (DailyBasalHistoryLog) new DailyBasalHistoryLog(
                // long pumpTimeSec, long sequenceNum, float dailyTotalBasal, float lastBasalRate, float iob, boolean finalEventForDay, int actualBatteryCharge, int lipoMv
                589593528L, 644076L,
                Float.intBitsToFloat(0x411c0e5b), // ~9.7535
                Float.intBitsToFloat(0x40200000), // 2.5
                Float.intBitsToFloat(0x40f8a6ef), // ~7.770
                true, 92, 4104
        ).withHeaderHighNibble(1);

        DailyBasalHistoryLog parsedRes = (DailyBasalHistoryLog) HistoryLogMessageTester.testSingle(
                "5110b87b2423ecd309005b0e1c4100002040efa6f840015c0810",
                expected
        );
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
        assertEquals(Instant.parse("2026-09-06T23:58:48Z"), parsedRes.getPumpTimeSecInstant());
        assertEquals(Float.intBitsToFloat(0x411c0e5b), parsedRes.getDailyTotalBasal(), 0.0F);
        assertEquals(2.5F, parsedRes.getLastBasalRate(), 0.0F);
        assertEquals(Float.intBitsToFloat(0x40f8a6ef), parsedRes.getIob(), 0.0F);
        assertEquals(true, parsedRes.getFinalEventForDay());
        assertEquals(92, parsedRes.getBatteryChargeRaw());
        assertEquals(92.0, parsedRes.getBatteryChargePercent(), 0.0);
        assertEquals(4104, parsedRes.getLipoMv());
    }

    @Test
    public void testDailyBasalHistoryLog_mobi_0527_notFinalEventForDay() throws DecoderException {
        // 05:27:29, a mid-day record: finalEventForDay=0
        DailyBasalHistoryLog expected = (DailyBasalHistoryLog) new DailyBasalHistoryLog(
                // long pumpTimeSec, long sequenceNum, float dailyTotalBasal, float lastBasalRate, float iob, boolean finalEventForDay, int actualBatteryCharge, int lipoMv
                589526849L, 640759L,
                Float.intBitsToFloat(0x40224b16), // ~2.5358
                Float.intBitsToFloat(0x3f0ccccd), // 0.55
                Float.intBitsToFloat(0x3fb21f75), // ~1.3916
                false, 88, 4061
        ).withHeaderHighNibble(1);

        DailyBasalHistoryLog parsedRes = (DailyBasalHistoryLog) HistoryLogMessageTester.testSingle(
                "511041772323f7c60900164b2240cdcc0c3f751fb23f0058dd0f",
                expected
        );
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
        assertEquals(Instant.parse("2026-09-06T05:27:29Z"), parsedRes.getPumpTimeSecInstant());
        assertEquals(Float.intBitsToFloat(0x40224b16), parsedRes.getDailyTotalBasal(), 0.0F);
        assertEquals(0.55F, parsedRes.getLastBasalRate(), 0.0F);
        assertEquals(Float.intBitsToFloat(0x3fb21f75), parsedRes.getIob(), 0.0F);
        assertEquals(false, parsedRes.getFinalEventForDay());
        assertEquals(88, parsedRes.getBatteryChargeRaw());
        assertEquals(88.0, parsedRes.getBatteryChargePercent(), 0.0);
        assertEquals(4061, parsedRes.getLipoMv());
    }
}
