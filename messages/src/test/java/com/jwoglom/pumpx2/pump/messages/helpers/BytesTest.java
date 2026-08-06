package com.jwoglom.pumpx2.pump.messages.helpers;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

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
    public void testWriteStringRejectsEncodedValueLongerThanField() {
        assertThrows(IllegalArgumentException.class, () -> Bytes.writeString("1234567", 6));
    }

    @Test
    public void testWriteStringAlwaysReturnsExactFieldWidth() {
        assertEquals(6, Bytes.writeString("123", 6).length);
        assertEquals(6, Bytes.writeString("123456", 6).length);
    }

    @Test
    public void testReadStringSupportsUtf8Bytes() {
        byte[] encoded = Bytes.writeString("café", 8);
        assertEquals("café", Bytes.readString(encoded, 0, 8));
    }

    @Test
    public void testDropHelpersRejectCountsOutsideArrayBounds() {
        byte[] bytes = new byte[]{1, 2, 3};

        assertThrows(IllegalArgumentException.class, () -> Bytes.dropFirstN(bytes, -1));
        assertThrows(IllegalArgumentException.class, () -> Bytes.dropFirstN(bytes, 4));
        assertThrows(IllegalArgumentException.class, () -> Bytes.dropLastN(bytes, -1));
        assertThrows(IllegalArgumentException.class, () -> Bytes.dropLastN(bytes, 4));
    }
}
