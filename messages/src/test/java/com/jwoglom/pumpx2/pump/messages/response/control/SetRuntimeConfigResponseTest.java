package com.jwoglom.pumpx2.pump.messages.response.control;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class SetRuntimeConfigResponseTest {
    @Test
    public void testSetRuntimeConfigResponse_success() throws DecoderException {
        SetRuntimeConfigResponse expected = new SetRuntimeConfigResponse(0);

        SetRuntimeConfigResponse parsedRes = new SetRuntimeConfigResponse();
        parsedRes.parse(expected.getCargo());

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
        assertEquals(1, parsedRes.getCargo().length);
        assertEquals(0, parsedRes.getStatus());
        assertTrue(parsedRes.isStatusOK());
    }

    @Test
    public void testSetRuntimeConfigResponse_error() throws DecoderException {
        SetRuntimeConfigResponse expected = new SetRuntimeConfigResponse(1);

        SetRuntimeConfigResponse parsedRes = new SetRuntimeConfigResponse();
        parsedRes.parse(expected.getCargo());

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
        assertEquals(1, parsedRes.getStatus());
    }
}
