package com.jwoglom.pumpx2.pump.messages.response.currentStatus;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.messages.bluetooth.CharacteristicUUID;

import org.apache.commons.codec.DecoderException;
import org.junit.Ignore;
import org.junit.Test;

public class CurrentBatteryV2ResponseTest {
    /** V2.5 API: fully charged */
    @Test
    @Ignore("need to fix duplicate UUID across UUIDs: https://github.com/jwoglom/pumpX2/issues/2")
    public void testCurrentBatteryV2ResponseFull() throws DecoderException {
        CurrentBatteryV2Response expected = new CurrentBatteryV2Response(
            // int currentBatteryAbc, int currentBatteryIbc, int chargingStatus, int unknown1, int unknown2, int unknown3, int unknown4
                100, 100, 0, 0, 0, 0, 0
        );

        CurrentBatteryV2Response parsedRes = (CurrentBatteryV2Response) MessageTester.test(
                "000d910d0b6464000000000000000000eb4f",
                13,
                1,
                CharacteristicUUID.CURRENT_STATUS_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }

    /** V2.5 API: pump displays battery at 10% */
    @Test
    @Ignore("need to fix duplicate UUID across UUIDs: https://github.com/jwoglom/pumpX2/issues/2")
    public void testCurrentBatteryV2Response() throws DecoderException {
        CurrentBatteryV2Response expected = new CurrentBatteryV2Response(
                // int currentBatteryAbc, int currentBatteryIbc, int chargingStatus, int unknown1, int unknown2, int unknown3, int unknown4
                27, 10, 0, 0, 0, 0, 0
        );

        CurrentBatteryV2Response parsedRes = (CurrentBatteryV2Response) MessageTester.test(
                "000691060b1b0a00000000000000000056c9",
                6,
                1,
                CharacteristicUUID.CURRENT_STATUS_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }

    /** V2.5 API: pump displays battery at 10%, charging */
    @Test
    @Ignore("need to fix duplicate UUID across UUIDs: https://github.com/jwoglom/pumpX2/issues/2")
    public void testCurrentBatteryV2ResponseCharging() throws DecoderException {
        CurrentBatteryV2Response expected = new CurrentBatteryV2Response(
                // int currentBatteryAbc, int currentBatteryIbc, int chargingStatus, int unknown1, int unknown2, int unknown3, int unknown4
                27, 10, 1, 0, 0, 0, 0
        );

        CurrentBatteryV2Response parsedRes = (CurrentBatteryV2Response) MessageTester.test(
                "000791070b1b0a0100000000000000001667",
                7,
                1,
                CharacteristicUUID.CURRENT_STATUS_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }
}