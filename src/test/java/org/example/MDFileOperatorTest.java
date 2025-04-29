package org.example;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MDFileOperatorTest {

    private MDFileOperator mdFileOperator;
    private Webpage testWebpage;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() throws IOException {
        System.setProperty("user.dir", tempDir.toString());

        mdFileOperator = new MDFileOperator();
        testWebpage = new Webpage("https://example.com", 2, "EN", "DE");
    }

    @AfterEach
    void shutdown() throws IOException {
        mdFileOperator.fileWriter.close();
    }

    @Test
    void generateFileWithContentShouldCreateFile() throws IOException {
        // Act
        mdFileOperator.generateFileWithContent(testWebpage);

        // Assert
        File outputFile = new File("pagereport.md");
        assertTrue(outputFile.exists());
        assertTrue(outputFile.length() > 0);
    }

    @Test
    void generateFileWithContentShouldIncludeMainData() throws IOException {
        // Act
        mdFileOperator.generateFileWithContent(testWebpage);

        // Assert
        List<String> lines = Files.readAllLines(Path.of("pagereport.md"));
        String content = String.join("\n", lines);

        assertTrue(content.contains("Website: https://example.com"));
        assertTrue(content.contains("Depth: 2"));
        assertTrue(content.contains("Source Language: DE"));
        assertTrue(content.contains("Target Language: EN"));
    }

    @Test
    void generateFileWithContentShouldIncludeLinks() throws IOException {
        // Arrange
        HashSet<String> links = new HashSet<>();
        links.add("https://example.com/valid1");
        links.add("https://example.com/valid2");
        testWebpage.setLinksFromWebpage(links);

        // Act
        mdFileOperator.generateFileWithContent(testWebpage);

        // Assert
        List<String> lines = Files.readAllLines(Path.of("pagereport.md"));
        String content = String.join("\n", lines);

        assertTrue(content.contains("___LINKS FROM THIS SITE___"));
        assertTrue(content.contains("https://example.com/valid1"));
        assertTrue(content.contains("https://example.com/valid2"));
    }

    @Test
    void generateFileWithContentShouldIncludeBrokenLinks() throws IOException {
        // Arrange
        HashSet<String> brokenLinks = new HashSet<>();
        brokenLinks.add("https://example.com/broken1");
        testWebpage.setBrokenLinks(brokenLinks);

        // Act
        mdFileOperator.generateFileWithContent(testWebpage);

        // Assert
        List<String> lines = Files.readAllLines(Path.of("pagereport.md"));
        String content = String.join("\n", lines);

        assertTrue(content.contains("___BROKEN LINKS FROM THIS SITE___"));
        assertTrue(content.contains("https://example.com/broken1<broken>"));
    }

    @Test
    void generateFileWithContentShouldHandleEmptyWebpage() throws IOException {
        // Act
        mdFileOperator.generateFileWithContent(testWebpage);

        // Assert
        List<String> lines = Files.readAllLines(Path.of("pagereport.md"));
        String content = String.join("\n", lines);

        assertTrue(content.contains("___LINKS FROM THIS SITE___"));
        assertTrue(content.contains("___BROKEN LINKS FROM THIS SITE___"));
    }

    @Test
    void generateFileWithContentShouldHandleNullValues() throws IOException {
        // Arrange
        testWebpage.setLinksFromWebpage(null);
        testWebpage.setBrokenLinks(null);

        // Act & Assert
        assertThrows(NullPointerException.class, () ->
                mdFileOperator.generateFileWithContent(testWebpage)
        );
    }
}