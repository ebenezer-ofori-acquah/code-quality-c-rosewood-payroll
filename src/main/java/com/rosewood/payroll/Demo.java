package com.rosewood.payroll;

public final class Demo {

    private Demo() {
        throw new AssertionError("Demo is a command-line entry point and must not be instantiated");
    }

    public static void main(String[] args) {
        EmployeeRepository repository = new InMemoryEmployeeRepository();
        PayrollCalculator calculator =
                new PayrollCalculator(repository, ThresholdTaxBandSelector.standard());

        double employeeOneNetPay = calculator.calculateNetPay("e1", 40, 20);
        System.out.println("Result e1, 40h @ 20     -> " + employeeOneNetPay);
        double employeeTwoNetPay = calculator.calculateNetPay("e2", 50, 30);
        System.out.println("Result e2, 50h @ 30     -> " + employeeTwoNetPay);
    }
}