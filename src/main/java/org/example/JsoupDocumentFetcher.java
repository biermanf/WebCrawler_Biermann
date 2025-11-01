package org.example;

import org.jsoup.nodes.Document;

import java.io.IOException;

public interface JsoupDocumentFetcher {
    Document fetchDocument(String url) throws IOException;
}

