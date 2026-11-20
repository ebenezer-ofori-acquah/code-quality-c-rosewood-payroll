package com.rosewood.payroll;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PayrollCalculatorTest {

    private static final double DELTA = 0.001;

    @Mock
    private EmployeeRepository repository;

    private PayrollCalculator calculator;

    @BeforeEach
    void setUp() {
        calculator = new PayrollCalculator(repository, ThresholdTaxBandSelector.standard());
    }

    @Nested
    class BaselineCalculations {

        @Test
        void calculatesStandardNetPayWithLowTaxBand() {
            when(repository.findById("e1")).thenReturn(new Employee("e1", "Efua Sarpong"));

            double net = calculator.calculateNetPay("e1", 40, 20);

            assertEquals(760.0, net, DELTA);
        }

        @Test
        void calculatesOvertimePayWithMediumTaxBand() {
            when(repository.findById("e2")).thenReturn(new Employee("e2", "Kwame Owusu"));

            double net = calculator.calculateNetPay("e2", 50, 30);

            assertEquals(1402.5, net, DELTA);
        }

        @Test
        void calculatesSubFortyHourNetPayWithLowTaxBand() {
            when(repository.findById("e1")).thenReturn(new Employee("e1", "Efua Sarpong"));

            double net = calculator.calculateNetPay("e1", 35, 25);

            assertEquals(831.25, net, DELTA);
        }

        @Test
        void calculatesZeroNetPayForZeroHours() {
            when(repository.findById("e1")).thenReturn(new Employee("e1", "Efua Sarpong"));

            double net = calculator.calculateNetPay("e1", 0, 20);

            assertEquals(0.0, net, DELTA);
        }

        @Test
        void calculatesZeroNetPayForZeroHourlyRate() {
            when(repository.findById("e1")).thenReturn(new Employee("e1", "Efua Sarpong"));

            double net = calculator.calculateNetPay("e1", 40, 0);

            assertEquals(0.0, net, DELTA);
        }
    }

    @Nested
    class BoundariesAndTaxBands {

        @ParameterizedTest
        @CsvSource({"41, 20, 788.5", "40, 20, 760.0"})
        void evaluatesOvertimeBoundaries(double hours, double rate, double expectedNet) {
            when(repository.findById("e1")).thenReturn(new Employee("e1", "Efua Sarpong"));

            double net = calculator.calculateNetPay("e1", hours, rate);

            assertEquals(expectedNet, net, DELTA);
        }

        @ParameterizedTest
        @CsvSource({
                "40, 25, 850.0",
                "39, 25, 926.25",
                "40, 125, 3750.0",
                "40, 124, 4216.0"
        })
        void evaluatesTaxBandBoundaries(double hours, double rate, double expectedNet) {
            when(repository.findById("e1")).thenReturn(new Employee("e1", "Efua Sarpong"));

            double net = calculator.calculateNetPay("e1", hours, rate);

            assertEquals(expectedNet, net, DELTA);
        }

        @Test
        void calculatesWithAnInjectedAdditionalTaxBand() {
            TaxBandSelector selector = new ThresholdTaxBandSelector(List.of(
                    new TaxBand(1000.0, 0.05),
                    new TaxBand(2000.0, 0.12),
                    new TaxBand(5000.0, 0.15),
                    new TaxBand(Double.POSITIVE_INFINITY, 0.25)));
            PayrollCalculator calculatorWithAdditionalBand =
                    new PayrollCalculator(repository, selector);
            when(repository.findById("e1")).thenReturn(new Employee("e1", "Efua Sarpong"));

            double net = calculatorWithAdditionalBand.calculateNetPay("e1", 40, 37.5);

            assertEquals(1320.0, net, DELTA);
        }
    }

    @Nested
    class FailuresAndValidation {

        @Test
        void rejectsNullRepository() {
            NullPointerException exception = assertThrows(
                    NullPointerException.class,
                    () -> new PayrollCalculator(null, ThresholdTaxBandSelector.standard()));

            assertEquals("repository must not be null", exception.getMessage());
        }

        @Test
        void propagatesEmployeeNotFoundFromRepository() {
            when(repository.findById("unknown"))
                    .thenThrow(new EmployeeNotFoundException("unknown"));

            assertThrows(
                    EmployeeNotFoundException.class,
                    () -> calculator.calculateNetPay("unknown", 40, 20));
        }

        @Test
        void rejectsNullEmployeeReturnedByRepository() {
            when(repository.findById("unknown")).thenReturn(null);

            assertThrows(
                    EmployeeNotFoundException.class,
                    () -> calculator.calculateNetPay("unknown", 40, 20));
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = { "   ", "\t" })
        void rejectsNullOrBlankEmployeeId(String id) {
            assertThrows(
                    IllegalArgumentException.class,
                    () -> calculator.calculateNetPay(id, 40, 20));
            verifyNoInteractions(repository);
        }

        @Test
        void rejectsNegativeHours() {
            assertThrows(
                    IllegalArgumentException.class,
                    () -> calculator.calculateNetPay("e1", -1, 20));
            verifyNoInteractions(repository);
        }

        @Test
        void rejectsNegativeHourlyRate() {
            assertThrows(
                    IllegalArgumentException.class,
                    () -> calculator.calculateNetPay("e1", 40, -10));
            verifyNoInteractions(repository);
        }

        @Test
        void rejectsGrossPayOverflow() {
            when(repository.findById("e1")).thenReturn(new Employee("e1", "Efua Sarpong"));

            assertThrows(
                    ArithmeticException.class,
                    () -> calculator.calculateNetPay("e1", Double.MAX_VALUE, 2));
        }

        @Test
        void rejectsNonFiniteNetPay() {
            TaxBandSelector overflowingSelector = grossPay -> Double.MAX_VALUE;
            PayrollCalculator calculatorWithOverflowingSelector = new PayrollCalculator(repository,
                    overflowingSelector);
            when(repository.findById("e1")).thenReturn(new Employee("e1", "Efua Sarpong"));

            assertThrows(
                    ArithmeticException.class,
                    () -> calculatorWithOverflowingSelector.calculateNetPay("e1", 2, 1));
        }

        @ParameterizedTest
        @ValueSource(doubles = { Double.NaN, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY })
        void rejectsNonFiniteHours(double invalidHours) {
            assertThrows(
                    IllegalArgumentException.class,
                    () -> calculator.calculateNetPay("e1", invalidHours, 20));
            verifyNoInteractions(repository);
        }

        @ParameterizedTest
        @ValueSource(doubles = { Double.NaN, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY })
        void rejectsNonFiniteHourlyRate(double invalidRate) {
            assertThrows(
                    IllegalArgumentException.class,
                    () -> calculator.calculateNetPay("e1", 40, invalidRate));
            verifyNoInteractions(repository);
        }
    }
}