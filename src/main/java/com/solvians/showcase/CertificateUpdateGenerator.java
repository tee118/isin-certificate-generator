package com.solvians.showcase;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Stream;

/**
 * Generates certificate updates using a thread pool.
 * 
 * Note: This class is kept for backward compatibility with existing tests,
 * but the main logic has been moved to App.java for simplicity.
 */
public class CertificateUpdateGenerator {
    private final int threads;
    private final int quotes;

    public CertificateUpdateGenerator(int threads, int quotes) {
        this.threads = threads;
        this.quotes = quotes;
    }

    /**
     * Generates certificate updates using multiple threads.
     * 
     * @return a stream of CertificateUpdate objects
     */
    public Stream<CertificateUpdate> generateQuotes() {
        ExecutorService executor = Executors.newFixedThreadPool(threads);
        ISINGenerator isinGenerator = new ISINGenerator();
        
        try {
            // Create tasks
            List<Callable<CertificateUpdate>> tasks = new ArrayList<>();
            for (int i = 0; i < quotes; i++) {
                tasks.add(() -> new CertificateUpdate(isinGenerator));
            }
            
            // Execute and collect results
            List<Future<CertificateUpdate>> futures = executor.invokeAll(tasks);
            List<CertificateUpdate> results = new ArrayList<>();
            
            for (Future<CertificateUpdate> future : futures) {
                results.add(future.get());
            }
            
            return results.stream();
            
        } catch (Exception e) {
            throw new RuntimeException("Error generating quotes", e);
        } finally {
            executor.shutdown();
        }
    }
}
