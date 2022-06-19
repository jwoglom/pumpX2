package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;

import com.google.common.collect.ImmutableSet;
import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.bluetooth.CharacteristicUUID;
import com.jwoglom.pumpx2.pump.messages.response.historyLog.BolusDeliveryHistoryLog;

import com.google.common.collect.ImmutableList;
import com.jwoglom.pumpx2.pump.messages.response.historyLog.BolusDeliveryHistoryLog.BolusSource;
import com.jwoglom.pumpx2.pump.messages.response.historyLog.BolusDeliveryHistoryLog.BolusType;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class BolusDeliveryHistoryLogTest {
    @Test
    public void testBolusDeliveryHistoryLog() throws DecoderException {
        BolusDeliveryHistoryLog expected = new BolusDeliveryHistoryLog(
            // int bolusID, int bolusDeliveryStatus, int bolusType, int bolusSource, int reserved, int requestedNow, int requestedLater, int correction, int extendedDurationRequested, int deliveredTotal
                1044,
                0,
                ImmutableSet.of(BolusType.FOOD1, BolusType.FOOD2),
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
    }
}