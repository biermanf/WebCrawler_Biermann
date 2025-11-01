package org.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;


class WebpageTest {

    private Webpage webpage;
    private static final String TEST_URL = "https://example.com";
    private static final int TEST_DEPTH = 2;

    @BeforeEach
    void setUp() {
        webpage = new Webpage(TEST_URL, TEST_DEPTH);
    }

    @Test
    void constructorShouldInitialize() {
        assertEquals(TEST_URL, webpage.getUrl());
        assertEquals(TEST_DEPTH, webpage.getDepth());
        assertTrue(webpage.getVisitedUrls().isEmpty());
        assertTrue(webpage.getBrokenLinks().isEmpty());
        assertTrue(webpage.getLinksFromWebpage().isEmpty());
        assertTrue(webpage.getHeadingsFromWebpage().isEmpty());
    }

    @Test
    void addHeadingToWebpage() {
        String headingType = "h1";
        String headingContent = "Test Überschrift";
        webpage.addHeadingToWebpage(headingType, headingContent);
        assertEquals(1, webpage.getHeadingsFromWebpage().size());
        assertEquals(headingContent, webpage.getHeadingsFromWebpage().get(headingType));
    }

    @Test
    void setLinksFromWebpageShouldUpdateLinks() {
        HashSet<String> links = new HashSet<>();
        links.add("https://example.com/page1");
        links.add("https://example.com/page2");
        webpage.setLinksFromWebpage(links);
        assertEquals(links, webpage.getLinksFromWebpage());
        assertEquals(2, webpage.getLinksFromWebpage().size());
    }

    @Test
    void setBrokenLinks() {
        // Arrange
        HashSet<String> brokenLinks = new HashSet<>();
        brokenLinks.add("https://example.com/broken1");
        webpage.setBrokenLinks(brokenLinks);
        assertEquals(brokenLinks, webpage.getBrokenLinks());
        assertEquals(1, webpage.getBrokenLinks().size());
    }


}
