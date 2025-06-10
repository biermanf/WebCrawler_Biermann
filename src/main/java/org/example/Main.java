package org.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collection;

public class Main {
    public static void main(String[] args) {
            System.out.println("Enter your URL like: http://www.example.com");
            int depth = 0;
            String url = "";
            String sourceLanguage;
            String targetLanguage;

            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            try {
                url = reader.readLine();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            if (URLValidator.checkIfValidUrl(url)) {

                System.out.println("Enter your targetLanguage");
                try {
                    targetLanguage = LanguageChecker.checkLanguage(reader.readLine());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                System.out.println("Enter your sourceLanguage");

                try {
                    sourceLanguage = LanguageChecker.checkLanguage(reader.readLine());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                int maxThreads = Runtime.getRuntime().availableProcessors();
                int maxDepth = 2;
                boolean shouldTranslate = false;

                CrawlerManager manager = new CrawlerManager(
                        maxThreads,
                        maxDepth,
                        sourceLanguage,
                        targetLanguage,
                        shouldTranslate
                );

                Collection<Webpage> results = manager.startCrawling(url);

                MDFileOperator fileOperator = null;
                try {
                    fileOperator = new MDFileOperator();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                fileOperator.generateFileWithContent(results);
                }
    }
}