package com.jwoglom.pumpx2.pump.messages.response.currentStatus;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;

import static org.junit.Assert.assertEquals;

import com.google.common.collect.ImmutableSet;
import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.messages.bluetooth.CharacteristicUUID;
import com.jwoglom.pumpx2.pump.messages.response.currentStatus.LastBolusStatusV2Response;
import com.jwoglom.pumpx2.pump.messages.response.historyLog.BolusDeliveryHistoryLog;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class LastBolusStatusV2ResponseTest {
    @Test
    public void testLastBolusStatusV2ResponseEmpty() throws DecoderException {
        LastBolusStatusV2Response expected = new LastBolusStatusV2Response(
            // int bit0, int short1, long uint5, long uint9, int bit13, int bit14, int bit15, long uint16, long uint20
                0, 0, 0, 0, 0, 0, 0, 0, 0
        );

        LastBolusStatusV2Response parsedRes = (LastBolusStatusV2Response) MessageTester.test(
                "0004a504180000000000000000000000000000000000000000000000008521",
                4,
                2,
                CharacteristicUUID.CURRENT_STATUS_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }

    @Test
    public void testLastBolusStatusV2ResponsePresent() throws DecoderException {
        // completed timestamp: 2:27PM
        // started timestamp: 2:24PM
        LastBolusStatusV2Response expected = new LastBolusStatusV2Response(
                // int bit0, int short1, long uint5, long uint9, int bit13, int bit14, int bit15, long uint16, long uint20
                1, 3240, 458576820, 6670, 3, 1, 1, 0, 6670
        );

        LastBolusStatusV2Response parsedRes = (LastBolusStatusV2Response) MessageTester.test(
                "0004a5041801a80c0000b453551b0e1a0000030101000000000e1a0000b898",
                4,
                2,
                CharacteristicUUID.CURRENT_STATUS_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());

        assertEquals(LastBolusStatusV2Response.BolusStatus.COMPLETE, parsedRes.getBolusStatus());
        assertEquals(BolusDeliveryHistoryLog.BolusSource.GUI, parsedRes.getBolusSource());
        assertEquals(ImmutableSet.of(BolusDeliveryHistoryLog.BolusType.FOOD1), parsedRes.getBolusType());
    }

    @Test
    public void testLastBolusStatusV2Response05uStopped() throws DecoderException {
        // stopped timestamp: 5:09PM
        // started timestamp: 5:08PM
        LastBolusStatusV2Response expected = new LastBolusStatusV2Response(
                // int bit0, int short1, long uint5, long uint9, int bit13, int bit14, int bit15, long uint16, long uint20
                1, 3242, 458586556, 50, 0, 1, 8, 0, 50
        );

        LastBolusStatusV2Response parsedRes = (LastBolusStatusV2Response) MessageTester.test(
                "0007a5071801aa0c0000bc79551b320000000001080000000032000000893b",
                7,
                2,
                CharacteristicUUID.CURRENT_STATUS_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());

        assertEquals(LastBolusStatusV2Response.BolusStatus.STOPPED, parsedRes.getBolusStatus());
        assertEquals(BolusDeliveryHistoryLog.BolusSource.GUI, parsedRes.getBolusSource());
        assertEquals(ImmutableSet.of(BolusDeliveryHistoryLog.BolusType.FOOD2), parsedRes.getBolusType());
    }

    @Test
    public void testLastBolusStatusV2Response05uCompleted() throws DecoderException {
        // completed timestamp: 5:11PM
        // started timestamp: 5:10PM
        LastBolusStatusV2Response expected = new LastBolusStatusV2Response(
                // int bit0, int short1, long uint5, long uint9, int bit13, int bit14, int bit15, long uint16, long uint20
                1, 3243, 458586676, 50, 3, 1, 8, 0, 50
        );

        LastBolusStatusV2Response parsedRes = (LastBolusStatusV2Response) MessageTester.test(
                "0008a5081801ab0c0000347a551b3200000003010800000000320000001462",
                8,
                2,
                CharacteristicUUID.CURRENT_STATUS_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());

        assertEquals(LastBolusStatusV2Response.BolusStatus.COMPLETE, parsedRes.getBolusStatus());
        assertEquals(BolusDeliveryHistoryLog.BolusSource.GUI, parsedRes.getBolusSource());
        assertEquals(ImmutableSet.of(BolusDeliveryHistoryLog.BolusType.FOOD2), parsedRes.getBolusType());
    }

    @Test
    public void testLastBolusStatusV2ResponsePartialStop() throws DecoderException {
        LastBolusStatusV2Response expected = new LastBolusStatusV2Response(
                // int bit0, int short1, long uint5, long uint9, int bit13, int bit14, int bit15, long uint16, long uint20
                1, 3245, 458594698, 1975, 0, 1, 8, 0, 2500
        );

        LastBolusStatusV2Response parsedRes = (LastBolusStatusV2Response) MessageTester.test(
                "0004a5041801ad0c00008a99551bb707000000010800000000c4090000bc9e",
                4,
                2,
                CharacteristicUUID.CURRENT_STATUS_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());

        assertEquals(LastBolusStatusV2Response.BolusStatus.STOPPED, parsedRes.getBolusStatus());
        assertEquals(BolusDeliveryHistoryLog.BolusSource.GUI, parsedRes.getBolusSource());
        assertEquals(ImmutableSet.of(BolusDeliveryHistoryLog.BolusType.FOOD2), parsedRes.getBolusType());
    }

    @Test
    public void testLastBolusStatusV2ResponseExtendedBolusFirstPartDeliveredSecondInProgress() throws DecoderException {
        // 2 units, 75% deliver now, 25% later, 2 hours
        // Only the first chunk of the extended bolus appears, while the ext bolus is in progress extendedBolusDuration is 0
        LastBolusStatusV2Response expected = new LastBolusStatusV2Response(
                // int bit0, int short1, long uint5, long uint9, int bit13, int bit14, int bit15, long uint16, long uint20
                1, 3248, 458595314, 1500, 3, 1, 8, 0, 1500
        );

        LastBolusStatusV2Response parsedRes = (LastBolusStatusV2Response) MessageTester.test(
                "0010a5101801b00c0000f29b551bdc05000003010800000000dc050000c546",
                16,
                2,
                CharacteristicUUID.CURRENT_STATUS_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());

        assertEquals(LastBolusStatusV2Response.BolusStatus.COMPLETE, parsedRes.getBolusStatus());
        assertEquals(BolusDeliveryHistoryLog.BolusSource.GUI, parsedRes.getBolusSource());
        assertEquals(ImmutableSet.of(BolusDeliveryHistoryLog.BolusType.FOOD2), parsedRes.getBolusType());
    }

    @Test
    public void testLastBolusStatusV2ResponseExtendedBolusAfterStop() throws DecoderException {
        // 2 units, 75% deliver now, 25% later, 2 hours
        // Extended bolus was stopped at 0.02u / 0.5u
        // The first component of the delivered bolus doesn't appear here, and it has the same bolus ID (?)
        LastBolusStatusV2Response expected = new LastBolusStatusV2Response(
                // int bit0, int short1, long uint5, long uint9, int bit13, int bit14, int bit15, long uint16, long uint20
                1, 3248, 458595482, 21, 0, 1, 4, 168, 500
        );

        LastBolusStatusV2Response parsedRes = (LastBolusStatusV2Response) MessageTester.test(
                "0016a5161801b00c00009a9c551b15000000000104a8000000f40100003714",
                22,
                2,
                CharacteristicUUID.CURRENT_STATUS_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());

        assertEquals(LastBolusStatusV2Response.BolusStatus.STOPPED, parsedRes.getBolusStatus());
        assertEquals(BolusDeliveryHistoryLog.BolusSource.GUI, parsedRes.getBolusSource());
        assertEquals(ImmutableSet.of(BolusDeliveryHistoryLog.BolusType.EXTENDED), parsedRes.getBolusType());
    }

    @Test
    public void testLastBolusStatusV2ResponseQuickBolusCancelled() throws DecoderException {
        // Quick bolus which was cancelled after delivering the entire bolus
        LastBolusStatusV2Response expected = new LastBolusStatusV2Response(
                // int bit0, int short1, long uint5, long uint9, int bit13, int bit14, int bit15, long uint16, long uint20
                1, 3251, 458597185, 500, 0, 0, 1, 0, 500
        );

        LastBolusStatusV2Response parsedRes = (LastBolusStatusV2Response) MessageTester.test(
                "0019a5191801b30c000041a3551bf401000000000100000000f4010000afcc",
                25,
                2,
                CharacteristicUUID.CURRENT_STATUS_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());

        assertEquals(LastBolusStatusV2Response.BolusStatus.STOPPED, parsedRes.getBolusStatus());
        assertEquals(BolusDeliveryHistoryLog.BolusSource.QUICK_BOLUS, parsedRes.getBolusSource());
        assertEquals(ImmutableSet.of(BolusDeliveryHistoryLog.BolusType.FOOD1), parsedRes.getBolusType());
    }


    @Test
    public void testLastBolusStatusV2ResponseControlIQAutoBolus() throws DecoderException {
        LastBolusStatusV2Response expected = new LastBolusStatusV2Response(
                // int bit0, int short1, long uint5, long uint9, int bit13, int bit14, int bit15, long uint16, long uint20
                1, 3274, 458765005, 1585, 3, 7, 2, 0, 1585
        );

        LastBolusStatusV2Response parsedRes = (LastBolusStatusV2Response) MessageTester.test(
                "0004a5041801ca0c0000cd32581b310600000307020000000031060000a8ca",
                25,
                2,
                CharacteristicUUID.CURRENT_STATUS_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());

        assertEquals(LastBolusStatusV2Response.BolusStatus.STOPPED, parsedRes.getBolusStatus());
        assertEquals(BolusDeliveryHistoryLog.BolusSource.CONTROL_IQ_AUTO_BOLUS, parsedRes.getBolusSource());
        assertEquals(ImmutableSet.of(BolusDeliveryHistoryLog.BolusType.CORRECTION), parsedRes.getBolusType());
    }
}