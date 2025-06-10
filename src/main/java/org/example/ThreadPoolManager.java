package org.example;

import java.util.concurrent.*;

public class ThreadPoolManager {
    private final ExecutorService executorService;
    private static final int QUEUE_CAPACITY = 1000;

    public ThreadPoolManager(int maxThreads) {
        BlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<>(QUEUE_CAPACITY);
        this.executorService = new ThreadPoolExecutor(
                maxThreads,
                maxThreads,
                0L,
                TimeUnit.MILLISECONDS,
                workQueue,
                new ThreadPoolExecutor.CallerRunsPolicy() // Wichtig: Verhindert Task-Rejection
        );
    }

    public ExecutorService getExecutorService() {
        return executorService;
    }

    public void shutdown() {
        executorService.shutdown();
    }
}