# Rosewood Payroll — Net Pay Service

This repository is used for the **Code Quality & Testing** applied lab. It
contains the earlier payroll implementation in a `retired` package so that it
can be kept for reference while the replacement implementation is developed
directly in the `com.rosewood.payroll` package.

## Project structure

```text
src/
├── main/java/com/rosewood/payroll/
│   ├── (new payroll implementation is developed here)
│   └── retired/
│       ├── Demo.java
│       ├── Employee.java
│       ├── EmployeeRepository.java
│       ├── InMemoryEmployeeRepository.java
│       └── PayrollCalculator.java
└── test/java/com/rosewood/payroll/retired/
    └── PayrollCalculatorTest.java
```

The `retired` directory is not a separate Maven project. It is a Java
subpackage that holds the previous implementation and its tests. The files are
kept there to preserve the original behavior for comparison; new payroll code
belongs directly in `src/main/java/com/rosewood/payroll/`, not in `retired`.

## Requirements

- JDK 21
- Maven

## Run the retired demo

The demo in `retired` runs the previous implementation. It is useful for
reference, but it does not run the replacement implementation:

```bash
mvn compile exec:java -Dexec.mainClass=com.rosewood.payroll.retired.Demo
```

## Run tests

Run the existing tests for the retired implementation:

```bash
mvn test
```

## Coverage and mutation testing

JaCoCo and PIT are configured in `pom.xml`:

```bash
mvn verify
mvn test-compile org.pitest:pitest-maven:mutationCoverage
```

The JaCoCo report is written to `target/site/jacoco/index.html`; the PIT report
is written to `target/pit-reports/index.html`. Review the assertions and
mutation results rather than treating a passing test run or line coverage as
proof that the payroll rules are correct.
