package com.solvians.showcase;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import static org.junit.jupiter.api.Assertions.*;

class CertificateUpdateTest {
    
    private ISINGenerator isinGenerator;
    private CertificateUpdate certificateUpdate;
    
    @BeforeEach
    void setUp() {
        isinGenerator = new ISINGenerator();
        certificateUpdate = new CertificateUpdate(isinGenerator);
    }
    
    @Test
    void testLineFormat() throws Exception {
        String line = certificateUpdate.call();
        
        // Should have exactly 6 comma-separated fields
        String[] fields = line.split(",");
        assertEquals(6, fields.length, "Certificate update should have 6 fields");
        
        // Verify no extra whitespace
        assertFalse(line.contains(" "), "Line should not contain spaces");
    }
    
    @Test
    void testFieldRanges() throws Exception {
        // Test multiple times to catch range violations
        for (int i = 0; i < 100; i++) {
            String line = certificateUpdate.call();
            String[] fields = line.split(",");
            
            // Parse the fields
            long timestamp = Long.parseLong(fields[0]);
            String isin = fields[1];
            double bidPrice = Double.parseDouble(fields[2]);
            int bidSize = Integer.parseInt(fields[3]);
            double askPrice = Double.parseDouble(fields[4]);
            int askSize = Integer.parseInt(fields[5]);
            
            // Verify timestamp is reasonable (within last 10 seconds)
            long now = System.currentTimeMillis();
            assertTrue(timestamp <= now && timestamp >= now - 10000, 
                "Timestamp should be recent");
            
            // Verify ISIN format
            assertEquals(12, isin.length(), "ISIN should be 12 characters");
            assertTrue(isin.matches("^[A-Z]{2}[A-Z0-9]{9}[0-9]$"), 
                "ISIN should match expected format");
            
            // Verify bid price range
            assertTrue(bidPrice >= 100.00 && bidPrice <= 200.00, 
                "Bid price should be between 100.00 and 200.00, got: " + bidPrice);
            
            // Verify bid size range
            assertTrue(bidSize >= 1000 && bidSize <= 5000, 
                "Bid size should be between 1000 and 5000, got: " + bidSize);
            
            // Verify ask price range
            assertTrue(askPrice >= 100.00 && askPrice <= 200.00, 
                "Ask price should be between 100.00 and 200.00, got: " + askPrice);
            
            // Verify ask size range
            assertTrue(askSize >= 1000 && askSize <= 10000, 
                "Ask size should be between 1000 and 10000, got: " + askSize);
        }
    }
    
    @Test
    void testPriceFormatting() throws Exception {
        // Test that prices always have exactly 2 decimal places
        for (int i = 0; i < 100; i++) {
            String line = certificateUpdate.call();
            String[] fields = line.split(",");
            
            String bidPriceStr = fields[2];
            String askPriceStr = fields[4];
            
            // Both prices should match the pattern: digits.2digits
            assertTrue(bidPriceStr.matches("^\\d+\\.\\d{2}$"), 
                "Bid price should have exactly 2 decimal places, got: " + bidPriceStr);
            assertTrue(askPriceStr.matches("^\\d+\\.\\d{2}$"), 
                "Ask price should have exactly 2 decimal places, got: " + askPriceStr);
        }
    }
    
    @Test
    void testSizeFormatting() throws Exception {
        // Test that sizes are integers with no decimal points
        for (int i = 0; i < 50; i++) {
            String line = certificateUpdate.call();
            String[] fields = line.split(",");
            
            String bidSizeStr = fields[3];
            String askSizeStr = fields[5];
            
            // Should be pure integers
            assertTrue(bidSizeStr.matches("^\\d+$"), 
                "Bid size should be an integer with no decimal point");
            assertTrue(askSizeStr.matches("^\\d+$"), 
                "Ask size should be an integer with no decimal point");
            
            // Should not have thousand separators
            assertFalse(bidSizeStr.contains(","), "Bid size should not have thousand separators");
            assertFalse(askSizeStr.contains(","), "Ask size should not have thousand separators");
        }
    }
    
    @Test
    void testNullISINGenerator() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new CertificateUpdate(null);
        });
        assertTrue(exception.getMessage().contains("cannot be null"));
    }
    
    @Test
    void testMultipleCalls() throws Exception {
        // Each call should produce a different line (different timestamp at minimum)
        String line1 = certificateUpdate.call();
        Thread.sleep(2); // Small delay to ensure different timestamp
        String line2 = certificateUpdate.call();
        
        assertNotEquals(line1, line2, "Multiple calls should produce different results");
    }
}
