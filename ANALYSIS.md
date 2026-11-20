# Rosewood Payroll — Class & Design Analysis

## Scope & Observed Facts

- **Inspected Java classes (this turn):** `Demo` (`src/main/java/com/rosewood/payroll/retired/Demo.java`), `Employee` (`src/main/java/com/rosewood/payroll/retired/Employee.java`), `EmployeeRepository` (`src/main/java/com/rosewood/payroll/retired/EmployeeRepository.java`), `InMemoryEmployeeRepository` (`src/main/java/com/rosewood/payroll/retired/InMemoryEmployeeRepository.java`), and `PayrollCalculator` (`src/main/java/com/rosewood/payroll/retired/PayrollCalculator.java`).
- **Test-suite reference note:** No test file was attached in this turn; references to `PayrollCalculatorTest` (`calculatesNetPay`, `calculatesOvertimePay`, `returnsPayForAnEmployee`) reflect the earlier inspection of `src/test/java/com/rosewood/payroll/PayrollCalculatorTest.java` and remain unverified against any post-edit test file.
- **Reference standard:** *AmaliTech Coding Standards — Java Edition 2026*.

### Ranking Criteria

1. **Critical:** Can hide failures as normal results or produce wrong payroll behavior silently.
2. **High:** Prevents clean substitution/extension or leaves important logic unverified by tests.
3. **Medium:** Couples multiple responsibilities, hard-codes rules/literals, or leaves contracts/inputs implicit.
4. **Low:** Degrades clarity, consistency, or data-carrier idiom without direct runtime failure by itself.

---

## Code Smells, Anti-Patterns & SOLID Violations (Ranked)

### 1. Critical — Catch-all exception swallowing returning `0`
- **Class / method:** `PayrollCalculator.calculateNetPay` (`src/main/java/com/rosewood/payroll/retired/PayrollCalculator.java`).
- **Observed behavior:** `calculateNetPay` wraps employee lookup, overtime, tax selection, net pay calculation, and console printing inside `try { ... } catch (Exception e) { return 0; }`. In `InMemoryEmployeeRepository.findById` (`src/main/java/com/rosewood/payroll/retired/InMemoryEmployeeRepository.java`), an unknown ID returns `employees.get(id)` (`null`), which causes `employee.getName()` to throw a `NullPointerException` that is caught and turned into `0`. `Demo.main` (`src/main/java/com/rosewood/payroll/retired/Demo.java`) calls `calculator.calculateNetPay("unknown", 40, 20)` directly.
- **Standard broken:** **§6.1 ("Do not ignore caught exceptions")** and **§6.3 ("Add context when you rethrow")**. §6.1 states that ignoring caught exceptions or continuing as if nothing happened is wrong, and exceptions should be handled where useful or allowed to propagate/rethrow with context. §6.3 requires keeping the cause and adding context rather than discarding failures.

### 2. High — Concrete repository instantiation inside `PayrollCalculator`
- **Class / field:** `PayrollCalculator` (`src/main/java/com/rosewood/payroll/retired/PayrollCalculator.java`), `private final EmployeeRepository repository = new InMemoryEmployeeRepository();`.
- **Observed behavior:** Although the field type is the interface `EmployeeRepository` (`src/main/java/com/rosewood/payroll/retired/EmployeeRepository.java`), `PayrollCalculator` constructs `InMemoryEmployeeRepository` itself. `Demo.main` (`src/main/java/com/rosewood/payroll/retired/Demo.java`) likewise instantiates `new PayrollCalculator()` directly.
- **Standard broken:** **§12.5 ("Dependency inversion principle (DIP)")**. §12.5 requires depending on abstractions rather than concrete classes and passing collaborators in rather than hard-wiring a concrete implementation.

### 3. Medium — Mixed responsibilities and console side effects in `PayrollCalculator.calculateNetPay`
- **Class / method:** `PayrollCalculator.calculateNetPay` (`src/main/java/com/rosewood/payroll/retired/PayrollCalculator.java`).
- **Observed behavior:** `calculateNetPay` performs repository lookup, gross pay calculation (`hoursWorked * hourlyRate`), overtime calculation (`hoursWorked > 40` with `* 0.5`), tax-rate selection (`< 1000`, `< 5000`, else), net pay calculation (`gross - (gross * taxRate)`), console output (`System.out.println(...)`), and exception catching.
- **Standard broken:** **§4.2 ("A function should do one thing")**, **§4.4 ("A function should have no side effects")**, and **§12.1 ("Single-responsibility principle (SRP)")**. §4.4 treats output/state changes beyond returning a value as side effects, and §12.1 states a class should have one job/reason to change (while §4.1 notes methods should be small, rarely over 20 lines).

