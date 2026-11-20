package com.rosewood.payroll;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class InMemoryEmployeeRepositoryTest {

    private InMemoryEmployeeRepository repository;

    @BeforeEach
    void setUp() {
        repository = new InMemoryEmployeeRepository();
    }

    @ParameterizedTest
    @CsvSource({
            "e1, Efua Sarpong",
            "e2, Kwame Owusu"
    })
    void findsSeededEmployeesById(String id, String expectedName) {
        Employee employee = repository.findById(id);

        assertEquals(id, employee.id());
        assertEquals(expectedName, employee.name());
    }

    @Test
    void throwsEmployeeNotFoundExceptionForMissingId() {
        assertThrows(
                EmployeeNotFoundException.class,
                () -> repository.findById("unknown"));
    }
}