package org.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.jsoup.nodes.Document;
import org.jsoup.Jsoup;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@ExtendWith(MockitoExtension.class)
public class CrawlerManagerConcurrencyTest {
    private CrawlerManager crawlerManager;

    @Mock
    private JsoupDocumentFetcher mockFetcher;

    @BeforeEach
    void setUp() throws IOException {
        // Erstelle ein einfaches Mock-Dokument
        Document mockDocument = Jsoup.parse("<html><body><p>Test Content</p></body></html>");

        // Konfiguriere den Mock für alle URLs
        when(mockFetcher.fetchDocument(anyString())).thenReturn(mockDocument);

        crawlerManager = new CrawlerManager(1, 4, mockFetcher);
    }

    @Test
    void testConcurrentExecution() throws InterruptedException {
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch completionLatch = new CountDownLatch(3);

        String[] testUrls = {
                "https://www.wikipedia.org",
                "https://www.github.com",
                "https://www.stackoverflow.com"
        };

        ExecutorService testExecutor = Executors.newFixedThreadPool(3);
        for (String url : testUrls) {
            testExecutor.submit(() -> {
                try {
                    startLatch.await();
                    crawlerManager.startCrawling(url);
                    completionLatch.countDown();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }

        startLatch.countDown();

        assertTrue(completionLatch.await(5, TimeUnit.SECONDS),
                "Mocking Sites - test should be fast");

        assertEquals(3, crawlerManager.getCrawledPages().size(),
                "Every Site crawled");

        testExecutor.shutdown();
        assertTrue(testExecutor.awaitTermination(1, TimeUnit.SECONDS));
    }
}