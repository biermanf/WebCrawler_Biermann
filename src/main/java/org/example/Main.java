package org.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collection;

public class Main {
    public static void main(String[] args) {
            System.out.println("Enter your URL like: http://www.example.com");
            int depth = 2;
            String url = "";

            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            try {
                url = reader.readLine();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            if (URLValidator.checkIfValidUrl(url)) {

                int maxThreads = Runtime.getRuntime().availableProcessors();

                CrawlerManager manager = new CrawlerManager(
                        maxThreads,
                        depth
                );

                manager.startCrawling(url);
                Collection<Webpage> results = manager.getCrawledPages().values();


                MDFileOperator fileOperator;
                try {
                    fileOperator = new MDFileOperator();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                fileOperator.generateFileWithContent(results);
                }
    }
}