package com.jwoglom.pumpx2.pump.messages.calculator;

import java.util.List;

/**
 * Represents data about the completed Bolus calculation, with the insulin amounts via {@link BolusCalcUnits}
 * and the conditions which resulted in those insulin amounts being decided.
 */
public class BolusCalcDecision {
    private final BolusCalcUnits units;
    private final List<BolusCalcCondition> conditions;

    BolusCalcDecision(BolusCalcUnits units, List<BolusCalcCondition> conditions) {
        this.units = units;
        this.conditions = conditions;
    }

    /**
     * @return the {@link BolusCalcUnits} representing the bolus calculator insulin decision
     */
    public BolusCalcUnits getUnits() {
        return units;
    }

    /**
     * @return the {@link BolusCalcCondition}s which resulted in this amount of insulin being decided
     */
    public List<BolusCalcCondition> getConditions() {
        return conditions;
    }

    @Override
    public String toString() {
        return "<" + units + ">: " + conditions;
    }
}
