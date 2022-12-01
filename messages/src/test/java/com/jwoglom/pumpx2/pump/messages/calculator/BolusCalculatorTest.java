package com.jwoglom.pumpx2.pump.messages.calculator;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import org.apache.commons.lang3.tuple.Pair;
import org.hamcrest.Matcher;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import com.jwoglom.pumpx2.pump.messages.calculator.BolusCalcCondition.*;
import com.jwoglom.pumpx2.pump.messages.response.currentStatus.LastBGResponse;

public class BolusCalculatorTest {

    private static final BolusParameters PARAMS = new BolusParameters(null, 0, null);
    private static final BolusCalcDataSnapshot SNAPSHOT = new BolusCalcDataSnapshot(true, 6000, 0, 200, 0, 30, true, 110, false, 25000, 0);
    private static final LastBGResponse EMPTY_BG_RESPONSE = new LastBGResponse(0L, 0, 0);


    private static final BolusCalcCondition FILL_BG_FROM_CGM = new DataDecision("fill BG from CGM");
    private static final BolusCalcCondition FILL_BG_FROM_INPUT = new DataDecision("use BG user input");

    private static final BolusCalcCondition POSITIVE_BG_CORRECTION = new Decision("add positive BG correction", "above BG target");
    private static final BolusCalcCondition NO_POSITIVE_BG_CORRECTION = new Decision("not add positive BG correction", "active IOB is greater than correction bolus while above target");
    private static final BolusCalcCondition SET_ZERO_INSULIN = new Decision("set zero insulin", "negative correction greater than carb amount");
    private static final BolusCalcCondition NEGATIVE_BG_CORRECTION = new Decision("add negative BG correction", "below BG target");


    @Test
    public void testNoActionWithoutBg() {
        expect(null, null,0.0, 0, new BolusCalcUnits(0.0, 0.0, 0.0, 0.0),
                new DataDecision("not fill BG", "not present in data snapshot or LastBGResponse"),
                new NonActionDecision("add units for BG", "no BG provided"));
    }

    @Test
    public void testManualBgOverridesCgmBG() {
        expect(147, 75, 0.0, 0, new BolusCalcUnits(1.23, 0.0, 1.23, 0.0), FILL_BG_FROM_INPUT, POSITIVE_BG_CORRECTION);
        expect(122, 400, 0.06, 0, new BolusCalcUnits(0.34, 0.0, 0.4, -0.06), FILL_BG_FROM_INPUT, POSITIVE_BG_CORRECTION);
    }

    @Test
    public void testIncreaseInsulinLowIOB() {
        expect(null, 147, 0.0, 0, new BolusCalcUnits(1.23, 0.0, 1.23, 0.0), FILL_BG_FROM_CGM, POSITIVE_BG_CORRECTION);
        expect(null, 147, 0.0, 5, new BolusCalcUnits(2.06, 0.83, 1.23, 0.0), FILL_BG_FROM_CGM, POSITIVE_BG_CORRECTION);
        expect(null, 147, 0.2, 0, new BolusCalcUnits(1.03, 0.0, 1.23, -0.2), FILL_BG_FROM_CGM, POSITIVE_BG_CORRECTION);
        expect(null, 147, 0.2, 5, new BolusCalcUnits(1.86, 0.83, 1.23, -0.2), FILL_BG_FROM_CGM, POSITIVE_BG_CORRECTION);
        expect(null, 156, 0.0, 0, new BolusCalcUnits(1.53, 0.0, 1.53, 0.0), FILL_BG_FROM_CGM, POSITIVE_BG_CORRECTION);
        expect(null, 156, 0.0, 5, new BolusCalcUnits(2.36, 0.83, 1.53, 0.0), FILL_BG_FROM_CGM, POSITIVE_BG_CORRECTION);
        expect(null, 122, 0.06, 0, new BolusCalcUnits(0.34, 0.0, 0.4, -0.06), FILL_BG_FROM_CGM, POSITIVE_BG_CORRECTION);
        expect(null, 122, 0.06, 45, new BolusCalcUnits(7.84, 7.50, 0.4, -0.06), FILL_BG_FROM_CGM, POSITIVE_BG_CORRECTION);
    }

