package org.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

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
            System.out.println("Enter your depth");
            try {
                depth = Integer.parseInt(reader.readLine());
            } catch (Exception e) {
                System.out.println("Enter a valid number");
                System.exit(0);
            }
            try {
                Crawler crawler = new Crawler(url, sourceLanguage, targetLanguage, depth);
                crawler.mainCrawler(url, 0);
                Translate translate = new Translate();
                MDFileOperator mdFileOperator = new MDFileOperator();
                for (Webpage webpage:crawler.getAllWebpages()) {
                    translate.translateHeaders(webpage,targetLanguage,sourceLanguage);
                    mdFileOperator.generateFileWithContent(webpage);
                }
            } catch (Exception e)
            {
                System.out.println("Error in Crawling: " + e.getMessage());
            }
        }
        else
        {
            System.out.println("Wrong URL type");
        }
    }
}