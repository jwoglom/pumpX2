package com.jwoglom.pumpx2.pump.messages.request.currentStatus;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.messages.bluetooth.CharacteristicUUID;

import org.apache.commons.codec.DecoderException;
import org.junit.Ignore;
import org.junit.Test;

public class CurrentBatteryV2RequestTest {
    @Test
    @Ignore("need to fix duplicate UUID across UUIDs: https://github.com/jwoglom/pumpX2/issues/2")
    public void testCurrentBatteryV2Request() throws DecoderException {
        // empty cargo
        CurrentBatteryV2Request expected = new CurrentBatteryV2Request();

        CurrentBatteryV2Request parsedReq = (CurrentBatteryV2Request) MessageTester.test(
                "0005900500504b",
                5,
                1,
                CharacteristicUUID.CURRENT_STATUS_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedReq.getCargo());
    }
}