    @Test
    public void testNoCorrectionCarbsOnlyAboveTargetWithIOBGreaterThanBGCorrection() {
        expect(null, 157, 6.09, 10, new BolusCalcUnits(1.67, 1.67, 1.57, -6.09), FILL_BG_FROM_CGM, NO_POSITIVE_BG_CORRECTION);
        expect(null, 168, 6.18, 0, new BolusCalcUnits(0.0, 0.0, 1.93, -6.18), FILL_BG_FROM_CGM, NO_POSITIVE_BG_CORRECTION);
        expect(null, 168, 6.18, 5, new BolusCalcUnits(0.83, 0.83, 1.93, -6.18), FILL_BG_FROM_CGM, NO_POSITIVE_BG_CORRECTION);
        expect(null, 172, 5.50, 0, new BolusCalcUnits(0.0, 0.0, 2.07, -5.50), FILL_BG_FROM_CGM, NO_POSITIVE_BG_CORRECTION);
        expect(null, 183, 4.51, 0, new BolusCalcUnits(0.0, 0.0, 2.43, -4.51), FILL_BG_FROM_CGM, NO_POSITIVE_BG_CORRECTION);
    }

    @Test
    public void testCorrectionToZeroNoCarbsBelowTargetWithHighIOB() {
        expect(null, 105, 6.09, 0, new BolusCalcUnits(0, 0, -0.17, -6.09), FILL_BG_FROM_CGM, SET_ZERO_INSULIN);
        expect(null, 105, 6.09, 10, new BolusCalcUnits(0, 1.67, -0.17, -6.09), FILL_BG_FROM_CGM, SET_ZERO_INSULIN);
        expect(null, 110, 9.17, 0, new BolusCalcUnits(0.0, 0.0, 0.0, -9.17), FILL_BG_FROM_CGM, NO_POSITIVE_BG_CORRECTION);
        expect(null, 100, 9.17, 5, new BolusCalcUnits(0.0, 0.83, -0.33, -9.17), FILL_BG_FROM_CGM, SET_ZERO_INSULIN);
        expect(null, 100, 9.17, 25, new BolusCalcUnits(0.0, 4.17, -0.33, -9.17), FILL_BG_FROM_CGM, SET_ZERO_INSULIN);
        expect(null, 100, 9.17, 57, new BolusCalcUnits(0.0, 9.50, -0.33, -9.17), FILL_BG_FROM_CGM, SET_ZERO_INSULIN);
    }

    @Test
    public void testNegativeCorrectionBelowTargetWithHighIOB() {
        expect(105, 200, 6.02, 50, new BolusCalcUnits(2.14, 8.33, -0.17, -6.02), FILL_BG_FROM_INPUT, NEGATIVE_BG_CORRECTION); // rounding: pump says 2.13
        expect(null, 105, 6.09, 50, new BolusCalcUnits(2.07, 8.33, -0.17, -6.09), FILL_BG_FROM_CGM, NEGATIVE_BG_CORRECTION);
        expect(90, 200, 6.02, 50, new BolusCalcUnits(1.64, 8.33, -0.67, -6.02), FILL_BG_FROM_INPUT, NEGATIVE_BG_CORRECTION); // rounding: pump says 1.63
        expect(70, 200, 6.02, 50, new BolusCalcUnits(0.98, 8.33, -1.33, -6.02), FILL_BG_FROM_INPUT, NEGATIVE_BG_CORRECTION); // rounding: pump says 0.97
        expect(60, 200, 6.02, 50, new BolusCalcUnits(0.64, 8.33, -1.67, -6.02), FILL_BG_FROM_INPUT, NEGATIVE_BG_CORRECTION); // rounding: pump says 0.63
        expect(53, 200, 6.02, 50, new BolusCalcUnits(0.41, 8.33, -1.9, -6.02), FILL_BG_FROM_INPUT, NEGATIVE_BG_CORRECTION); // rounding: pump says 0.4
        expect(null, 100, 9.17, 58, new BolusCalcUnits(0.17, 9.67, -0.33, -9.17), FILL_BG_FROM_CGM, NEGATIVE_BG_CORRECTION);
    }

