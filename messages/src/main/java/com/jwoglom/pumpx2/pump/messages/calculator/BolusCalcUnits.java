package com.jwoglom.pumpx2.pump.messages.calculator;

import com.google.common.base.Preconditions;
import com.jwoglom.pumpx2.shared.L;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Objects;

/**
 * An object representing the units for each component of the bolus calculation. The total amount
 * is the amount which is actually suggested by the pump; all other values are helpers which can
 * be used to display the subcomponents of the calculation. Note that the total is NOT ALWAYS the
 * sum of each other component, since in some scenarios components will be ignored in the calculation.
 */
public class BolusCalcUnits {

    private final double total;
    private final double fromCarbs;
    private final double fromBG;
    private final double fromIOB;
    private final double fromUser;

    BolusCalcUnits(double total, double fromCarbs, double fromBG, double fromIOB, double fromUser) {
        this.total = doublePrecision(total);
        this.fromCarbs = doublePrecision(fromCarbs);
        this.fromBG = doublePrecision(fromBG);
        this.fromIOB = doublePrecision(fromIOB);
        this.fromUser = doublePrecision(fromUser);
    }

    BolusCalcUnits(double total, double fromCarbs, double fromBG, double fromIOB) {
        this(total, fromCarbs, fromBG, fromIOB, 0.0);
    }

    /**
     * @param fromUser the amount of insulin entered by the user which overrides all other amounts
     * @return a {@link BolusCalcUnits} object which only contains the user-provided input
     */
    public static BolusCalcUnits fromUser(double fromUser) {
        return new BolusCalcUnits(fromUser, 0.0, 0.0, 0.0, fromUser);
    }

    /**
     * @return the total amount of insulin which will be filled in the units input.
     * This is NOT ALWAYS the sum of all of the individual components.
     */
    public double getTotal() {
        return total;
    }

    public double getFromCarbs() {
        return fromCarbs;
    }

    public double getFromBG() {
        return fromBG;
    }

    public double getFromIOB() {
        return fromIOB;
    }

    public double getFromUser() {
        return fromUser;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BolusCalcUnits that = (BolusCalcUnits) o;
        return Double.compare(that.total, total) == 0 && Double.compare(that.fromCarbs, fromCarbs) == 0 && Double.compare(that.fromBG, fromBG) == 0 && Double.compare(that.fromIOB, fromIOB) == 0 && Double.compare(that.fromUser, fromUser) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(total, fromCarbs, fromBG, fromIOB, fromUser);
    }

    @Override
    public String toString() {
        return "BolusCalcUnits(total: " + total + " = fromCarbs: " + fromCarbs + " + bg: " + fromBG + " + fromIOB: " + fromIOB + " + fromUser: " + fromUser + ")";
    }


    private static final DecimalFormat decFormat = new DecimalFormat("0.00");
    static {
        decFormat.setRoundingMode(RoundingMode.HALF_UP);
    }
    static double doublePrecision(double val) {
//        // hack with RoundingMode.FLOOR: round -0.051 => -0.05 and -0.059 => -0.06
//        if (val < 0) {
//            return -1 * doublePrecision(-1 * val);
//        }
        L.d("doublePrecision", val+" -> " + Double.parseDouble(decFormat.format(val)));
        return Double.parseDouble(decFormat.format(val));
    }
}
