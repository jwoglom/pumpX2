package com.jwoglom.pumpx2.pump.messages.builders.crypto;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.SecureRandomSpi;
import java.util.Arrays;

public class AllZeroSecureRandom extends SecureRandom {

    private static class CustomSpi extends SecureRandomSpi {
        CustomSpi() {}

        @Override
        protected void engineSetSeed(byte[] seed) {
        }

        @Override
        protected void engineNextBytes(byte[] bytes) {
            Arrays.fill(bytes, (byte) 0);
        }

        @Override
        protected byte[] engineGenerateSeed(int numBytes) {
            return null;
        }

    }

    public AllZeroSecureRandom()
    {
        super(createSPI(), null);
    }

    static SecureRandomSpi createSPI()
    {
        return new CustomSpi();
    }

    public static void main(String... args) {
        AllZeroSecureRandom secRnd = new AllZeroSecureRandom();

        byte[] rndBytes = new byte[17];
        secRnd.nextBytes(rndBytes);

        for (byte r : rndBytes)
        {
            System.out.print(String.format("%02x ", r & 0xFF));
        }
        System.out.println();
    }

    private static final long serialVersionUID = -7550189683373026294L;

}