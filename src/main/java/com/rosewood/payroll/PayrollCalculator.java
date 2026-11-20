package com.rosewood.payroll;

import java.util.Objects;

public class PayrollCalculator {

    private final EmployeeRepository repository;
    private final TaxBandSelector taxBandSelector;

    public PayrollCalculator(EmployeeRepository repository, TaxBandSelector taxBandSelector) {
        this.repository = Objects.requireNonNull(repository, "repository must not be null");
        this.taxBandSelector =
                Objects.requireNonNull(taxBandSelector, "taxBandSelector must not be null");
    }

    /**
     * Calculates net pay for an existing employee.
     *
     * @throws IllegalArgumentException  if the ID or pay inputs are invalid
     * @throws EmployeeNotFoundException if the employee does not exist
     * @throws ArithmeticException       if gross or net pay is outside the
     *                                   supported numeric range
     */
    public double calculateNetPay(String employeeId, double hoursWorked, double hourlyRate) {
        PayrollInputValidator.validate(employeeId, hoursWorked, hourlyRate);

        Employee employee = repository.findById(employeeId);
        if (employee == null) {
            throw new EmployeeNotFoundException(employeeId);
        }

        double gross = GrossPayCalculator.calculate(hoursWorked, hourlyRate);
        double taxRate = taxBandSelector.selectTaxRate(gross);
        double netPay = gross - (gross * taxRate);
        if (!Double.isFinite(netPay)) {
            throw new ArithmeticException("Net pay overflowed the supported numeric range");
        }
        return netPay;
    }
}