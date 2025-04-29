package org.example;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class URLValidator {
    private static final String URL_REGEX =
            "^(http|https)://" +                 // Protokoll
                    "([a-zA-Z0-9]([a-zA-Z0-9\\-]*[a-zA-Z0-9])?\\.)+" + // Subdomains
                    "[a-zA-Z]{2,}" +                    // Top-Level-Domain
                    "(:\\d{1,5})?" +                    // Optionaler Port
                    "(/.*)?" +                          // Optionaler Pfad
                    "$";

    private static final Pattern URL_PATTERN = Pattern.compile(URL_REGEX);

    public static boolean checkIfValidUrl(String url) {
        if (url == null || url.isEmpty()) {
            return false;
        }
        Matcher matcher = URL_PATTERN.matcher(url);
        return matcher.matches();
    }
}