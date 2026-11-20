package com.rosewood.payroll;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.ResourceLock;
import org.junit.jupiter.api.parallel.Resources;

class DemoTest {

    @Test
    void constructorRejectsDirectInstantiation() throws ReflectiveOperationException {
        Constructor<Demo> constructor = Demo.class.getDeclaredConstructor();
        constructor.setAccessible(true);

        assertThrows(InvocationTargetException.class, constructor::newInstance);
    }

    @Test
    @ResourceLock(Resources.SYSTEM_OUT)
    void printsKnownPayrollResults() {
        PrintStream originalOut = System.out;
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        try (PrintStream capturedOut =
                     new PrintStream(output, true, StandardCharsets.UTF_8)) {
            System.setOut(capturedOut);
            assertDoesNotThrow(() -> Demo.main(new String[0]));
        } finally {
            System.setOut(originalOut);
        }

        String result = output.toString(StandardCharsets.UTF_8);
        assertAll(
                () -> assertTrue(result.contains("Result e1, 40h @ 20     -> 760.0")),
                () -> assertTrue(result.contains("Result e2, 50h @ 30     -> 1402.5")));
    }
}
