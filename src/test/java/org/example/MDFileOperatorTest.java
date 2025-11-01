package org.example;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class MDFileOperatorTest {
    private MDFileOperator mdFileOperator;
    private Collection<Webpage> webpages;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() throws IOException {
        System.setProperty("user.dir", tempDir.toString());
        mdFileOperator = new MDFileOperator();
        webpages = new ArrayList<>();
    }

    @Test
    @DisplayName("Test Multiple Webpages Write")
    void testMultipleWebpagesWrite() throws IOException {
        // Arrange
        Webpage webpage1 = new Webpage("https://example1.com", 1);
        Webpage webpage2 = new Webpage("https://example2.com", 2);
        webpages.add(webpage1);
        webpages.add(webpage2);

        // Act
        mdFileOperator.generateFileWithContent(webpages);

        // Assert
        List<String> lines = Files.readAllLines(Path.of("pagereport.md"));
        String content = String.join("\n", lines);
        assertTrue(content.contains("example1.com"));
        assertTrue(content.contains("example2.com"));
    }

    @Test
    @DisplayName("Test Empty Collection")
    void testEmptyCollection() throws IOException {
        // Act
        mdFileOperator.generateFileWithContent(webpages);

        // Assert
        File outputFile = new File("pagereport.md");
        assertTrue(outputFile.exists());
        assertEquals(0, outputFile.length());
    }

    @Test
    @DisplayName("Test Webpage With Headers")
    void testWebpageWithHeaders() throws IOException {
        // Arrange
        Webpage webpage = new Webpage("https://example.com", 1);
        HashMap<String, String> headers = new HashMap<>();
        headers.put("h1", "Hauptüberschrift");
        headers.put("h2", "Unterüberschrift");
        webpage.setHeadingsFromWebpage(headers);
        webpages.add(webpage);

        // Act
        mdFileOperator.generateFileWithContent(webpages);

        // Assert
        List<String> lines = Files.readAllLines(Path.of("pagereport.md"));
        String content = String.join("\n", lines);
        assertTrue(content.contains("Hauptüberschrift"));
        assertTrue(content.contains("Unterüberschrift"));
    }

    @Test
    @DisplayName("Test File Separator")
    void testFileSeparator() throws IOException {
        // Arrange
        Webpage webpage1 = new Webpage("https://example1.com", 1);
        Webpage webpage2 = new Webpage("https://example2.com", 2);
        webpages.add(webpage1);
        webpages.add(webpage2);

        // Act
        mdFileOperator.generateFileWithContent(webpages);

        // Assert
        List<String> lines = Files.readAllLines(Path.of("pagereport.md"));
        String content = String.join("\n", lines);
        assertTrue(content.contains("---"));
    }

    @AfterEach
    void cleanup() throws IOException {
        mdFileOperator.fileWriter.close();
    }
}