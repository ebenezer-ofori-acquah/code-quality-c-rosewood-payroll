package com.rosewood.payroll;

import java.util.Map;

public class InMemoryEmployeeRepository implements EmployeeRepository {
    private final Map<String, Employee> employees = Map.of(
            "e1", new Employee("e1", "Efua Sarpong"),
            "e2", new Employee("e2", "Kwame Owusu"));

    @Override
    public Employee findById(String id) {
        Employee employee = employees.get(id);
        if (employee == null) {
            throw new EmployeeNotFoundException(id);
        }
        return employee;
    }

}
