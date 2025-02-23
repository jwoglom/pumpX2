package com.jwoglom.pumpx2.pump.messages.response.currentStatus;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.messages.bluetooth.CharacteristicUUID;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class UnknownMobiOpcode20ResponseTest {
    @Test
    public void testUnknownMobiOpcode20Response_beforeChangeCartridge1() throws DecoderException {
        UnknownMobiOpcode20Response expected = new UnknownMobiOpcode20Response(
            0, 260
        );

        UnknownMobiOpcode20Response parsedRes = (UnknownMobiOpcode20Response) MessageTester.test(
                "0082158203000401ff76",
                -126,
                1,
                CharacteristicUUID.CURRENT_STATUS_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }

    @Test
    public void testUnknownMobiOpcode20Response_beforeChangeCartridge2() throws DecoderException {
        UnknownMobiOpcode20Response expected = new UnknownMobiOpcode20Response(
                0, 5
        );

        UnknownMobiOpcode20Response parsedRes = (UnknownMobiOpcode20Response) MessageTester.test(
                "005c155c03000500a5ad",
                92,
                1,
                CharacteristicUUID.CURRENT_STATUS_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }



    @Test
    public void testUnknownMobiOpcode20Response_afterFillTubing1() throws DecoderException {
        UnknownMobiOpcode20Response expected = new UnknownMobiOpcode20Response(
                1, 770
        );

        UnknownMobiOpcode20Response parsedRes = (UnknownMobiOpcode20Response) MessageTester.test(
                "00771577030102032ad7",
                119,
                1,
                CharacteristicUUID.CURRENT_STATUS_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }

    @Test
    public void testUnknownMobiOpcode20Response_beforeChangeCart() throws DecoderException {
        UnknownMobiOpcode20Response expected = new UnknownMobiOpcode20Response(
                0, 3
        );

        UnknownMobiOpcode20Response parsedRes = (UnknownMobiOpcode20Response) MessageTester.test(
                "0024152403000300a818",
                36,
                1,
                CharacteristicUUID.CURRENT_STATUS_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }


}