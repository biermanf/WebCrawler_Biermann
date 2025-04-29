package org.example;

public class LanguageChecker {
    public static String checkLanguage(String language) {
        switch (language) {
            case "en":
                return "en";
            case "de":
                return "de";
            default:
                return "en";
        }
    }
}
