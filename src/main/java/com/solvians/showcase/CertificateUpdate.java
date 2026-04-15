package com.solvians.showcase;

import java.util.concurrent.Callable;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Generates a single certificate update line in CSV format.
 * 
 * Each line contains: timestamp, ISIN, bid price, bid size, ask price, ask size
 * Example: 1352122280502,DE1234567896,101.23,1000,103.45,1000
 * 
 * Thread-safe - uses ThreadLocalRandom and has no mutable state.
 */
public class CertificateUpdate implements Callable<String> {
    
    private final ISINGenerator isinGenerator;
    
    public CertificateUpdate(ISINGenerator isinGenerator) {
        if (isinGenerator == null) {
            throw new IllegalArgumentException("ISINGenerator cannot be null");
        }
        this.isinGenerator = isinGenerator;
    }
    
    /**
     * Generates one certificate update line.
     * 
     * @return a CSV line with timestamp, ISIN, bid price, bid size, ask price, ask size
     */
    @Override
    public String call() {
        long timestamp = System.currentTimeMillis();
        String isin = isinGenerator.generate();
        
        ThreadLocalRandom random = ThreadLocalRandom.current();
        
        // Bid price: 100.00 to 200.00 inclusive, with 2 decimal places
        double bidPrice = random.nextDouble(100.00, 200.01);
        
        // Bid size: 1000 to 5000 inclusive
        int bidSize = random.nextInt(1000, 5001);
        
        // Ask price: 100.00 to 200.00 inclusive, with 2 decimal places
        double askPrice = random.nextDouble(100.00, 200.01);
        
        // Ask size: 1000 to 10000 inclusive
        int askSize = random.nextInt(1000, 10001);
        
        // Format the line - prices must have exactly 2 decimal places
        return String.format("%d,%s,%.2f,%d,%.2f,%d",
            timestamp, isin, bidPrice, bidSize, askPrice, askSize);
    }
}
