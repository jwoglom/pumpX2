package com.jwoglom.pumpx2.pump.messages.helpers;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;
import static org.junit.Assert.assertEquals;

import com.google.common.collect.ImmutableList;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class BytesTest {
    @Test
    public void testFloats() {
        List<Float> tests = ImmutableList.of(
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
}
