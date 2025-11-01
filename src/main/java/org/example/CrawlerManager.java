package org.example;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class CrawlerManager {
    private final ExecutorService executorService;
    private final ConcurrentHashMap<String, Webpage> crawledPages;
    private final ConcurrentHashSet<String> visitedUrls;
    private final AtomicInteger activeThreads;
    private final JsoupDocumentFetcher documentFetcher;
    private final int maxDepth;

    public CrawlerManager(int maxDepth, int threadPoolSize) {
        this.maxDepth = maxDepth;
        this.executorService = Executors.newFixedThreadPool(threadPoolSize);
        this.crawledPages = new ConcurrentHashMap<>();
        this.visitedUrls = new ConcurrentHashSet<>();
        this.activeThreads = new AtomicInteger(0);
        this.documentFetcher = new JsoupDocumentFetcher();
    }

    public CrawlerManager(int maxDepth, int threadPoolSize, JsoupDocumentFetcher documentFetcher) {
        this.maxDepth = maxDepth;
        this.executorService = Executors.newFixedThreadPool(threadPoolSize);
        this.crawledPages = new ConcurrentHashMap<>();
        this.visitedUrls = new ConcurrentHashSet<>();
        this.activeThreads = new AtomicInteger(0);
        this.documentFetcher = documentFetcher;
    }

    public void startCrawling(String startUrl) {
        CountDownLatch initialLatch = new CountDownLatch(1);
        Crawler initialCrawler = new Crawler(
                startUrl,
                0,
                maxDepth,
                visitedUrls,
                executorService,
                crawledPages,
                initialLatch,
                activeThreads,
                documentFetcher
        );

        executorService.submit(initialCrawler);

        try {
            while (!isProcessingComplete(initialLatch)) {
                Thread.sleep(100);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Error in Crawling: " + e.getMessage());
        } finally {
            shutdownAndAwaitTermination();
        }
    }

    private boolean isProcessingComplete(CountDownLatch latch) {
        return latch.getCount() == 0 && activeThreads.get() == 0;
    }

    private void shutdownAndAwaitTermination() {
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
                if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
                    System.err.println("Thread cancellation failed");
                }
            }
        } catch (InterruptedException ie) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    public Map<String, Webpage> getCrawledPages() {
        return new HashMap<>(crawledPages);
    }
}
