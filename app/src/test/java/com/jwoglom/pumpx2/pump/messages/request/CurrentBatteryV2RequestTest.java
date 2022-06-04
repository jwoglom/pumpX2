package com.jwoglom.pumpx2.pump.messages.request;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.bluetooth.CharacteristicUUID;
import com.jwoglom.pumpx2.pump.messages.request.CurrentBatteryV2Request;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class CurrentBatteryV2RequestTest {
    @Test
    public void testCurrentBatteryV2Request() throws DecoderException {
        // empty cargo
        CurrentBatteryV2Request expected = new CurrentBatteryV2Request();

        CurrentBatteryV2Request parsedReq = (CurrentBatteryV2Request) MessageTester.test(
                "xxxx",
                2,
                1,
                CharacteristicUUID.CURRENT_STATUS_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedReq.getCargo());
    }
}