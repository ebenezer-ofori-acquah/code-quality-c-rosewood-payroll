# Rosewood Payroll — Net Pay Service

This Maven project is the Rosewood Payroll implementation for the Code Quality
& Testing lab. The active application is in `com.rosewood.payroll`. The original
AI-generated implementation is retained in `com.rosewood.payroll.retired` for
comparison.

## Architecture

- `PayrollCalculator` validates inputs, verifies that the employee exists,
  calculates gross pay and overtime, and applies the configured tax bands.
- `EmployeeRepository` abstracts employee lookup. The calculator receives this
  dependency through its constructor.
- `InMemoryEmployeeRepository` contains the sample employees and throws
  `EmployeeNotFoundException` when an ID is not found.
- `Demo` wires the in-memory repository to the calculator and prints example
  results, including the handled missing-employee case.
- `Employee` is the immutable employee data record.

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
    │   ├── InMemoryEmployeeRepository.java
    │   ├── PayrollCalculator.java
    │   └── retired
    │       ├── Demo.java
    │       ├── Employee.java
    │       ├── EmployeeRepository.java
    │       ├── InMemoryEmployeeRepository.java
    │       └── PayrollCalculator.java
    └── test/java/com/rosewood/payroll
        ├── DemoTest.java
        ├── PayrollCalculatorTest.java
        ├── InMEmoryEmployeeRespository.java
        └── retired
            └── RetiredPayrollCalculatorTest.java
```

`InMEmoryEmployeeRespository.java` currently contains the
`InMemoryEmployeeRepositoryTest` class. Its filename does not match the class
name; it is shown as it currently exists in the source tree.

The `retired` packages are kept under Maven's standard source roots, so Maven
still compiles the old classes and discovers the old test. The retired test is
preserved for historical comparison; its shallow assertions should not be
treated as proof of the active implementation's behavior.

## Requirements

- JDK 21
- Maven

Run commands from the project root.

## Run the active demo

```bash
mvn compile exec:java -Dexec.mainClass=com.rosewood.payroll.Demo
```

To run the retired demo for comparison:

```bash
mvn compile exec:java -Dexec.mainClass=com.rosewood.payroll.retired.Demo
```

## Run tests

```bash
mvn test
```

This runs the active tests and the retained retired-package test.

## Coverage and mutation testing

JaCoCo coverage report:

```bash
mvn verify
```

Open `target/site/jacoco/index.html`. The report includes active and retired
production classes.

PIT mutation testing:

```bash
mvn test-compile org.pitest:pitest-maven:mutationCoverage
```

Open `target/pit-reports/index.html`. PIT is configured to include both the
active and retired packages. Review assertions and individual mutation results
rather than treating line coverage or a passing test run alone as proof that
the payroll rules are correct.
