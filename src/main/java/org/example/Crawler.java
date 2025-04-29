package org.example;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class Crawler {
    private int depth = 0; // Maximum depth to crawl
    private String url;
    String targetLanguage;
    String sourceLanguage;
    private HashSet<String> visitedUrls; // Set of visited URLs
    private HashSet<String> linksFromWebpage;
    private HashSet<String> brokenLinks;

    HashSet<Webpage> crawledPages;

    public Crawler(String url, String sourceLanguage, String targetLanguage, int depth) {
        this.visitedUrls = new HashSet<>();
        this.url = url;
        this.targetLanguage = targetLanguage;
        this.depth = depth;
        this.sourceLanguage = sourceLanguage;
        crawledPages = new HashSet<>();
    }
    public void mainCrawler(String url, int depth)
    {
        Webpage webpage = new Webpage(url,depth,targetLanguage,sourceLanguage);
        crawl(url,depth, webpage);
        //translateAndWriteToFile(webpage);
        Translate translate = new Translate();
        //translate.translateHeaders(webpage.getHeadingsFromWebpage(),targetLanguage,sourceLanguage);
    }

    private void crawl(String url, int depth, Webpage webpage) {
        linksFromWebpage = new HashSet<>();
        brokenLinks = new HashSet<>();
        webpage = new Webpage(url,depth,targetLanguage,sourceLanguage);
        if (depth > this.depth || visitedUrls.contains(url)) {
            return;
        }
        //webpage.setSource(Language.GERMAN);
        //webpage.setTarget(Language.ENGLISH);
        visitedUrls.add(url);
        crawledPages.add(webpage);
        try {
            Document document = Jsoup.connect(url).get();
            Elements hTags = document.select("h1, h2, h3, h4, h5, h6");
            addHeadingsToCrawledWebsite(hTags, webpage);
            Elements links = document.select("a[href]");
            checkWebpageLinks(links,webpage);
            //Recursive call of the crawl Method
            for (Element link : links) {
                crawl(link.absUrl("href"), depth + 1, webpage);
            }
        } catch (Exception e2) {
            System.err.println("Could not crawl " + url + ": " + e2.getMessage());
        }
    }
    private void addHeadingsToCrawledWebsite(Elements hTags, Webpage webpage)
    {
        if(!hTags.select("h1").text().isEmpty()) {
            webpage.addHeadingToWebpage("h1",hTags.select("h1").text());
        }
        if(!hTags.select("h2").text().isEmpty()) {
            webpage.addHeadingToWebpage("h2",hTags.select("h2").text());
        }
        if(!hTags.select("h3").text().isEmpty()) {
            webpage.addHeadingToWebpage("h3",hTags.select("h3").text());
        }
        if(!hTags.select("h4").text().isEmpty()) {
            webpage.addHeadingToWebpage("h4",hTags.select("h4").text());
        }
        if(!hTags.select("h5").text().isEmpty()) {
            webpage.addHeadingToWebpage("h5",hTags.select("h5").text());
        }
        if(!hTags.select("h6").text().isEmpty()) {
            webpage.addHeadingToWebpage("h6",hTags.select("h6").text());
        }
    }
    private void checkWebpageLinks(Elements links, Webpage webpage)
    {
        try {
            for (Element link : links) {
                String href = link.absUrl("href");
                URL linkResponseChecker = new URL(href);
                HttpURLConnection httpLinkResponseChecker = (HttpURLConnection) linkResponseChecker.openConnection();
                httpLinkResponseChecker.setRequestProperty("User-Agent", "curl/7.64.1");
                httpLinkResponseChecker.setConnectTimeout(15000);
                httpLinkResponseChecker.setReadTimeout(15000);
                httpLinkResponseChecker.setRequestMethod("GET"); //this is important, some websites don't allow head request
                if (httpLinkResponseChecker.getResponseCode() == 200) {
                    linksFromWebpage.add(href);
                }
                if (httpLinkResponseChecker.getResponseCode() != 200) {
                    brokenLinks.add(href);
                    //webpage.addBrokenLink(href);
                }
                httpLinkResponseChecker.disconnect();
            }
            webpage.setLinksFromWebpage(linksFromWebpage);
            webpage.setBrokenLinks(brokenLinks);

        }
        catch (Exception ex)
        {
            System.err.println("Error in Crawling: " + url + ": " + ex.getMessage());
        }
    }
    public HashSet<Webpage> getAllWebpages()
    {
        return crawledPages;
    }

}
