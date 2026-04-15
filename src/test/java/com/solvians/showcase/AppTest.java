package com.solvians.showcase;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

public class AppTest {
    
    @Test
    public void testExpectTwoIntArgs() {
        // Test with insufficient arguments
        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            App.main(new String[]{"xxx"});
        });
        assertTrue(thrown.getMessage().contains("Expected 2 arguments"));
        
        // Test with non-numeric arguments
        NumberFormatException numbers = assertThrows(NumberFormatException.class, () -> {
            App.main(new String[]{"xxx", "zzz"});
        });
        
        // Test with first arg numeric, second not
        numbers = assertThrows(NumberFormatException.class, () -> {
            App.main(new String[]{"10", "zzz"});
        });
        assertEquals("For input string: \"zzz\"", numbers.getMessage());
    }
    
    @Test
    public void testPositiveArguments() {
        // Test with zero threads
        IllegalArgumentException exception1 = assertThrows(IllegalArgumentException.class, () -> {
            App.main(new String[]{"0", "100"});
        });
        assertTrue(exception1.getMessage().contains("must be positive"));
        
        // Test with negative threads
        IllegalArgumentException exception2 = assertThrows(IllegalArgumentException.class, () -> {
            App.main(new String[]{"-1", "100"});
        });
        assertTrue(exception2.getMessage().contains("must be positive"));
        
        // Test with zero quotes
        IllegalArgumentException exception3 = assertThrows(IllegalArgumentException.class, () -> {
            App.main(new String[]{"4", "0"});
        });
        assertTrue(exception3.getMessage().contains("must be positive"));
        
        // Test with negative quotes
        IllegalArgumentException exception4 = assertThrows(IllegalArgumentException.class, () -> {
            App.main(new String[]{"4", "-10"});
        });
        assertTrue(exception4.getMessage().contains("must be positive"));
    }
    
    @Test
    public void testCorrectNumberOfLines() {
        // Capture stdout to verify output
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outputStream));
        
        try {
            // Run with small parameters
            App.main(new String[]{"2", "10"});
            
            // Get the output and split into lines
            String output = outputStream.toString();
            String[] lines = output.split(System.lineSeparator());
            
            // Should have exactly 10 lines
            assertEquals(10, lines.length, "Should generate exactly 10 certificate updates");
            
            // Each line should have the correct format
            for (String line : lines) {
                String[] fields = line.split(",");
                assertEquals(6, fields.length, "Each line should have 6 fields");
                
                // Verify basic format of each field
                assertTrue(fields[0].matches("^\\d+$"), "Timestamp should be numeric");
                assertTrue(fields[1].matches("^[A-Z]{2}[A-Z0-9]{9}[0-9]$"), "ISIN should match format");
                assertTrue(fields[2].matches("^\\d+\\.\\d{2}$"), "Bid price should have 2 decimals");
                assertTrue(fields[3].matches("^\\d+$"), "Bid size should be integer");
                assertTrue(fields[4].matches("^\\d+\\.\\d{2}$"), "Ask price should have 2 decimals");
                assertTrue(fields[5].matches("^\\d+$"), "Ask size should be integer");
            }
            
        } finally {
            // Restore original stdout
            System.setOut(originalOut);
        }
    }
    
    @Test
    public void testMultiThreadedExecution() {
        // Capture stdout
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outputStream));
        
        try {
            // Run with multiple threads
            App.main(new String[]{"5", "50"});
            
            // Get the output and split into lines
            String output = outputStream.toString();
            String[] lines = output.split(System.lineSeparator());
            
            // Should have exactly 50 lines
            assertEquals(50, lines.length, "Should generate exactly 50 certificate updates");
            
            // All lines should be valid
            for (String line : lines) {
                String[] fields = line.split(",");
                assertEquals(6, fields.length, "Each line should have 6 fields");
            }
            
        } finally {
            // Restore original stdout
            System.setOut(originalOut);
        }
    }
}
