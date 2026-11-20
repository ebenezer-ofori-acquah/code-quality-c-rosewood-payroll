package com.rosewood.payroll;

public class EmployeeNotFoundException extends RuntimeException {
    public EmployeeNotFoundException(String employeeId) {
        super("Employee not found for id: " + employeeId);
    }
}
