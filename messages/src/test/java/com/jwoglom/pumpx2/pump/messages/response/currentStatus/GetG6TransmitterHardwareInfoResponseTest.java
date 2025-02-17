package com.jwoglom.pumpx2.pump.messages.response.currentStatus;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.messages.bluetooth.CharacteristicUUID;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class GetG6TransmitterHardwareInfoResponseTest {
    @Test
    public void testUnknownMobiOpcodeNeg60Response() throws DecoderException {
        GetG6TransmitterHardwareInfoResponse expected = new GetG6TransmitterHardwareInfoResponse(
            new byte[]{
                    50,
                    46,
                    50,
                    55,
                    46,
                    50,
                    46,
                    49,
                    48,
                    51,
                    0,
                    0,
                    0,
                    0,
                    0,
                    0,
                    48,
                    120,
                    70,
                    70,
                    0,
                    50,
                    46,
                    49,
                    48,
                    51,
                    0,
                    0,
                    0,
                    0,
                    0,
                    0,
                    49,
                    54,
                    55,
                    48,
                    53,
                    0,
                    46,
                    49,
                    48,
                    51,
                    0,
                    0,
                    0,
                    0,
                    0,
                    0,
                    83,
                    87,
                    49,
                    50,
                    48,
                    57,
                    55,
                    0,
                    48,
                    51,
                    0,
                    0,
                    0,
                    0,
                    0,
                    0,
                    0,
                    0,
                    0,
                    0,
                    0,
                    0,
                    0,
                    0,
                    0,
                    0,
                    0,
                    0,
                    0,
                    0,
                    0,
                    0,
                    0,
                    0,
                    0,
                    0,
                    0,
                    0,
                    0,
                    0,
                    0,
                    0,
                    0,
                    0,
                    0,
                    0,
                    0,
                    0
            }
        );

        GetG6TransmitterHardwareInfoResponse parsedRes = (GetG6TransmitterHardwareInfoResponse) MessageTester.test(
                "001ac51a60322e32372e322e3130330000000000003078464600322e3130330000000000003136373035002e3130330000000000005357313230393700303300000000000000000000000000000000000000000000000000000000000000000000000000004b61",
                26,
                6,
                CharacteristicUUID.CURRENT_STATUS_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }
}