### 4. Medium — Tax brackets and overtime literals embedded in conditionals
- **Class / method:** `PayrollCalculator.calculateNetPay` (`src/main/java/com/rosewood/payroll/retired/PayrollCalculator.java`).
- **Observed behavior:** Tax thresholds (`1000`, `5000`), tax rates (`0.05`, `0.15`, `0.25`), overtime threshold (`40`), and overtime factor (`0.5`) are hard-coded literals inside `if` / `else if` / `else` branches. Adding or changing a tax band requires editing `calculateNetPay`.
- **Standard broken:** **§12.2 ("Open-closed principle (OCP)")** and **§3.4 ("Use searchable names: replace magic numbers with named constants")**. §12.2 calls for designs open for extension without modifying existing code, and §3.4 requires named constants instead of magic numbers.

### 5. Medium — Implicit `null` return for missing employees
- **Classes / methods:** `EmployeeRepository.findById` (`src/main/java/com/rosewood/payroll/retired/EmployeeRepository.java`) and `InMemoryEmployeeRepository.findById` (`src/main/java/com/rosewood/payroll/retired/InMemoryEmployeeRepository.java`).
- **Observed behavior:** `findById` returns `null` when an ID is absent, and `PayrollCalculator.calculateNetPay` does not check for `null` before calling `employee.getName()`.
- **Standard broken:** **§6.1** (handling/propagating failures explicitly rather than letting execution proceed into a hidden failure); **§12.2**'s good example also uses `Optional` rather than sentinel/null values. *(The handbook does not state a standalone rule banning `null` returns from repositories.)*

### 6. Medium — Unvalidated method inputs
- **Class / method:** `PayrollCalculator.calculateNetPay(String employeeId, double hoursWorked, double hourlyRate)` (`src/main/java/com/rosewood/payroll/retired/PayrollCalculator.java`).
- **Observed behavior:** No checks reject null/blank `employeeId`, negative `hoursWorked` or `hourlyRate`, or non-finite values (`NaN`, `Double.POSITIVE_INFINITY`, `Double.NEGATIVE_INFINITY`).
- **Standard & scope note:** The handbook does not have a dedicated rule on numeric/input validation or `NaN`/infinity checks; input validation rules should be confirmed against the lab brief while keeping error propagation aligned with **§6.1** and **§6.3**.

### 7. Low — `Employee` is a candidate for a record
- **Class:** `Employee` (`src/main/java/com/rosewood/payroll/retired/Employee.java`).
- **Observed behavior:** `Employee` is an immutable data carrier with `id` and `name` and no behavior beyond accessors. This is a style and maintainability opportunity, not a functional defect.
- **Standard & design note:** **§8 ("Objects and Data Structures")** recommends records for data carriers.

---

## AI-Written Tests That Prove Nothing (and Why)

*Carried over from the earlier `src/test/java/com/rosewood/payroll/PayrollCalculatorTest.java` inspection (not re-attached in this turn):*

1. **`calculatesNetPay()`:** Asserts `net > 0` for `calculateNetPay("e1", 40, 20)`. Any positive number passes, even if the calculation is wrong; breaks **§9**'s requirement for assertions that would fail if behavior were wrong.
2. **`calculatesOvertimePay()`:** Mocks `PayrollCalculator` itself and stubs `calculateNetPay("e2", 50, 30)` to return `1402.5`. The real calculator and overtime logic are never executed; breaks **§9**'s requirement to verify real behavior with assertions that fail when behavior is wrong. *(Specific rules against mocking the system under test itself are not explicitly stated in the handbook.)*
3. **`returnsPayForAnEmployee()`:** Calls `calculateNetPay("e1", 35, 25)` and asserts `assertNotNull(net)` on a primitive `double` (autoboxed to `Double`), which cannot be null even when returning `0.0` or an incorrect amount. Fails **§9**'s requirement for meaningful assertions.

### What Matters Most

