package com.pensasha.school.sms;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class Gateway {
    private String mBaseUrl;
    private int mPartnerId;
    private String mApiKey;
    private String mShortCode;
    private HttpURLConnection connection;

    public Gateway(String baseUrl, int partnerId, String apiKey, String shortCode) {
        this.mBaseUrl = baseUrl;
        this.mPartnerId = partnerId;
        this.mApiKey = apiKey;
        this.mShortCode = shortCode;
    }

    private String getFinalURL(String mobile, String message) {
        String encodedMessage = URLEncoder.encode(message, StandardCharsets.UTF_8);
        String encodedMobiles = URLEncoder.encode(mobile, StandardCharsets.UTF_8);
        return this.mBaseUrl + "?apikey=" + this.mApiKey + "&partnerID=" + this.mPartnerId + "&shortcode=" + this.mShortCode + "&mobile=" + encodedMobiles + "&message=" + encodedMessage;
    }

    public String sendSingleSms(String message, String mobile) throws IOException {
        String finalUrl = this.getFinalURL(mobile, message);
        return this.makeHttpGetRequest(finalUrl);
    }

    public String sendBulkSms(String message, String[] mobiles) throws IOException {
        String numbers = Arrays.toString(mobiles).replace("[", "").replace("]", "").replace(" ", "");
        String finalUrl = this.getFinalURL(numbers, message);
        return this.makeHttpGetRequest(finalUrl);
    }

    private String makeHttpGetRequest(String urlString) throws IOException {
        String line;
        URL url = this.makeURL(urlString);
        if (this.connection == null) {
            this.connection = (HttpURLConnection)url.openConnection();
            this.connection.setRequestMethod("GET");
            this.connection.setReadTimeout(15000);
        }
        BufferedReader in = new BufferedReader(new InputStreamReader(this.connection.getInputStream()));
        StringBuilder content = new StringBuilder();
        while ((line = in.readLine()) != null) {
            content.append(line);
            content.append(System.lineSeparator());
        }
        return content.toString();
    }

    private URL makeURL(String urlString) throws MalformedURLException {
        return new URL(urlString);
    }
}