package com.rosewood.payroll;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class GrossPayCalculatorTest {

    private static final double DELTA = 0.001;

    @Test
    void calculatesRegularGrossPay() {
        assertEquals(800.0, GrossPayCalculator.calculate(40, 20), DELTA);
    }

    @Test
    void calculatesOvertimeAtHalfRate() {
        assertEquals(1100.0, GrossPayCalculator.calculate(50, 20), DELTA);
    }

    @Test
    void rejectsGrossPayOverflow() {
        assertThrows(
                ArithmeticException.class,
                () -> GrossPayCalculator.calculate(Double.MAX_VALUE, 2));
    }
}
