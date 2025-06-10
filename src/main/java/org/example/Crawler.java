package org.example;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashSet;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Crawler implements Runnable {
    private final String url;
    private final int currentDepth;
    private final int maxDepth;
    private final ConcurrentHashSet<String> visitedUrls;
    private final ExecutorService executorService;
    private final ConcurrentHashMap<String, Webpage> crawledPages;
    private final CountDownLatch depthLatch;
    private final AtomicInteger activeThreads;
    private final String sourceLanguage;
    private final String targetLanguage;
    private final boolean shouldTranslate;


    public Crawler(String url, int currentDepth, int maxDepth,
                   ConcurrentHashSet<String> visitedUrls,
                   ExecutorService executorService,
                   ConcurrentHashMap<String, Webpage> crawledPages,
                   CountDownLatch depthLatch,
                   AtomicInteger activeThreads,
                   String sourceLanguage,
                   String targetLanguage,
                   boolean shouldTranslate
    ) {
        this.url = url;
        this.currentDepth = currentDepth;
        this.maxDepth = maxDepth;
        this.visitedUrls = visitedUrls;
        this.executorService = executorService;
        this.crawledPages = crawledPages;
        this.depthLatch = depthLatch;
        this.activeThreads = activeThreads;
        this.sourceLanguage = sourceLanguage;
        this.targetLanguage = targetLanguage;
        this.shouldTranslate = shouldTranslate;
    }

    @Override
    public void run() {
        try {
            if (currentDepth > maxDepth || !visitedUrls.add(url)) {
                return;
            }

            activeThreads.incrementAndGet();
            processPage();
        } finally {
            activeThreads.decrementAndGet();
            depthLatch.countDown();
        }
    }

    private void processPage() {
        try {
            Document document = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0")
                    .timeout(5000)
                    .get();

            Webpage webpage = new Webpage(url, currentDepth, sourceLanguage, targetLanguage);
            processHeaders(document, webpage);
            processLinks(document, webpage);

            if (shouldTranslate && !sourceLanguage.equals(targetLanguage)) {
                Translate translator = new Translate();
                translator.translateHeaders(webpage, targetLanguage, sourceLanguage);
            }


            crawledPages.put(url, webpage);

        } catch (Exception e) {
            System.err.println("Error processing " + url + ": " + e.getMessage());
        }
    }

    private void processHeaders(Document document, Webpage webpage) {
        Elements headers = document.select("h1, h2, h3, h4, h5, h6");
        for (Element header : headers) {
            String headerTag = header.tagName();
            webpage.addHeadingToWebpage(headerTag, header.text());
        }
    }

    private void processLinks(Document document, Webpage webpage) {
        Elements links = document.select("a[href]");
        ConcurrentHashSet<String> pageLinks = new ConcurrentHashSet<>();
        int validLinksCount = 0;

        for (Element link : links) {
            String href = link.absUrl("href");
            if (URLValidator.checkIfValidUrl(href)) {
                pageLinks.add(href);
                if (currentDepth < maxDepth) {
                    if (validLinksCount < 50) {
                        submitNewCrawlerTask(href, null);
                        validLinksCount++;
                    }
                }
            }
        }

        webpage.setLinksFromWebpage(new HashSet<>(pageLinks.getSet()));
    }

    private void submitNewCrawlerTask(String newUrl, CountDownLatch linkLatch) {
        Crawler newCrawler = new Crawler(
                newUrl,
                currentDepth + 1,
                maxDepth,
                visitedUrls,
                executorService,
                crawledPages,
                linkLatch,
                activeThreads,
                sourceLanguage,
                targetLanguage,
                shouldTranslate
        );
        executorService.submit(newCrawler);
    }
}