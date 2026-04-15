package com.solvians.showcase;

import org.junit.jupiter.api.Test;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class CertificateUpdateGeneratorTest {

    @Test
    public void testGenerateQuotes() {
        CertificateUpdateGenerator certificateUpdateGenerator = new CertificateUpdateGenerator(10, 100);
        Stream<CertificateUpdate> quotes = certificateUpdateGenerator.generateQuotes();
        assertNotNull(quotes);
        
        // The generator should produce exactly threads * quotes items
        assertEquals(100, quotes.count());
    }
    
    @Test
    public void testGenerateQuotesSmall() {
        CertificateUpdateGenerator certificateUpdateGenerator = new CertificateUpdateGenerator(2, 5);
        Stream<CertificateUpdate> quotes = certificateUpdateGenerator.generateQuotes();
        assertNotNull(quotes);
        
        // Should produce exactly 5 items
        assertEquals(5, quotes.count());
    }
}
