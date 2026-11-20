package com.rosewood.payroll;

import java.util.List;
import java.util.Objects;

public final class ThresholdTaxBandSelector implements TaxBandSelector {

    private static final double LOW_TAX_UPPER_BOUND = 1000.0;
    private static final double LOW_TAX_RATE = 0.05;
    private static final double MEDIUM_TAX_UPPER_BOUND = 5000.0;
    private static final double MEDIUM_TAX_RATE = 0.15;
    private static final double HIGH_TAX_RATE = 0.25;

    private static final List<TaxBand> STANDARD_TAX_BANDS = List.of(
            new TaxBand(LOW_TAX_UPPER_BOUND, LOW_TAX_RATE),
            new TaxBand(MEDIUM_TAX_UPPER_BOUND, MEDIUM_TAX_RATE),
            new TaxBand(Double.POSITIVE_INFINITY, HIGH_TAX_RATE));

    private final List<TaxBand> bands;

    public ThresholdTaxBandSelector(List<TaxBand> bands) {
        this.bands = List.copyOf(Objects.requireNonNull(bands, "bands must not be null"));
        if (this.bands.isEmpty()) {
            throw new IllegalArgumentException("At least one tax band is required");
        }

        double previousUpperBound = 0.0;
        for (TaxBand band : this.bands) {
            if (band.upperGrossExclusive() <= previousUpperBound) {
                throw new IllegalArgumentException("Tax band upper bounds must be increasing");
            }
            previousUpperBound = band.upperGrossExclusive();
        }
    }

    public static ThresholdTaxBandSelector standard() {
        return new ThresholdTaxBandSelector(STANDARD_TAX_BANDS);
    }

    @Override
    public double selectTaxRate(double grossPay) {
        if (!Double.isFinite(grossPay) || grossPay < 0) {
            throw new IllegalArgumentException("Gross pay must be a non-negative finite number");
        }
        for (TaxBand band : bands) {
            if (grossPay < band.upperGrossExclusive()) {
                return band.rate();
            }
        }
        throw new IllegalStateException("No tax band covers gross pay: " + grossPay);
    }
}
