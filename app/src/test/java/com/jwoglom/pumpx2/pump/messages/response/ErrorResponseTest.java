package com.jwoglom.pumpx2.pump.messages.response;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;
import static org.junit.Assert.assertEquals;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.bluetooth.CharacteristicUUID;
import com.jwoglom.pumpx2.pump.messages.response.currentStatus.PumpSettingsResponse;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class ErrorResponseTest {
    @Test
    public void testErrorResponseBadOpcode() throws DecoderException {
        ErrorResponse expected = new ErrorResponse(
                -112, ErrorResponse.ErrorCode.BAD_OPCODE.id()
        );

        ErrorResponse parsedRes = (ErrorResponse) MessageTester.test(
                "00054d05029006168b",
                5,
                1,
                CharacteristicUUID.CURRENT_STATUS_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }

    @Test
    public void testErrorResponseInvalidParameter() throws DecoderException {
        ErrorResponse expected = new ErrorResponse(
                66, ErrorResponse.ErrorCode.INVALID_REQUIRED_PARAMETER.id()
        );

        ErrorResponse parsedRes = (ErrorResponse) MessageTester.test(
                "00084d08024207f4d1",
                8,
                1,
                CharacteristicUUID.CURRENT_STATUS_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }
}