package org.example;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class Webpage {
    private HashSet<String> visitedUrls; // Set of visited URLs
    private HashSet<String> linksFromWebpage;
    private HashSet<String> brokenLinks;
    private HashMap<String, String> headingsFromWebpage;

    private String url;

    private int depth;


    public Webpage(String url, int depth) {
        this.url = url;
        this.depth = depth;
        visitedUrls = new HashSet<>();
        brokenLinks = new HashSet<>();
        linksFromWebpage = new HashSet<>();
        headingsFromWebpage = new HashMap<>();
    }

    public HashMap<String, String> getHeadingsFromWebpage() {
        return headingsFromWebpage;
    }

    public void setHeadingsFromWebpage(HashMap<String, String> headingsFromWebpage) {
        this.headingsFromWebpage = headingsFromWebpage;
    }

    public HashSet<String> getVisitedUrls() {
        return visitedUrls;
    }

    public void setVisitedUrls(HashSet<String> visitedUrls) {
        this.visitedUrls = visitedUrls;
    }

    public HashSet<String> getLinksFromWebpage() {
        return linksFromWebpage;
    }

    public void setLinksFromWebpage(HashSet<String> linksFromWebpage) {
        this.linksFromWebpage = linksFromWebpage;
    }

    public HashSet<String> getBrokenLinks() {
        return brokenLinks;
    }

    public void setBrokenLinks(HashSet<String> brokenLinks) {
        this.brokenLinks = brokenLinks;
    }

    public void addHeadingToWebpage(String headingType, String headingContent) {
        this.headingsFromWebpage.put(headingType,headingContent);
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

}