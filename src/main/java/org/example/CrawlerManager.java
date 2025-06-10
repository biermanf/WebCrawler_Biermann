package org.example;

import java.util.Collection;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class CrawlerManager {
    private final ThreadPoolManager threadPoolManager;
    private final ConcurrentHashSet<String> visitedUrls;
    private final ConcurrentHashMap<String, Webpage> crawledPages;
    private final AtomicInteger activeThreads;
    private final int maxDepth;
    private final String sourceLanguage;
    private final String targetLanguage;
    private final boolean shouldTranslate;


    public CrawlerManager(int maxThreads, int maxDepth,
                          String sourceLanguage, String targetLanguage,boolean shouldTranslate
    ) {
        this.threadPoolManager = new ThreadPoolManager(maxThreads);
        this.visitedUrls = new ConcurrentHashSet<>();
        this.crawledPages = new ConcurrentHashMap<>();
        this.activeThreads = new AtomicInteger(0);
        this.maxDepth = maxDepth;
        this.sourceLanguage = sourceLanguage;
        this.targetLanguage = targetLanguage;
        this.shouldTranslate = shouldTranslate;

    }

    public Collection<Webpage> startCrawling(String startUrl) {
        CountDownLatch startLatch = new CountDownLatch(1);

        Crawler initialCrawler = new Crawler(
                startUrl, 0, maxDepth,
                visitedUrls,
                threadPoolManager.getExecutorService(),
                crawledPages,
                startLatch,
                activeThreads,
                sourceLanguage,
                targetLanguage,
                shouldTranslate
        );

        threadPoolManager.getExecutorService().submit(initialCrawler);

        try {
            while (activeThreads.get() > 0 || !startLatch.await(100, TimeUnit.MILLISECONDS)) {
                Thread.sleep(100);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            threadPoolManager.shutdown();
        }

        return crawledPages.values();
    }
}