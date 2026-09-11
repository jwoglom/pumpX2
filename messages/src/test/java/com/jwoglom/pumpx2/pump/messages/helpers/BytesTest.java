package com.jwoglom.pumpx2.pump.messages.helpers;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import org.junit.Test;

import java.util.List;

public class BytesTest {
    @Test
    public void testFloats() {
        List<Float> tests = Arrays.asList(
                0.0F,
                1.0F,
                1.11F,
                1.123F,
                -1.0F,
                -1.11F,
                -1.123F,
                0.00000001F,
                -0.00000001F,
                12345.54321F,
                -12345.54321F,
                Float.NaN
        );

        tests.forEach(f -> {
            byte[] bytes = Bytes.toFloat(f);
            float read = Bytes.readFloat(bytes, 0);
            assertEquals(f, read, 0.00001);
            byte[] back = Bytes.toFloat(read);
            assertHexEquals(bytes, back);
        });
    }

    @Test
    public void testDropFirstN() {
        byte[] input = new byte[]{1, 2, 3, 4};
        assertArrayEquals(new byte[]{1, 2, 3, 4}, Bytes.dropFirstN(input, 0));
        assertArrayEquals(new byte[]{3, 4}, Bytes.dropFirstN(input, 2));
        assertArrayEquals(new byte[]{}, Bytes.dropFirstN(input, 4));

        assertThrows(IllegalArgumentException.class, () -> Bytes.dropFirstN(input, -1));
        assertThrows(IllegalArgumentException.class, () -> Bytes.dropFirstN(input, 5));
    }

    @Test
    public void testDropLastN() {
        byte[] input = new byte[]{1, 2, 3, 4};
        assertArrayEquals(new byte[]{1, 2, 3, 4}, Bytes.dropLastN(input, 0));
        assertArrayEquals(new byte[]{1, 2}, Bytes.dropLastN(input, 2));
        assertArrayEquals(new byte[]{}, Bytes.dropLastN(input, 4));

        assertThrows(IllegalArgumentException.class, () -> Bytes.dropLastN(input, -1));
        assertThrows(IllegalArgumentException.class, () -> Bytes.dropLastN(input, 5));
    }

    @Test
    public void testReadStringStopsAtNulAndFieldWidth() {
        byte[] raw = "abc\0defgh".getBytes(StandardCharsets.UTF_8);

        assertEquals("abc", Bytes.readString(raw, 0, 8));
        // the field width bounds the read even with no terminator inside it
        assertEquals("ab", Bytes.readString(raw, 0, 2));
        assertEquals("def", Bytes.readString(raw, 4, 3));
        // an empty field is a leading NUL, not an error
        assertEquals("", Bytes.readString(raw, 3, 4));
        // reads are clamped to the array, not just the field width
        assertEquals("defgh", Bytes.readString(raw, 4, 100));
    }

    @Test
    public void testReadStringDecodesNonAscii() {
        // Every byte of a multi-byte UTF-8 sequence is negative as a signed byte, so the
        // previous `raw[i] > 0` terminator cut these fields off at the first non-ASCII byte.
        for (String s : List.of("café", "Ünïcøde", "日本語", "aéb")) {
            byte[] field = Bytes.writeString(s, 32);
            assertEquals(s, Bytes.readString(field, 0, 32));
        }
    }

    @Test
    public void testWriteStringPadsToExactWidth() {
        assertArrayEquals(new byte[]{'a', 'b', 'c', 0, 0}, Bytes.writeString("abc", 5));
        assertArrayEquals(new byte[]{'a', 'b', 'c'}, Bytes.writeString("abc", 3));
        assertArrayEquals(new byte[]{0, 0}, Bytes.writeString("", 2));

        // The field width counts encoded bytes, not characters: "é" is two bytes.
        assertEquals(4, Bytes.writeString("é", 4).length);
        assertArrayEquals(new byte[]{(byte) 0xc3, (byte) 0xa9, 0, 0}, Bytes.writeString("é", 4));
    }

    @Test
    public void testWriteStringRejectsOverlongInput() {
        // Previously returned a 4-byte array for a 3-byte field, producing cargo of the
        // wrong size that would then be sent to the pump.
        assertThrows(IllegalArgumentException.class, () -> Bytes.writeString("abcd", 3));
        // Fits as characters but not as encoded bytes.
        assertThrows(IllegalArgumentException.class, () -> Bytes.writeString("é", 1));
    }

    @Test
    public void testWriteStringReadStringRoundTrip() {
        for (String s : List.of("", "a", "abcdefgh", "café")) {
            assertEquals(s, Bytes.readString(Bytes.writeString(s, 16), 0, 16));
        }
    }
}
