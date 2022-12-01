package com.jwoglom.pumpx2.pump.messages.calculator;

import java.util.List;

/**
 * Represents data about a component of the total Bolus calculation, with its insulin amount and
 * conditions which resulted in that insulin amount being decided.
 */
public class BolusCalcComponent {
    private final Double units;
    private final List<BolusCalcCondition> conditions;

    BolusCalcComponent(Double units, List<BolusCalcCondition> conditions) {
        this.units = units;
        this.conditions = conditions;
    }

    /**
     * @return the insulin amount in units for this component
     */
    public Double getUnits() {
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
        return this.units+"u: " + conditions;
    }
}
