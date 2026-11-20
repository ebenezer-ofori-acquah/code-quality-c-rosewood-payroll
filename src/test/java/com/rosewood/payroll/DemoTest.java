package com.rosewood.payroll;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.ResourceLock;
import org.junit.jupiter.api.parallel.Resources;

import static org.junit.jupiter.api.Assertions.assertTrue;

class DemoTest {

    @Test
    @ResourceLock(Resources.SYSTEM_OUT)
    void printsPayrollResultsAndHandlesUnknownEmployee() {
        PrintStream originalOut = System.out;
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        try (PrintStream capturedOut = new PrintStream(output, true, StandardCharsets.UTF_8)) {
            System.setOut(capturedOut);
            Demo.main(new String[0]);
        } finally {
            System.setOut(originalOut);
        }

        String result = output.toString(StandardCharsets.UTF_8);
        assertTrue(result.contains("Result e1, 40h @ 20     -> 760.0"));
        assertTrue(result.contains("Result e2, 50h @ 30     -> 1402.5"));
        assertTrue(result.contains("Lookup failed: Employee not found for id: unknown"));
    }
}
