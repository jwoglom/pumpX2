package com.jwoglom.pumpx2.pump.messages.calculator;

import java.util.Objects;

public class BolusParameters {
    Double units;
    Integer carbsGrams;
    Integer glucoseMgdl;

    BolusParameters() {
        // all null
    }

    public BolusParameters(Double units, Integer carbsGrams, Integer glucoseMgdl) {
        this.units = units;
        this.carbsGrams = carbsGrams;
        this.glucoseMgdl = glucoseMgdl;
    }

    public BolusParameters withUnits(Double units) {
        return new BolusParameters(units, carbsGrams, glucoseMgdl);
    }

    public BolusParameters withCarbsGrams(Integer carbsGrams) {
        return new BolusParameters(units, carbsGrams, glucoseMgdl);
    }

    public BolusParameters withGlucoseMgdl(Integer glucoseMgdl) {
        return new BolusParameters(units, carbsGrams, glucoseMgdl);
    }

    public Double getUnits() {
        return units;
    }

    public Integer getCarbsGrams() {
        return carbsGrams;
    }

    public Integer getGlucoseMgdl() {
        return glucoseMgdl;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BolusParameters that = (BolusParameters) o;
        if ((that.units == null && units != null) || (that.units != null && units == null)) {
            return false;
        }
        if ((that.carbsGrams == null && carbsGrams != null) || (that.carbsGrams != null && carbsGrams == null)) {
            return false;
        }
        if ((that.glucoseMgdl == null && glucoseMgdl != null) || (that.glucoseMgdl != null && glucoseMgdl == null)) {
            return false;
        }
        return Double.compare(that.units, units) == 0 && carbsGrams == that.carbsGrams && glucoseMgdl == that.glucoseMgdl;
    }

    @Override
    public int hashCode() {
        return Objects.hash(units, carbsGrams, glucoseMgdl);
    }
}
