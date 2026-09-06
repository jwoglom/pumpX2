package com.jwoglom.pumpx2.pump.messages.response.control;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class SetConfigurableFeatureResponseTest {
    @Test
    public void testSetConfigurableFeatureResponse_success() throws DecoderException {
        SetConfigurableFeatureResponse expected = new SetConfigurableFeatureResponse(0, 0);

        SetConfigurableFeatureResponse parsedRes = new SetConfigurableFeatureResponse();
        parsedRes.parse(expected.getCargo());

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
        assertEquals(2, parsedRes.getCargo().length);
        assertEquals(0, parsedRes.getStatus());
        assertEquals(0, parsedRes.getReason());
        assertTrue(parsedRes.isStatusOK());
    }

    @Test
    public void testSetConfigurableFeatureResponse_error() throws DecoderException {
        SetConfigurableFeatureResponse expected = new SetConfigurableFeatureResponse(1, 3);

        SetConfigurableFeatureResponse parsedRes = new SetConfigurableFeatureResponse();
        parsedRes.parse(expected.getCargo());

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
        assertEquals(1, parsedRes.getStatus());
        assertEquals(3, parsedRes.getReason());
    }
}