    @Test
    public void testDecreaseInsulinFromCarbsWhenBelowBGTarget() {
        expect(null, 108, 7.45, 50, new BolusCalcUnits(0.81, 8.33, -0.07, -7.45), FILL_BG_FROM_CGM, NEGATIVE_BG_CORRECTION);
        expect(109, 108, 7.45, 50, new BolusCalcUnits(0.85, 8.33, -0.03, -7.45), FILL_BG_FROM_INPUT, NEGATIVE_BG_CORRECTION);
        expect(100, 108, 7.45, 50, new BolusCalcUnits(0.55, 8.33, -0.33, -7.45), FILL_BG_FROM_INPUT, NEGATIVE_BG_CORRECTION);
        expect(84, 108, 7.45, 50, new BolusCalcUnits(0.01, 8.33, -0.87, -7.45), FILL_BG_FROM_INPUT, NEGATIVE_BG_CORRECTION);
        expect(null, 68, 0.0, 35, new BolusCalcUnits(4.43, 5.83, -1.40, 0.0), FILL_BG_FROM_CGM, NEGATIVE_BG_CORRECTION);
    }

    @Test
    public void testSetZeroInsulinWhenBelowBGTargetWithLowCarbAmount() {
        expect(null, 108, 7.45, 0, new BolusCalcUnits(0.0, 0, -0.07, -7.45), FILL_BG_FROM_CGM, SET_ZERO_INSULIN);
        expect(null, 108, 7.45, 20, new BolusCalcUnits(0, 3.33, -0.07, -7.45), FILL_BG_FROM_CGM, SET_ZERO_INSULIN);
        expect(83, 108, 7.45, 50, new BolusCalcUnits(0.0, 8.33, -0.9, -7.45), FILL_BG_FROM_INPUT, SET_ZERO_INSULIN);
        expect(80, 108, 7.45, 50, new BolusCalcUnits(0.0, 8.33, -1.0, -7.45), FILL_BG_FROM_INPUT, SET_ZERO_INSULIN);
    }

    @Test
    public void testNoCorrectionWhenExactlyAtBGTargetWithIOB() {
        expect(110, 108, 7.45, 50, new BolusCalcUnits(8.33, 8.33, 0.0, -7.45), FILL_BG_FROM_INPUT, NO_POSITIVE_BG_CORRECTION);
        expect(null, 110, 9.17, 0, new BolusCalcUnits(0.0, 0.0, 0.0, -9.17), FILL_BG_FROM_CGM, NO_POSITIVE_BG_CORRECTION);
    }

    @Test
    public void testOnlyCarbsWhenExactlyAtBGTargetWithIOB() {
        expect(null, 110, 9.17, 5, new BolusCalcUnits(0.83, 0.83, 0.0, -9.17), FILL_BG_FROM_CGM, NO_POSITIVE_BG_CORRECTION);
    }

    public BolusCalculator expect(Integer manualBg, Integer cgmBg, double iob, int carbs, BolusCalcUnits insulin, BolusCalcCondition... conditions) {
        BolusParameters params = new BolusParameters(PARAMS.getUnits(), carbs, PARAMS.getGlucoseMgdl());
        if (manualBg != null) {
            params = params.withGlucoseMgdl(manualBg);
        }

        BolusCalcDataSnapshot snapshot = SNAPSHOT.withIOB(iob);
        if (cgmBg != null) {
            snapshot = snapshot.withCorrectionFactor(cgmBg);
        }

        BolusCalcLastBG lastBG = new BolusCalcLastBG(snapshot.toRawResponse(), EMPTY_BG_RESPONSE);
        BolusCalculator units = new BolusCalculator(params, snapshot, lastBG);
        BolusCalcDecision parsed = units.parse();


        assertEquals("return correct calculation for " + parsed, insulin, parsed.getUnits());
        assertEquals("return correct total for " + parsed, insulin.getTotal(), parsed.getUnits().getTotal(), 0.001);
        assertEquals("return conditions for " + parsed, new HashSet<>(Arrays.asList(conditions)), new HashSet<>(parsed.getConditions()));

        return units;
    }
}
