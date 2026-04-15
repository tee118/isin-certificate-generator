package com.solvians.showcase;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Generates valid ISIN strings according to the ISO 6166 standard.
 * An ISIN consists of a 2-letter country code, a 9-character alphanumeric identifier,
 * and a single check digit calculated using a Luhn-like algorithm.
 * 
 * Thread-safe - uses ThreadLocalRandom and has no mutable state.
 */
public class ISINGenerator {
    
    private static final String UPPERCASE_LETTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String ALPHANUMERIC = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    
    /**
     * Generates a complete ISIN with a random country code and body.
     */
    public String generate() {
        String countryCode = generateRandomCountryCode();
        return generateWithCountryCode(countryCode);
    }
    
    /**
     * Generates an ISIN using the provided country code.
     * Useful for testing or when you need ISINs from a specific country.
     */
    public String generateWithCountryCode(String countryCode) {
        validateCountryCode(countryCode);
        
        String body = generateRandomBody();
        String isinPrefix = countryCode + body;
        int checkDigit = calculateCheckDigit(isinPrefix);
        
        return isinPrefix + checkDigit;
    }
    
    /**
     * Calculates the ISIN check digit using the Luhn algorithm.
     * 
     * The algorithm works as follows:
     * 1. Convert letters to numbers (A=10, B=11, ... Z=35)
     * 2. Starting from the right, double every second digit
     * 3. If doubling produces a two-digit number, add those digits together
     * 4. Sum all the digits
     * 5. The check digit makes the total a multiple of 10
     */
    int calculateCheckDigit(String isinPrefix) {
        if (isinPrefix == null || isinPrefix.length() != 11) {
            throw new IllegalArgumentException("ISIN prefix must be exactly 11 characters");
        }
        
        // Convert the ISIN prefix to a string of digits
        StringBuilder numericString = new StringBuilder();
        for (char c : isinPrefix.toCharArray()) {
            if (Character.isDigit(c)) {
                numericString.append(c);
            } else if (Character.isUpperCase(c)) {
                // A=10, B=11, etc.
                int value = c - 'A' + 10;
                numericString.append(value);
            } else {
                throw new IllegalArgumentException("ISIN prefix must contain only uppercase letters and digits");
            }
        }
        
        // Apply the Luhn algorithm: double every second digit from the right
        String digits = numericString.toString();
        int sum = 0;
        boolean shouldDouble = false;
        
        for (int i = digits.length() - 1; i >= 0; i--) {
            int digit = Character.getNumericValue(digits.charAt(i));
            
            if (shouldDouble) {
                digit *= 2;
                // If we get a two-digit number, add the digits together
                if (digit > 9) {
                    digit = (digit / 10) + (digit % 10);
                }
            }
            
            sum += digit;
            shouldDouble = !shouldDouble;
        }
        
        // The check digit is whatever makes the sum a multiple of 10
        return (10 - (sum % 10)) % 10;
    }
    
    private String generateRandomCountryCode() {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        char first = UPPERCASE_LETTERS.charAt(random.nextInt(UPPERCASE_LETTERS.length()));
        char second = UPPERCASE_LETTERS.charAt(random.nextInt(UPPERCASE_LETTERS.length()));
        return "" + first + second;
    }
    
    private String generateRandomBody() {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        StringBuilder body = new StringBuilder(9);
        for (int i = 0; i < 9; i++) {
            char c = ALPHANUMERIC.charAt(random.nextInt(ALPHANUMERIC.length()));
            body.append(c);
        }
        return body.toString();
    }
    
    private void validateCountryCode(String countryCode) {
        if (countryCode == null) {
            throw new IllegalArgumentException("Country code cannot be null");
        }
        if (countryCode.length() != 2) {
            throw new IllegalArgumentException("Country code must be exactly 2 characters, got: " + countryCode.length());
        }
        if (!countryCode.matches("[A-Z]{2}")) {
            throw new IllegalArgumentException("Country code must contain only uppercase letters, got: " + countryCode);
        }
    }
}
