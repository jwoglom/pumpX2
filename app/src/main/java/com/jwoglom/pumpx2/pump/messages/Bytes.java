package com.jwoglom.pumpx2.pump.messages;

import com.google.common.base.Preconditions;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Arrays;

import kotlin.UByte;
import kotlin.collections.ArraysKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.Charsets;

public class Bytes {

    public static byte[] combine(byte[] ...items) {
        int sum = 0;
        for (byte[] item : items) {
            sum += item.length;
        }

        byte[] ret = new byte[sum];
        int i = 0;
        for (byte[] item : items) {
            for (byte b : item) {
                ret[i] = b;
                i++;
            }
        }

        return ret;
    }

    public static int readShort(byte[] raw, int i) {
        Preconditions.checkArgument(i >= 0 && i + 1 < raw.length);
        return ((andWithMaxValue(raw[i+1]) & 255) << 8) | (andWithMaxValue(raw[i]) & 255);
    }

    public static long readUint32(byte[] bArr, int i) {
        if (i >= 0) {
            int i2 = i + 3;
            if (i2 < bArr.length) {
                return ((long) (((bArr[i] & UByte.MAX_VALUE) & 255) | ((bArr[i2] & UByte.MAX_VALUE) << 24) | (((bArr[i + 2] & UByte.MAX_VALUE) & 255) << 16) | (((bArr[i + 1] & UByte.MAX_VALUE) & 255) << 8))) & 4294967295L;
            }
            throw new ArrayIndexOutOfBoundsException();
        }
        throw new ArrayIndexOutOfBoundsException();
    }

    public static BigInteger readUint64(byte[] bArr, int i) {
        if (i >= 0) {
            int i2 = i + 7;
            if (i2 < bArr.length) {
                return new BigInteger(1, ArraysKt.reversedArray(Arrays.copyOfRange(bArr, i, i + 8)));
            }
            throw new ArrayIndexOutOfBoundsException(i2);
        }
        throw new ArrayIndexOutOfBoundsException(i);
    }

    private static int andWithMaxValue(byte b) {
        return b & UByte.MAX_VALUE;
    }

    public static byte[] toUint32(long j) {
        byte[] bArr = new byte[8];
        ByteBuffer.wrap(bArr).order(ByteOrder.LITTLE_ENDIAN).putLong(j);
        return Arrays.copyOfRange(bArr, 0, 4);
    }

    public static byte[] toUint64(long j) {
        byte[] bArr = new byte[8];
        ByteBuffer.wrap(bArr).order(ByteOrder.LITTLE_ENDIAN).putLong(j);
        return bArr;
    }

    public static byte[] firstTwoBytesLittleEndian(int i) {
        byte[] bytes = new byte[4];
        ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).putInt(i & 0x0000ffff);
        return Arrays.copyOfRange(bytes, 0, 2);
    }

    public static String readString(byte[] raw, int i, int length) {
        if (i >= 0 && i < raw.length) {
            byte[] strBytes = new byte[0];
            byte b;
            while (i < raw.length && (b = raw[i]) > 0) {
                strBytes = combine(strBytes, new byte[]{b});
                if (strBytes.length >= length) {
                    break;
                }
                i++;
            }
            return new String(strBytes, Charsets.UTF_8);
        }
        throw new ArrayIndexOutOfBoundsException(i);
    }

    // null byte pads the string to the given length
    public static byte[] writeString(String input, int length) {
        byte[] encoded = input.getBytes(StandardCharsets.UTF_8);
        for (int i=encoded.length; i < length; i++) {
            encoded = combine(encoded, new byte[]{0});
        }

        return encoded;
    }

    private static final int[] crcLookupTable = {0, 4129, 8258, 12387, 16516, 20645, 24774, 28903, 33032, 37161, 41290, 45419, 49548, 53677, 57806, 61935, 4657, 528, 12915, 8786, 21173, 17044, 29431, 25302, 37689, 33560, 45947, 41818, 54205, 50076, 62463, 58334, 9314, 13379, 1056, 5121, 25830, 29895, 17572, 21637, 42346, 46411, 34088, 38153, 58862, 62927, 50604, 54669, 13907, 9842, 5649, 1584, 30423, 26358, 22165, 18100, 46939, 42874, 38681, 34616, 63455, 59390, 55197, 51132, 18628, 22757, 26758, 30887, 2112, 6241, 10242, 14371, 51660, 55789, 59790, 63919, 35144, 39273, 43274, 47403, 23285, 19156, 31415, 27286, 6769, 2640, 14899, 10770, 56317, 52188, 64447, 60318, 39801, 35672, 47931, 43802, 27814, 31879, 19684, 23749, 11298, 15363, 3168, 7233, 60846, 64911, 52716, 56781, 44330, 48395, 36200, 40265, 32407, 28342, 24277, 20212, 15891, 11826, 7761, 3696, 65439, 61374, 57309, 53244, 48923, 44858, 40793, 36728, 37256, 33193, 45514, 41451, 53516, 49453, 61774, 57711, 4224, 161, 12482, 8419, 20484, 16421, 28742, 24679, 33721, 37784, 41979, 46042, 49981, 54044, 58239, 62302, 689, 4752, 8947, 13010, 16949, 21012, 25207, 29270, 46570, 42443, 38312, 34185, 62830, 58703, 54572, 50445, 13538, 9411, 5280, 1153, 29798, 25671, 21540, 17413, 42971, 47098, 34713, 38840, 59231, 63358, 50973, 55100, 9939, 14066, 1681, 5808, 26199, 30326, 17941, 22068, 55628, 51565, 63758, 59695, 39368, 35305, 47498, 43435, 22596, 18533, 30726, 26663, 6336, 2273, 14466, 10403, 52093, 56156, 60223, 64286, 35833, 39896, 43963, 48026, 19061, 23124, 27191, 31254, 2801, 6864, 10931, 14994, 64814, 60687, 56684, 52557, 48554, 44427, 40424, 36297, 31782, 27655, 23652, 19525, 15522, 11395, 7392, 3265, 61215, 65342, 53085, 57212, 44955, 49082, 36825, 40952, 28183, 32310, 20053, 24180, 11923, 16050, 3793, 7920};
    public static byte[] calculateCRC16(byte[] bArr) {
        int i = 0x0000ffff;
        for (byte b : bArr) {
            i = (i << 8) ^ crcLookupTable[(b ^ (i >>> 8)) & 255];
        }
        int i2 = i ^ 0;
        return new byte[]{(byte) (i2 & 255), (byte) ((i2 >>> 8) & 255)};
    }

    private static final SecureRandom secureRandom = new SecureRandom();
    public static byte[] getSecureRandom8Bytes() {
        byte[] bArr = new byte[8];
        secureRandom.nextBytes(bArr);
        return bArr;
    }

    public static byte[] getSecureRandom10Bytes() {
        byte[] bArr = new byte[10];
        secureRandom.nextBytes(bArr);
        return bArr;
    }
}
