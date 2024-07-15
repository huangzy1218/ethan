package com.ethan.common.threadpool;

import java.util.concurrent.*;

import static com.ethan.common.constant.CommonConstants.DEFAULT_CORE_POOL_SIZE;
import static com.ethan.common.constant.CommonConstants.DEFAULT_KEEP_ALIVE_TIME;

/**
 * Custom executor service.
 *
 * @author Huang Z.Y.
 */
public class AdaptiveExecuteService implements Executor {

    private final ExecutorService executorService;

    public AdaptiveExecuteService(int corePoolSize, int maxPoolSize, long keepAliveTime, TimeUnit timeUnit, BlockingQueue<Runnable> workQueue) {
        this.executorService = new ThreadPoolExecutor(
                corePoolSize,
                maxPoolSize,
                keepAliveTime,
                timeUnit,
                workQueue,
                new ThreadPoolExecutor.AbortPolicy()
        );
    }

    public static ExecutorService newDefaultExecutor(String name) {
        BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<>();
        // Create a thread factory to set the thread name
        ThreadFactory threadFactory = new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r, name);
            }
        };
        // Create a ThreadPoolExecutor using a custom thread factory
        return new ThreadPoolExecutor(
                DEFAULT_CORE_POOL_SIZE,
                DEFAULT_CORE_POOL_SIZE * 2,
                DEFAULT_KEEP_ALIVE_TIME,
                TimeUnit.SECONDS,
                workQueue,
                threadFactory,
                new ThreadPoolExecutor.AbortPolicy()
        );
    }

    @Override
    public void execute(Runnable command) {
        executorService.execute(command);
    }

    public void shutdown() {
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    public boolean isShutdown() {
        return executorService.isShutdown();
    }

}
