package com.jwoglom.pumpx2.pump.messages.response.control;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;
import static com.jwoglom.pumpx2.pump.messages.MessageTester.initPumpState;
import com.jwoglom.pumpx2.pump.messages.PacketArrayList;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.messages.bluetooth.CharacteristicUUID;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class SetIDPSettingsResponseTest {
    @Test
    public void testSetIDPSettingsResponse_1() throws DecoderException {
        initPumpState(PacketArrayList.IGNORE_INVALID_HMAC, 0L);
        
        SetIDPSettingsResponse expected = new SetIDPSettingsResponse(
            0
        );

        SetIDPSettingsResponse parsedRes = (SetIDPSettingsResponse) MessageTester.test(
                "0009ad091a00020e033e2061ea619a9325f6bd309c77dc8c496c9c352a4da72237",
                9,
                1,
                CharacteristicUUID.CONTROL_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }


    @Test
    public void testSetIDPSettingsResponse_2() throws DecoderException {
        initPumpState(PacketArrayList.IGNORE_INVALID_HMAC, 0L);

        SetIDPSettingsResponse expected = new SetIDPSettingsResponse(
                0
        );

        SetIDPSettingsResponse parsedRes = (SetIDPSettingsResponse) MessageTester.test(
                "0000ad001a000200033e208cb5056600c4b9485675759170d5b20c687e24c64bea",
                0,
                1,
                CharacteristicUUID.CONTROL_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }
}