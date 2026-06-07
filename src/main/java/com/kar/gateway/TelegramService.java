package com.kar.gateway;

import jakarta.inject.Singleton;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Singleton
public class TelegramService {

    private final TelegramConfig telegramConfig;
    private final HttpClient httpClient;

    public TelegramService(TelegramConfig telegramConfig) {
        this.telegramConfig = telegramConfig;
        this.httpClient = HttpClient.newHttpClient();
    }

    public void sendMessage(Long chatId, String text) {
        try {
            String url = "https://api.telegram.org/bot"
                    + telegramConfig.getToken()
                    + "/sendMessage";

            String body = "{\"chat_id\":" + chatId + ",\"text\":\"" + text + "\"}";

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();

            httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            System.out.println("Failed to send Telegram message: " + e.getMessage());
        }
    }
}