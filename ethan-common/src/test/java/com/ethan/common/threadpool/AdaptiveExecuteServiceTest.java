package com.ethan.common.threadpool;

import com.ethan.common.URL;
import com.ethan.common.util.ExecutorUtils;
import org.junit.Test;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class AdaptiveExecuteServiceTest {

    @Test
    public void testNewAExecutor() {
        URL url = URL.valueOf("ethan://localhost:10010/DemoService?threadname=1");
        String name = ExecutorUtils.setThreadName(url, "Default");
        ExecutorService executor = AdaptiveExecuteService.newDefaultExecutor(name);

        // Submit task and verify
        Future<String> result = executor.submit(() -> {
            // Simulate task execution
            Thread.sleep(500);
            return "Task completed";
        });
        try {
            // Get results and verify them
            String output = result.get();
            assertEquals("Task completed", output);
        } catch (InterruptedException | ExecutionException e) {
            fail("Task execution failed: " + e.getMessage());
        } finally {
            executor.shutdown();
            try {
                if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                    executor.shutdownNow();
                }
            } catch (InterruptedException e) {
                executor.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }

}