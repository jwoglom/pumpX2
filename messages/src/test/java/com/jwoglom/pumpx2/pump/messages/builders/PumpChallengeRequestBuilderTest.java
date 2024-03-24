package com.jwoglom.pumpx2.pump.messages.builders;

import com.jwoglom.pumpx2.pump.messages.models.PairingCodeType;

import org.junit.Test;

public class PumpChallengeRequestBuilderTest {
    @Test
    public void testValidLongCode() throws PumpChallengeRequestBuilder.InvalidPairingCodeFormat {
        PumpChallengeRequestBuilder.processPairingCode("abcdefghijklmnop");
        PumpChallengeRequestBuilder.processPairingCode("abcd-efgh-ijkl-mnop");
        PumpChallengeRequestBuilder.processPairingCode("abcd-1234-ijkl-5678");
        PumpChallengeRequestBuilder.processPairingCode("abcd1234ijkl5678");
        PumpChallengeRequestBuilder.processPairingCode("abcd-1234-ijkl 5678");
    }

    @Test(expected = PumpChallengeRequestBuilder.InvalidLongPairingCodeFormat.class)
    public void testInvalidLongCode1() throws PumpChallengeRequestBuilder.InvalidPairingCodeFormat {
        PumpChallengeRequestBuilder.processPairingCode("abcd-!fgh-ijkl-mnop");
    }

    @Test(expected = PumpChallengeRequestBuilder.InvalidLongPairingCodeFormat.class)
    public void testInvalidLongCode2() throws PumpChallengeRequestBuilder.InvalidPairingCodeFormat {
        PumpChallengeRequestBuilder.processPairingCode("abcd!fghijklmnop");
    }

    @Test(expected = PumpChallengeRequestBuilder.InvalidLongPairingCodeFormat.class)
    public void testInvalidLongCode3() throws PumpChallengeRequestBuilder.InvalidPairingCodeFormat {
        PumpChallengeRequestBuilder.processPairingCode("abcd--efgh-ijkl-mnop-q");
    }

    @Test(expected = PumpChallengeRequestBuilder.InvalidLongPairingCodeFormat.class)
    public void testInvalidLongCodeIsShortCode() throws PumpChallengeRequestBuilder.InvalidPairingCodeFormat {
        PumpChallengeRequestBuilder.processPairingCode("123456", PairingCodeType.LONG_16CHAR);
    }

    @Test
    public void testValidShortCode() throws PumpChallengeRequestBuilder.InvalidPairingCodeFormat {
        PumpChallengeRequestBuilder.processPairingCode("123456");
        PumpChallengeRequestBuilder.processPairingCode("123 456");
        PumpChallengeRequestBuilder.processPairingCode("123-456");
        PumpChallengeRequestBuilder.processPairingCode("123-789");
    }

    @Test(expected = PumpChallengeRequestBuilder.InvalidShortPairingCodeFormat.class)
    public void testInvalidShortCode1() throws PumpChallengeRequestBuilder.InvalidPairingCodeFormat {
        PumpChallengeRequestBuilder.processPairingCode("123", PairingCodeType.SHORT_6CHAR);
    }

    @Test(expected = PumpChallengeRequestBuilder.InvalidShortPairingCodeFormat.class)
    public void testInvalidShortCode2() throws PumpChallengeRequestBuilder.InvalidPairingCodeFormat {
        PumpChallengeRequestBuilder.processPairingCode("1234567", PairingCodeType.SHORT_6CHAR);
    }

    @Test(expected = PumpChallengeRequestBuilder.InvalidShortPairingCodeFormat.class)
    public void testInvalidShortCode3() throws PumpChallengeRequestBuilder.InvalidPairingCodeFormat {
        PumpChallengeRequestBuilder.processPairingCode("123 45a", PairingCodeType.SHORT_6CHAR);
    }

    @Test(expected = PumpChallengeRequestBuilder.InvalidShortPairingCodeFormat.class)
    public void testInvalidShortCodeIsLongCode() throws PumpChallengeRequestBuilder.InvalidPairingCodeFormat {
        PumpChallengeRequestBuilder.processPairingCode("abcd-efgh-ijkl-mnop", PairingCodeType.SHORT_6CHAR);
    }
}
