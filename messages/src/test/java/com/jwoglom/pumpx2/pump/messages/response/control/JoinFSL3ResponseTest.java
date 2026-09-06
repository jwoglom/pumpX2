package com.jwoglom.pumpx2.pump.messages.response.control;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class JoinFSL3ResponseTest {
    @Test
    public void testJoinFSL3Response_success() throws DecoderException {
        JoinFSL3Response expected = new JoinFSL3Response(0, 0);

        JoinFSL3Response parsedRes = new JoinFSL3Response();
        parsedRes.parse(expected.getCargo());

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
        assertEquals(2, parsedRes.getCargo().length);
        assertEquals(0, parsedRes.getStatus());
        assertEquals(0, parsedRes.getStatusType());
        assertTrue(parsedRes.isStatusOK());
    }

    @Test
    public void testJoinFSL3Response_error() throws DecoderException {
        JoinFSL3Response expected = new JoinFSL3Response(1, 5);

        JoinFSL3Response parsedRes = new JoinFSL3Response();
        parsedRes.parse(expected.getCargo());

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
        assertEquals(1, parsedRes.getStatus());
        assertEquals(5, parsedRes.getStatusType());
    }
}
