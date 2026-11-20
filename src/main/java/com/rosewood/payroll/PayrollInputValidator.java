package com.rosewood.payroll;

final class PayrollInputValidator {

    private PayrollInputValidator() {
    }

    static void validate(String employeeId, double hoursWorked, double hourlyRate) {
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
}
