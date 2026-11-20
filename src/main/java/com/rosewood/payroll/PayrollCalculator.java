package com.rosewood.payroll;

import java.util.List;
import java.util.Objects;

public class PayrollCalculator {

    private static final double STANDARD_HOURS = 40.0;
    private static final double OVERTIME_MULTIPLIER = 0.5;
    private static final List<TaxBand> TAX_BANDS = List.of(
            new TaxBand(1000.0, 0.05),
            new TaxBand(5000.0, 0.15),
            new TaxBand(Double.POSITIVE_INFINITY, 0.25));

    private final EmployeeRepository repository;

    public PayrollCalculator(EmployeeRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository must not be null");
    }

    public double calculateNetPay(String employeeId, double hoursWorked, double hourlyRate) {
        validateInputs(employeeId, hoursWorked, hourlyRate);
        repository.findById(employeeId);

        double gross = calculateGrossPay(hoursWorked, hourlyRate);
        double taxRate = selectTaxRate(gross);
        return gross - (gross * taxRate);
    }

    private void validateInputs(String employeeId, double hoursWorked, double hourlyRate) {
        if (employeeId == null || employeeId.isBlank()) {
            throw new IllegalArgumentException("Employee id must not be null or blank");
        }
        if (!Double.isFinite(hoursWorked) || hoursWorked < 0) {
            throw new IllegalArgumentException("Hours worked must be a non-negative finite number");
        }
        if (!Double.isFinite(hourlyRate) || hourlyRate < 0) {
            throw new IllegalArgumentException("Hourly rate must be a non-negative finite number");
        }
    }

    private double calculateGrossPay(double hoursWorked, double hourlyRate) {
        double gross = hoursWorked * hourlyRate;
        if (hoursWorked > STANDARD_HOURS) {
            gross += (hoursWorked - STANDARD_HOURS) * hourlyRate * OVERTIME_MULTIPLIER;
        }
        if (!Double.isFinite(gross)) {
            throw new ArithmeticException("Gross pay overflowed the supported numeric range");
        }
        return gross;
    }

    private double selectTaxRate(double gross) {
        for (int index = 0; index < TAX_BANDS.size() - 1; index++) {
            TaxBand band = TAX_BANDS.get(index);
            if (gross < band.upperGrossExclusive()) {
                return band.rate();
            }
        }
        return TAX_BANDS.getLast().rate();
    }

    private record TaxBand(double upperGrossExclusive, double rate) {
    }
}