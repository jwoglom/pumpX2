package com.jwoglom.pumpx2.pump.messages.calculator;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class BolusCalcUnitsTest {

    @Test
    public void testDoublePrecision() {
        assertEquals(1.23, BolusCalcUnits.doublePrecision(1.2333333333333334), 0.001);
        assertEquals(0.83, BolusCalcUnits.doublePrecision(0.8333333333333334), 0.001);

        assertEquals(-1.23, BolusCalcUnits.doublePrecision(-1.2333333333333334), 0.001);
        assertEquals(-0.83, BolusCalcUnits.doublePrecision(-0.8333333333333334), 0.001);

        assertEquals(-0.05, BolusCalcUnits.doublePrecision(-0.051), 0.001);
        assertEquals(-0.06, BolusCalcUnits.doublePrecision(-0.059), 0.001);
    }
}
