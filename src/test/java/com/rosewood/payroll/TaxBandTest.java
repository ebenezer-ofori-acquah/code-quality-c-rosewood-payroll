package com.rosewood.payroll;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class TaxBandTest {

    @ParameterizedTest
    @ValueSource(doubles = {0.0, -1.0, Double.NaN})
    void rejectsInvalidUpperBound(double upperBound) {
        assertThrows(
                IllegalArgumentException.class,
                () -> new TaxBand(upperBound, 0.05));
    }

    @ParameterizedTest
    @ValueSource(doubles = {-0.01, 1.01, Double.NaN, Double.POSITIVE_INFINITY})
    void rejectsInvalidTaxRate(double taxRate) {
        assertThrows(
                IllegalArgumentException.class,
                () -> new TaxBand(1000.0, taxRate));
    }
}
