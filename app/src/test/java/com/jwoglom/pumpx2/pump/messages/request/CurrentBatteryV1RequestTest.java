package com.jwoglom.pumpx2.pump.messages.request;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.bluetooth.CharacteristicUUID;
import com.jwoglom.pumpx2.pump.messages.request.CurrentBatteryV1Request;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class CurrentBatteryV1RequestTest {
    @Test
    public void testCurrentBatteryV1Request() throws DecoderException {
        // empty cargo
        CurrentBatteryV1Request expected = new CurrentBatteryV1Request();

        CurrentBatteryV1Request parsedReq = (CurrentBatteryV1Request) MessageTester.test(
                "0003340300aa80",
                3,
                1,
                CharacteristicUUID.CURRENT_STATUS_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedReq.getCargo());
    }
}