package com.jwoglom.pumpx2.pump.messages.calculator;

import static org.junit.Assert.assertEquals;

import java.util.Locale;

import org.junit.Test;

public class BolusCalcUnitsLocaleTest {
    @Test
    public void testDoublePrecisionIsLocaleIndependent() {
        Locale old = Locale.getDefault();
        try {
            // Use a locale that formats decimals with commas (e.g., "0,00").
            Locale.setDefault(Locale.GERMANY);

            assertEquals(0.0, BolusCalcUnits.doublePrecision(0.0), 0.0);
            assertEquals(1.23, BolusCalcUnits.doublePrecision(1.234), 0.0000001);
            assertEquals(1.24, BolusCalcUnits.doublePrecision(1.235), 0.0000001);
            assertEquals(-1.23, BolusCalcUnits.doublePrecision(-1.234), 0.0000001);

            // Constructor calls doublePrecision internally; ensure no crash under comma-decimal locale.
            new BolusCalcUnits(0.0, 0.0, 0.0, 0.0, 0.0);
        } finally {
            Locale.setDefault(old);
        }
    }
}

