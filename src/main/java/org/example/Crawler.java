package org.example;

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
    private final JsoupDocumentFetcher documentFetcher;

    public Crawler(String url,
                   int currentDepth,
                   int maxDepth,
                   ConcurrentHashSet<String> visitedUrls,
                   ExecutorService executorService,
                   ConcurrentHashMap<String, Webpage> crawledPages,
                   CountDownLatch depthLatch,
                   AtomicInteger activeThreads,
                   JsoupDocumentFetcher documentFetcher) {
        this.url = url;
        this.currentDepth = currentDepth;
        this.maxDepth = maxDepth;
        this.visitedUrls = visitedUrls;
        this.executorService = executorService;
        this.crawledPages = crawledPages;
        this.depthLatch = depthLatch;
        this.activeThreads = activeThreads;
        this.documentFetcher = documentFetcher;
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
            Document document = documentFetcher.fetchDocument(url);

            Webpage webpage = new Webpage(url, currentDepth);
            processHeaders(document, webpage);
            processLinks(document, webpage);

            crawledPages.put(url, webpage);

        } catch (Exception e) {
            System.err.println("Error processing " + url + ": " + e.getMessage());
        }
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
                documentFetcher
        );
        executorService.submit(newCrawler);
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
}