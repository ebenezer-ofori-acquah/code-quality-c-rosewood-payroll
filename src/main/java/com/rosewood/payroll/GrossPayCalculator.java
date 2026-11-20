package com.rosewood.payroll;

final class GrossPayCalculator {

    private static final double STANDARD_HOURS = 40.0;
    private static final double OVERTIME_MULTIPLIER = 0.5;

    private GrossPayCalculator() {
    }

    static double calculate(double hoursWorked, double hourlyRate) {
        double gross = hoursWorked * hourlyRate;
        if (hoursWorked > STANDARD_HOURS) {
            gross += (hoursWorked - STANDARD_HOURS) * hourlyRate * OVERTIME_MULTIPLIER;
        }
        if (!Double.isFinite(gross)) {
            throw new ArithmeticException("Gross pay overflowed the supported numeric range");
        }
        return gross;
    }
}
