package com.jwoglom.pumpx2.pump.messages.builders.crypto;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;

import org.junit.Test;

public class AllZeroSecureRandomTest {
    @Test
    public void oneByte() {
        AllZeroSecureRandom r = new AllZeroSecureRandom();
        byte[] out = new byte[1];
        r.nextBytes(out);
        assertHexEquals(new byte[]{0}, out);
    }

    private byte[] getZeroes(int num) {
        byte[] exp = new byte[num];
        for (int i=0; i<num; i++) {
            exp[i] = 0;
        }
        return exp;
    }


    @Test
    public void manyBytes() {
        for (int i=1; i<256; i++) {
            AllZeroSecureRandom r = new AllZeroSecureRandom();
            byte[] out = new byte[i];
            r.nextBytes(out);

            assertHexEquals(getZeroes(i), out);
        }
    }
}
