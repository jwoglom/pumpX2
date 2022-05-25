package com.jwoglom.pumpx2.pump.messages;

import static org.junit.Assert.assertEquals;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.junit.Test;

public class OpCodeFinderTest {
    @Test
    public void testOpCodeFinder() throws DecoderException {
        // 16: CentralChallengeRequest
        assertEquals(16, findOpCode("000010000a00004d08435da26947356d6f"));
        // 17: CentralChallengeResponse
        assertEquals(17, findOpCode("000011001e01008c212d7a8fbda85f83a3440254488dfb561264ec840c4e16873046bc2c1a"));
        // 18: PumpChallengeRequest
        assertEquals(18, findOpCode("010112011601000194a8f98ca49cddf70c2c1331"));
        // 19: PumpChallengeResponse
        assertEquals(19, findOpCode("0001130103010001e8cc"));
        // 32: ApiVersionRequest
        assertEquals(32, findOpCode("0002200200382c"));
        // 33: ApiVersionResponse
        assertEquals(33, findOpCode("00022102040200000077c8"));
    }

    public int findOpCode(String hex) throws DecoderException {
        byte[] raw = Hex.decodeHex(hex);

        return (int) raw[2];
    }
}
