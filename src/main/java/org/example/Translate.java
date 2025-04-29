package org.example;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;

public class Translate {

    private static final String RAPIDAPI_HOST = "text-translator2.p.rapidapi.com";
    private static final String RAPIDAPI_KEY = "f30d09e2f7mshed755fb9e8d7513p199d18jsn474873e9896b"; // Replace with your RapidAPI key
    private static final String API_URL = "https://text-translator2.p.rapidapi.com/translate";
    //private static final String RAPIDAPI_HOST = "text-translator2.p.rapidapi.com";
    //private static final String RAPIDAPI_KEY = "YOUR_RAPIDAPI_KEY"; // Replace with your RapidAPI key

    public void translateHeaders(Webpage webpage, String targetLanguage, String sourceLanguage) {
        try {

            HashMap<String, String> translatedHeaders = translateHashMap(webpage.getHeadingsFromWebpage(), targetLanguage, sourceLanguage);
            webpage.setHeadingsFromWebpage(translatedHeaders);
            // Print the translated HashMap
            /*for (Map.Entry<String, String> entry : translatedHeaders.entrySet()) {
                System.out.println(entry.getKey() + ": " + entry.getValue());
            }*/
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static HashMap<String, String> translateHashMap(HashMap<String, String> headers, String targetLanguage, String sourceLanguage) throws Exception {
        HashMap<String, String> translatedMap = new HashMap<>();
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            String translatedText = translateText(entry.getValue(), targetLanguage,sourceLanguage);
            translatedMap.put(entry.getKey(), translatedText);
        }
        return translatedMap;
    }

    public static String translateText(String text, String targetLanguage, String sourceLanguage) throws Exception {
        URL url = new URL(API_URL);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        connection.setRequestProperty("x-rapidapi-host", RAPIDAPI_HOST);
        connection.setRequestProperty("x-rapidapi-key", RAPIDAPI_KEY);
        connection.setDoOutput(true);

        // Construct the request body as application/x-www-form-urlencoded
        String requestBody = "source_language=" + sourceLanguage + "&target_language=" + targetLanguage + "&text=" + text;

        // Send the request
        try (OutputStream os = connection.getOutputStream()) {
            os.write(requestBody.getBytes());
            os.flush();
        }
        int responseCode = connection.getResponseCode();
        // Read the response
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            // Parse the JSON response
            JSONObject jsonResponse = new JSONObject(response.toString());
            return jsonResponse.getJSONObject("data").getString("translatedText");
        }
        catch (Exception e) {
            throw new RuntimeException("Failed : HTTP error code : " + e.getMessage());
        }
    }

}
