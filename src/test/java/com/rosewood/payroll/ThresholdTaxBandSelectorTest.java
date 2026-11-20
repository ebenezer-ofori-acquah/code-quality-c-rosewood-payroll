package com.rosewood.payroll;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class ThresholdTaxBandSelectorTest {

    private static final double DELTA = 0.001;

    @Test
    void selectsTheFirstBandWhoseUpperBoundExceedsGrossPay() {
        ThresholdTaxBandSelector selector = ThresholdTaxBandSelector.standard();

        assertEquals(0.05, selector.selectTaxRate(999.99), DELTA);
        assertEquals(0.15, selector.selectTaxRate(1000.0), DELTA);
        assertEquals(0.25, selector.selectTaxRate(5000.0), DELTA);
    }

    @Test
    void acceptsAnAdditionalBandThroughItsConfiguration() {
        ThresholdTaxBandSelector selector = new ThresholdTaxBandSelector(List.of(
                new TaxBand(1000.0, 0.05),
                new TaxBand(2000.0, 0.12),
                new TaxBand(5000.0, 0.15),
                new TaxBand(Double.POSITIVE_INFINITY, 0.25)));

        assertEquals(0.12, selector.selectTaxRate(1500.0), DELTA);
    }

    @Test
    void rejectsDuplicateTaxBandUpperBounds() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new ThresholdTaxBandSelector(List.of(
                        new TaxBand(1000.0, 0.05),
                        new TaxBand(1000.0, 0.12),
                        new TaxBand(Double.POSITIVE_INFINITY, 0.25))));
    }

    @Test
    void rejectsEmptyBandList() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new ThresholdTaxBandSelector(List.of()));
    }

    @Test
    void rejectsGrossPayOutsideConfiguredBands() {
        ThresholdTaxBandSelector selector =
                new ThresholdTaxBandSelector(
                        List.of(new TaxBand(1000.0, 0.05)));

        assertThrows(IllegalStateException.class, () -> selector.selectTaxRate(1000.0));
    }

    @ParameterizedTest
    @ValueSource(doubles = {-1.0, Double.NaN, Double.POSITIVE_INFINITY})
    void rejectsInvalidGrossPay(double grossPay) {
        ThresholdTaxBandSelector selector = ThresholdTaxBandSelector.standard();

        assertThrows(
                IllegalArgumentException.class,
                () -> selector.selectTaxRate(grossPay));
    }
}
