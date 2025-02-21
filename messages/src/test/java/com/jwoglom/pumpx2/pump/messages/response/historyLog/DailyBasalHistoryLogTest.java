package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;

import static org.junit.Assert.assertEquals;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.messages.bluetooth.CharacteristicUUID;
import com.jwoglom.pumpx2.pump.messages.response.historyLog.DailyBasalHistoryLog;

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
}