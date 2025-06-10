package org.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CrawlerTest {
    private Crawler crawler;
    private ExecutorService executorService;
    private ConcurrentHashSet<String> visitedUrls;
    private ConcurrentHashMap<String, Webpage> crawledPages;
    private CountDownLatch depthLatch;
    private AtomicInteger activeThreads;

    @BeforeEach
    void setUp() {
        executorService = Executors.newFixedThreadPool(2);
        visitedUrls = new ConcurrentHashSet<>();
        crawledPages = new ConcurrentHashMap<>();
        depthLatch = new CountDownLatch(1);
        activeThreads = new AtomicInteger(0);

        crawler = new Crawler(
                "https://example.com",
                0,
                2,
                visitedUrls,
                executorService,
                crawledPages,
                depthLatch,
                activeThreads,
                "de",
                "en",
                true
        );
    }

    @Test
    @DisplayName("Test Crawler Translation Disabled")
    void testCrawlerWithTranslationDisabled() {
        Crawler noTranslateCrawler = new Crawler(
                "https://example.com",
                0,
                2,
                visitedUrls,
                executorService,
                crawledPages,
                depthLatch,
                activeThreads,
                "de",
                "en",
                false
        );

        assertNotNull(noTranslateCrawler);
        assertEquals(false, getTranslateField(noTranslateCrawler));
    }

    @Test
    @DisplayName("Test Max Depth Limitation")
    void testMaxDepthLimitation() {
        Crawler depthCrawler = new Crawler(
                "https://example.com",
                3,
                2,
                visitedUrls,
                executorService,
                crawledPages,
                depthLatch,
                activeThreads,
                "de",
                "en",
                true
        );

        depthCrawler.run();
        assertTrue(crawledPages.isEmpty());
    }

    @Test
    @DisplayName("Test URL Validation")
    void testUrlValidation() {
        Crawler invalidCrawler = new Crawler(
                "invalid-url",
                0,
                2,
                visitedUrls,
                executorService,
                crawledPages,
                depthLatch,
                activeThreads,
                "de",
                "en",
                true
        );

        invalidCrawler.run();
        assertTrue(crawledPages.isEmpty());
    }

    @Test
    @DisplayName("Test Duplicate URL Handling")
    void testDuplicateUrlHandling() {
        visitedUrls.add("https://example.com");
        crawler.run();
        assertTrue(crawledPages.isEmpty());
    }

    private boolean getTranslateField(Crawler crawler) {
        try {
            Field field = Crawler.class.getDeclaredField("shouldTranslate");
            field.setAccessible(true);
            return (boolean) field.get(crawler);
        } catch (Exception e) {
            return true;
        }
    }
}