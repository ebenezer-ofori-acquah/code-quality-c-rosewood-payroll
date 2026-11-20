# Rosewood Payroll — Net Pay Service

This Maven project contains the refactored Rosewood Payroll application for
the Code Quality & Testing lab. The active application is under
`com.rosewood.payroll`.

## Deliverables

- [ANALYSIS.md](ANALYSIS.md) is preserved unchanged. It is a historical
  analysis of the deleted starter implementation, not the current submission.
- This README documents the active architecture and how to build, test, and run it.

## Architecture

- `PayrollCalculator` orchestrates input validation, employee lookup, gross
  pay calculation, tax-rate selection, and net-pay calculation.
- `EmployeeRepository` abstracts employee lookup. The calculator receives the
  repository and tax-band selector through its constructor.
- `InMemoryEmployeeRepository` provides sample employees and throws
  `EmployeeNotFoundException` for an unknown ID. The calculator also rejects a
  repository implementation that returns `null`.
- `GrossPayCalculator` calculates regular and overtime gross pay and detects
  gross-pay overflow.
- `TaxBandSelector` defines tax-rate selection. `ThresholdTaxBandSelector`
  accepts an ordered list of `TaxBand` values, so new thresholds can be
  supplied without changing `PayrollCalculator`.
- `PayrollInputValidator` checks employee IDs, hours, and hourly rates.
- `Demo` is a small console example. Its standard output is demo-only output,
  not application logging or production observability.

## Project structure

```text
.
├── ANALYSIS.md
├── README.md
├── pom.xml
└── src
    ├── main/java/com/rosewood/payroll
    │   ├── Demo.java
    │   ├── Employee.java
    │   ├── EmployeeNotFoundException.java
    │   ├── EmployeeRepository.java
    │   ├── GrossPayCalculator.java
    │   ├── InMemoryEmployeeRepository.java
    │   ├── PayrollCalculator.java
    │   ├── PayrollInputValidator.java
    │   ├── TaxBand.java
    │   ├── TaxBandSelector.java
    │   └── ThresholdTaxBandSelector.java
    └── test/java/com/rosewood/payroll
        ├── DemoTest.java
        ├── GrossPayCalculatorTest.java
        ├── InMemoryEmployeeRepositoryTest.java
        ├── PayrollCalculatorTest.java
        ├── TaxBandTest.java
        └── ThresholdTaxBandSelectorTest.java
```

The original AI-generated implementation and its tests have been deleted.
They are not present in the project, Maven source roots, or application JAR.

## Requirements

- JDK 25
- Maven

Run commands from the project root.

## Run the active demo

```bash
mvn compile exec:java -Dexec.mainClass=com.rosewood.payroll.Demo
```

## Run tests

```bash
mvn test
```

This runs the active JUnit 5 and Mockito tests only.

## Testing methodology

- Unit tests use JUnit 5 and Mockito for real calculation behavior, validation,
  repository outcomes, and injected tax-band behavior.
- Integration coverage is provided through the repository-to-calculator path
  and the in-memory repository tests; no external service integration exists
  in this project.
- Manual verification uses `Demo.main` to run the example and inspect its
  console output.
- Regression verification uses `mvn clean verify`, with JaCoCo enforcing 100%
  statement, branch, and line coverage.
- Mutation verification uses PIT to confirm meaningful assertions resist
  mutations.

## Coverage and mutation testing

Generate the JaCoCo coverage report:

```bash
mvn verify
```

Open `target/site/jacoco/index.html`.

Run PIT mutation testing against the active classes and tests:

```bash
mvn test-compile org.pitest:pitest-maven:mutationCoverage
```

Open `target/pit-reports/index.html`. Review actual assertions and mutation
results; line coverage alone does not establish that the business rules are
correct.
