package com.solvians.showcase;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Main application for generating certificate updates using multiple threads.
 * 
 * Usage: java App <numberOfThreads> <numberOfCertificateUpdates>
 * Example: java App 4 100
 */
public class App {
    
    public static void main(String[] args) {
        // Validate we have the required arguments
        if (args.length < 2) {
            throw new RuntimeException("Expected 2 arguments: numberOfThreads and numberOfCertificateUpdates. But got: " + args.length);
        }
        
        // Parse the arguments
        int threads = Integer.parseInt(args[0]);
        int quotes = Integer.parseInt(args[1]);
        
        // Validate the values are positive
        if (threads <= 0 || quotes <= 0) {
            throw new IllegalArgumentException("Both numberOfThreads and numberOfCertificateUpdates must be positive");
        }
        
        // Create a thread pool with the specified number of threads
        ExecutorService executor = Executors.newFixedThreadPool(threads);
        
        try {
            // Create the ISIN generator (can be shared across threads)
            ISINGenerator isinGenerator = new ISINGenerator();
            
            // Create a list of tasks - one for each certificate update we need
            List<Callable<String>> tasks = new ArrayList<>();
            for (int i = 0; i < quotes; i++) {
                tasks.add(new CertificateUpdate(isinGenerator));
            }
            
            // Execute all tasks and collect the results
            List<Future<String>> futures = executor.invokeAll(tasks);
            
            // Print each generated certificate update line
            for (Future<String> future : futures) {
                System.out.println(future.get());
            }
            
        } catch (Exception e) {
            System.err.println("Error generating certificate updates: " + e.getMessage());
            throw new RuntimeException(e);
        } finally {
            // Always shut down the executor to free resources
            executor.shutdown();
        }
    }
}
