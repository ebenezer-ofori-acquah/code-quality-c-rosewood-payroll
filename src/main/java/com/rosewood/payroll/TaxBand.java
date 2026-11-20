package com.rosewood.payroll;

public record TaxBand(double upperGrossExclusive, double rate) {

    public TaxBand {
        if (Double.isNaN(upperGrossExclusive) || upperGrossExclusive <= 0) {
            throw new IllegalArgumentException("Tax band upper bound must be positive");
        }
        if (!Double.isFinite(rate) || rate < 0 || rate > 1) {
            throw new IllegalArgumentException("Tax band rate must be between 0 and 1");
        }
    }
}
