package com.jwoglom.pumpx2.pump.messages.response;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.bluetooth.CharacteristicUUID;
import com.jwoglom.pumpx2.pump.messages.response.CurrentBatteryV1Response;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class CurrentBatteryV1ResponseTest {
    @Test
    public void testCurrentBatteryV1Response() throws DecoderException {
        CurrentBatteryV1Response expected = new CurrentBatteryV1Response(
            // int currentBatteryAbc, int currentBatteryIbc
                99, 100
        );

        CurrentBatteryV1Response parsedRes = (CurrentBatteryV1Response) MessageTester.test(
                "0003350302636452b9",
                3,
                1,
                CharacteristicUUID.CURRENT_STATUS_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }

    @Test
    public void testCurrentBatteryV1Response2() throws DecoderException {
        CurrentBatteryV1Response expected = new CurrentBatteryV1Response(
                // int currentBatteryAbc, int currentBatteryIbc
                100, 100
        );

        CurrentBatteryV1Response parsedRes = (CurrentBatteryV1Response) MessageTester.test(
                "00043504026464e871",
                4,
                1,
                CharacteristicUUID.CURRENT_STATUS_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }
}