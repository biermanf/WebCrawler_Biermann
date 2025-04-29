package org.example;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class URLValidatorTest {

    @Test
    void checkIfValidUrl_validHttpUrl_returnsTrue() {
        String url = "http://example.com";
        boolean result = URLValidator.checkIfValidUrl(url);
        assertTrue(result);
    }

    @Test
    void checkIfValidUrl_validHttpsUrl_returnsTrue() {
        String url = "https://example.com";
        boolean result = URLValidator.checkIfValidUrl(url);
        assertTrue(result);
    }

    @Test
    void checkIfValidUrl_validUrlWithSubdomain_returnsTrue() {
        String url = "https://sub.example.com";
        boolean result = URLValidator.checkIfValidUrl(url);
        assertTrue(result);
    }


    @Test
    void checkIfValidUrl_missingProtocol_returnsFalse() {
        String url = "example.com";
        boolean result = URLValidator.checkIfValidUrl(url);
        assertFalse(result);
    }

    @Test
    void checkIfValidUrl_nullInput_returnsFalse() {
        String url = null;
        boolean result = URLValidator.checkIfValidUrl(url);
        assertFalse(result);
    }

    @Test
    void checkIfValidUrl_emptyInput_returnsFalse() {
        String url = "";
        boolean result = URLValidator.checkIfValidUrl(url);
        assertFalse(result);
    }

    @Test
    void checkIfValidUrl_invalidCharacters_returnsFalse() {
        String url = "http://example!.com";
        boolean result = URLValidator.checkIfValidUrl(url);
        assertFalse(result);
    }
}
