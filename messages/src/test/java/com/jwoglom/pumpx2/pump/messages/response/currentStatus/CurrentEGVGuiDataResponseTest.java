package com.jwoglom.pumpx2.pump.messages.response.currentStatus;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;

import static org.junit.Assert.assertEquals;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.messages.bluetooth.CharacteristicUUID;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class CurrentEGVGuiDataResponseTest {
    @Test
    public void testCurrentEGVGuiDataResponseNoCGM() throws DecoderException {
        CurrentEGVGuiDataResponse expected = new CurrentEGVGuiDataResponse(
            // long bgReadingTimestampSeconds, int cgmReading, int egvStatus, int trendRate
                0L, 0, CurrentEGVGuiDataResponse.EGVStatus.UNAVAILABLE.id(), 0
        );

        CurrentEGVGuiDataResponse parsedRes = (CurrentEGVGuiDataResponse) MessageTester.test(
                "000323030800000000000004007b52",
                3,
                1,
                CharacteristicUUID.CURRENT_STATUS_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
        assertEquals(CurrentEGVGuiDataResponse.EGVStatus.UNAVAILABLE, parsedRes.getEgvStatus());
    }


    @Test
    public void testCurrentEGVGuiDataResponse_123_negative1Delta() throws DecoderException {
        // CurrentEGVGuiDataResponse[bgReadingTimestampSeconds=471039176,cgmReading=123,egvStatus=1,trendRate=-1]
        CurrentEGVGuiDataResponse expected = new CurrentEGVGuiDataResponse(
                // long bgReadingTimestampSeconds, int cgmReading, int egvStatus, int trendRate
                471039176L, 123, CurrentEGVGuiDataResponse.EGVStatus.VALID.id(), -1
        );

        CurrentEGVGuiDataResponse parsedRes = (CurrentEGVGuiDataResponse) MessageTester.test(
                "0032233208c87c131c7b0001ff5eb5",
                50,
                1,
                CharacteristicUUID.CURRENT_STATUS_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }

    @Test
    public void testCurrentEGVGuiDataResponse_122_negative1Delta() throws DecoderException {
        // CurrentEGVGuiDataResponse[bgReadingTimestampSeconds=471039474,cgmReading=122,egvStatus=1,trendRate=-1]
        CurrentEGVGuiDataResponse expected = new CurrentEGVGuiDataResponse(
                // long bgReadingTimestampSeconds, int cgmReading, int egvStatus, int trendRate
                471039474L, 122, CurrentEGVGuiDataResponse.EGVStatus.VALID.id(), -1
        );

        CurrentEGVGuiDataResponse parsedRes = (CurrentEGVGuiDataResponse) MessageTester.test(
                "004c234c08f27d131c7a0001ff5e4a",
                76,
                1,
                CharacteristicUUID.CURRENT_STATUS_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }
}