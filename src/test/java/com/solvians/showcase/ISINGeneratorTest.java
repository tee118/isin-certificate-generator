package com.solvians.showcase;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.*;

class ISINGeneratorTest {
    
    private ISINGenerator generator;
    
    @BeforeEach
    void setUp() {
        generator = new ISINGenerator();
    }
    
    @Test
    void testGeneratedISINFormat() {
        // Generate several ISINs and verify they all match the expected format
        for (int i = 0; i < 100; i++) {
            String isin = generator.generate();
            
            // Should be exactly 12 characters
            assertEquals(12, isin.length(), "ISIN should be 12 characters long");
            
            // Should match the pattern: 2 letters + 9 alphanumeric + 1 digit
            assertTrue(isin.matches("^[A-Z]{2}[A-Z0-9]{9}[0-9]$"), 
                "ISIN should match format: 2 letters, 9 alphanumeric, 1 digit. Got: " + isin);
        }
    }
    
    @Test
    void testCheckDigitCalculation() {
        // Test that check digit calculation is consistent and produces valid digits
        
        // Test various ISIN prefixes
        int checkDigit1 = generator.calculateCheckDigit("DE123456789");
        assertTrue(checkDigit1 >= 0 && checkDigit1 <= 9, "Check digit should be 0-9");
        
        int checkDigit2 = generator.calculateCheckDigit("US037833100");
        assertTrue(checkDigit2 >= 0 && checkDigit2 <= 9, "Check digit should be 0-9");
        
        int checkDigit3 = generator.calculateCheckDigit("GB000263494");
        assertTrue(checkDigit3 >= 0 && checkDigit3 <= 9, "Check digit should be 0-9");
        
        // Test that same input always produces same check digit
        assertEquals(checkDigit1, generator.calculateCheckDigit("DE123456789"));
        assertEquals(checkDigit2, generator.calculateCheckDigit("US037833100"));
        assertEquals(checkDigit3, generator.calculateCheckDigit("GB000263494"));
    }
    
    @Test
    void testGenerateWithCountryCode() {
        String isin = generator.generateWithCountryCode("US");
        
        // Should start with the specified country code
        assertTrue(isin.startsWith("US"), "ISIN should start with US");
        
        // Should still be valid format
        assertEquals(12, isin.length());
        assertTrue(isin.matches("^[A-Z]{2}[A-Z0-9]{9}[0-9]$"));
        
        // The check digit should be correct
        String prefix = isin.substring(0, 11);
        int expectedCheckDigit = generator.calculateCheckDigit(prefix);
        int actualCheckDigit = Character.getNumericValue(isin.charAt(11));
        assertEquals(expectedCheckDigit, actualCheckDigit);
    }
    
    @Test
    void testInvalidCountryCodeNull() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            generator.generateWithCountryCode(null);
        });
        assertTrue(exception.getMessage().contains("cannot be null"));
    }
    
    @Test
    void testInvalidCountryCodeEmpty() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            generator.generateWithCountryCode("");
        });
        assertTrue(exception.getMessage().contains("exactly 2 characters"));
    }
    
    @Test
    void testInvalidCountryCodeSingleChar() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            generator.generateWithCountryCode("A");
        });
        assertTrue(exception.getMessage().contains("exactly 2 characters"));
    }
    
    @Test
    void testInvalidCountryCodeLowercase() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            generator.generateWithCountryCode("us");
        });
        assertTrue(exception.getMessage().contains("uppercase letters"));
    }
    
    @Test
    void testInvalidCountryCodeWithNumbers() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            generator.generateWithCountryCode("U1");
        });
        assertTrue(exception.getMessage().contains("uppercase letters"));
    }
    
    @Test
    void testConcurrentGeneration() throws InterruptedException {
        // Test that the generator works correctly when used from multiple threads
        int threadCount = 10;
        int isinsPerThread = 100;
        Set<String> allIsins = ConcurrentHashMap.newKeySet();
        CountDownLatch latch = new CountDownLatch(threadCount);
        
        for (int i = 0; i < threadCount; i++) {
            new Thread(() -> {
                ISINGenerator threadGenerator = new ISINGenerator();
                for (int j = 0; j < isinsPerThread; j++) {
                    String isin = threadGenerator.generate();
                    allIsins.add(isin);
                    
                    // Verify format
                    assertEquals(12, isin.length());
                    assertTrue(isin.matches("^[A-Z]{2}[A-Z0-9]{9}[0-9]$"));
                }
                latch.countDown();
            }).start();
        }
        
        latch.await();
        
        // We should have generated a lot of ISINs
        assertEquals(threadCount * isinsPerThread, allIsins.size(), 
            "All generated ISINs should be unique (or very close to it)");
    }
    
    @Test
    void testCheckDigitEdgeCases() {
        // Test with all letters
        int checkDigit1 = generator.calculateCheckDigit("ABCDEFGHIJK");
        assertTrue(checkDigit1 >= 0 && checkDigit1 <= 9);
        
        // Test with all numbers
        int checkDigit2 = generator.calculateCheckDigit("AA123456789");
        assertTrue(checkDigit2 >= 0 && checkDigit2 <= 9);
        
        // Test with mixed
        int checkDigit3 = generator.calculateCheckDigit("AB1CD2EF3GH");
        assertTrue(checkDigit3 >= 0 && checkDigit3 <= 9);
    }
    
    @Test
    void testInvalidPrefixLength() {
        // Too short
        Exception exception1 = assertThrows(IllegalArgumentException.class, () -> {
            generator.calculateCheckDigit("DE12345678");
        });
        assertTrue(exception1.getMessage().contains("exactly 11 characters"));
        
        // Too long
        Exception exception2 = assertThrows(IllegalArgumentException.class, () -> {
            generator.calculateCheckDigit("DE1234567890");
        });
        assertTrue(exception2.getMessage().contains("exactly 11 characters"));
    }
}
