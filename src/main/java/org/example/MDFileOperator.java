package org.example;

import net.steppschuh.markdowngenerator.text.Text;
import net.steppschuh.markdowngenerator.text.emphasis.BoldText;
import net.steppschuh.markdowngenerator.text.heading.Heading;

import java.io.FileWriter;
import java.io.IOException;

public class MDFileOperator {
    String fileName = "pagereport.md";
    FileWriter fileWriter;
    public MDFileOperator() throws IOException {
        this.fileWriter = new FileWriter(fileName);
    }
    public void generateFileWithContent(Webpage webpage) {
        try {
            buildMainData(webpage);
            buildHeadingData(webpage);
            buildValidLinks(webpage);
            buildBrokenLinks(webpage);
            fileWriter.close();
        } catch (IOException e)
        {
            System.out.println("Error writing file: " + e.getMessage());
        }
    }
    private void buildMainData(Webpage webpage)
    {
        String buildMainData = new BoldText("Website: " + webpage.getUrl()) + "\n" +
                new Text("Depth: " + webpage.getDepth()) + "\n" +
                new Text("Source Language: " + webpage.getSourceLanguage()) + "\n" +
                new Text("Target Language: " + webpage.getTargetLanguage()) + "\n";
        try {
            fileWriter.write(buildMainData);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private void buildHeadingData(Webpage webpage)
    {
        StringBuilder headingBuilder = new StringBuilder();
        if(webpage.getHeadingsFromWebpage().get("h1") != null)
        {
            headingBuilder.append(new Heading(webpage.getHeadingsFromWebpage().get("h1"), 1)).append("\n");
        }
        if(webpage.getHeadingsFromWebpage().get("h2") != null)
        {
            headingBuilder.append(new Heading(webpage.getHeadingsFromWebpage().get("h2"), 2)).append("\n");
        }
        if(webpage.getHeadingsFromWebpage().get("h3") != null)
        {
            headingBuilder.append(new Heading(webpage.getHeadingsFromWebpage().get("h3"), 3)).append("\n");
        }
        if(webpage.getHeadingsFromWebpage().get("h4") != null)
        {
            headingBuilder.append(new Heading(webpage.getHeadingsFromWebpage().get("h4"), 4)).append("\n");
        }
        if(webpage.getHeadingsFromWebpage().get("h5") != null)
        {
            headingBuilder.append(new Heading(webpage.getHeadingsFromWebpage().get("h5"), 5)).append("\n");
        }
        if(webpage.getHeadingsFromWebpage().get("h6") != null)
        {
            headingBuilder.append(new Heading(webpage.getHeadingsFromWebpage().get("h6"), 6)).append("\n");
        }
        try {
            fileWriter.write(headingBuilder.toString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private void buildValidLinks(Webpage webpage)
    {
        try {
            fileWriter.write("___LINKS FROM THIS SITE___" + "\n");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        for (String linkToWebpage : webpage.getLinksFromWebpage()) {
            try {
                fileWriter.write(linkToWebpage + "\n");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
    private void buildBrokenLinks(Webpage webpage)
    {
        try {
            fileWriter.write("___BROKEN LINKS FROM THIS SITE___" + "\n");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        for (String linkToWebpage : webpage.getBrokenLinks()) {
            try {
                fileWriter.write(linkToWebpage + "<broken>" +"\n");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