- **Most important code defect:** Swallowing exceptions and returning `0` in `PayrollCalculator.calculateNetPay` (**#1, Critical**), because it masks lookup failures and unexpected errors as normal zero pay.
- **Most important test defect:** Mocking `PayrollCalculator` in `calculatesOvertimePay()` alongside weak/vacuous assertions (`assertTrue(net > 0)`, `assertNotNull(net)`), because green tests can hide broken overtime, tax, and boundary logic.

---

## Design & Refactoring Decisions

### Refactoring Structure, Dependency Injection & Rule Extension 
- **Single clear responsibility per class and method (§4.1, §4.2, §4.4, §12.1):** Payroll calculation logic is separated from presentation/console printing (keeping console output in the demo/caller rather than printing inside pay calculation) and decomposed into focused, small responsibilities with no hidden side effects.
- **Constructor dependency injection (§12.5):** Collaborators such as `EmployeeRepository` are injected via constructor rather than instantiated directly inside the calculator, allowing callers and tests to supply real, fake, or mock repositories without modifying the calculator.
- **Absorbing the coming rule change (§3.4, §12.2):** Tax-band thresholds, rates, and rule evaluation are isolated behind a dedicated tax-band abstraction/strategy and named constants rather than buried in an inline conditional chain inside the main calculation flow. Adding or adjusting a tax band slots into the tax-band rules without rewriting overtime, gross-pay, or repository-lookup logic.

### Exception Handling & Explicit Failure Semantics (§6.1, §6.3)
- **No swallowed or hidden failures (§6.1):** Broad catch-all blocks that discard exceptions and return `0` are removed so missing employees, invalid inputs, or unexpected errors are never disguised as a valid payroll result.
- **Targeted handling and custom domain exceptions (§6.1, §6.3):** Lookup misses and invalid inputs are handled explicitly—using domain-specific exceptions (such as an employee-not-found exception and clear validation errors) that preserve context and cause where rethrown or wrapped, while letting unexpected failures propagate to callers that can handle them meaningfully.

### JUnit 5 & Mockito Testing Strategy (§9)
- **Testing real behavior across happy paths, edges, and failures:** Tests exercise the real payroll calculator with injected repository doubles/mocks rather than mocking the calculator itself, verifying exact outputs on happy paths, boundary conditions (such as overtime hours around 40 and tax thresholds around 1,000 and 5,000), and failure cases (unknown employees and invalid inputs).
- **Meaningful assertions over mock counts or coverage alone (§9):** Assertions check actual computed outcomes and thrown exceptions rather than vacuous checks (`assertNotNull` on primitives or loose `> 0` checks) or mock-invocation counts alone, and are verified via test execution, coverage reports, and mutation testing (PIT).

### Kept, Fixed, and Rewritten from the AI Baseline (§9, §12.5)
- **Kept:** The `EmployeeRepository` abstraction boundary and the baseline payroll arithmetic rules (base pay, time-and-a-half overtime above 40 hours, and the existing tax brackets) unless superseded by the lab brief.
- **Fixed:** Constructor injection of `EmployeeRepository`, removal of the catch-all `return 0` handler, removal of console-printing side effects from pay calculation, named constants, and modular separation of tax-band selection.
- **Rewritten:** The shallow AI-generated tests that asserted only `net > 0`, mocked the calculator under test, or checked `assertNotNull` on a primitive `double`, replacing them with focused JUnit 5 and Mockito tests against the real implementation.

### Chosen Improvement: Parameterised Boundary & Tax-Band Tests + Explicit Not-Found Exception (§6.1, §9)
- **The improvement:** Pair a domain-specific not-found exception with a parameterised JUnit 5 test suite covering exact boundary points and adjacent values for overtime and tax bands (including input validation and missing-employee failures).
- **Why it matters:** Payroll defects most often hide at exact threshold boundaries (`40` hours, `1000`, `5000`) and in silent error masking; explicit failure types prevent disguised zero-pay outcomes, while parameterised boundary tests systematically catch off-by-one and relational/arithmetic mutations that broad or single-sample tests miss.

### Verification Check
- Run the Maven test, coverage, and mutation checks (`mvn verify` and `mvn test-compile org.pitest:pitest-maven:mutationCoverage`) on the final code and test suite and inspect the resulting Surefire, JaCoCo, and PIT reports to confirm that all tests pass, targeted branches are covered, and boundary/arithmetic mutations are killed.