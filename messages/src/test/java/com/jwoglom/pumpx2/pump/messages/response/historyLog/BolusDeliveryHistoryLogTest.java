package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;

import java.util.Set;
import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.messages.bluetooth.CharacteristicUUID;

import com.jwoglom.pumpx2.pump.messages.response.historyLog.BolusDeliveryHistoryLog.BolusSource;
import com.jwoglom.pumpx2.pump.messages.response.historyLog.BolusDeliveryHistoryLog.BolusType;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class BolusDeliveryHistoryLogTest {
    @Test
    public void testBolusDeliveryHistoryLog1() throws DecoderException {
        BolusDeliveryHistoryLog expected = new BolusDeliveryHistoryLog(
            // long pumpTimeSec, long sequenceNum, int bolusID, int bolusDeliveryStatus, int bolusType, int bolusSource, int reserved, int requestedNow, int requestedLater, int correction, int extendedDurationRequested, int deliveredTotal
                446066139L, 183687L,
                1044,
                0,
                Set.of(BolusType.FOOD1, BolusType.FOOD2),
                BolusSource.CONTROL_IQ_AUTO_BOLUS,
                20,
                1001,
                0,
                1001,
                0,
                1001
        );

        BolusDeliveryHistoryLog parsedRes = (BolusDeliveryHistoryLog) HistoryLogMessageTester.testSingle(
                "1801db6d961a87cd0200140400090714e9030000e9030000e903",
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }

    @Test
    public void testBolusDeliveryHistoryLog2() throws DecoderException {
        BolusDeliveryHistoryLog expected = new BolusDeliveryHistoryLog(
                // long pumpTimeSec, long sequenceNum, int bolusID, int bolusDeliveryStatus, int bolusType, int bolusSource, int reserved, int requestedNow, int requestedLater, int correction, int extendedDurationRequested, int deliveredTotal
                446175714L, 187031L,
                1063,
                0,
                Set.of(BolusType.FOOD1, BolusType.FOOD2),
                BolusSource.GUI,
                39,
                210,
                0,
                210,
                0,
                210
        );

        BolusDeliveryHistoryLog parsedRes = (BolusDeliveryHistoryLog) HistoryLogMessageTester.testSingle(
                "1801e219981a97da0200270400090127d2000000d2000000d200",
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }

    @Test
    public void testBolusDeliveryHistoryLog3() throws DecoderException {
        BolusDeliveryHistoryLog expected = new BolusDeliveryHistoryLog(
                // long pumpTimeSec, long sequenceNum, int bolusID, int bolusDeliveryStatus, int bolusType, int bolusSource, int reserved, int requestedNow, int requestedLater, int correction, int extendedDurationRequested, int deliveredTotal
                446175382L, 186993L,
                1062,
                1,
                Set.of(BolusType.FOOD1, BolusType.FOOD2),
                BolusSource.GUI,
                38,
                920,
                0,
                920,
                0,
                0
        );

        BolusDeliveryHistoryLog parsedRes = (BolusDeliveryHistoryLog) HistoryLogMessageTester.testSingle(
                "18019618981a71da020026040109012698030000980300000000",
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }
}