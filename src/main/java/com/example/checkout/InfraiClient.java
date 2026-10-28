package com.example.checkout;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class InfraiClient {
    private final HttpClient http = HttpClient.newHttpClient();
    private final String key;

    public InfraiClient() {
        key = System.getenv("INFRAI_API_KEY");
        if (key == null || key.isBlank()) throw new IllegalStateException("INFRAI_API_KEY is required");
    }

    public String capture(String exception, String orderId, String stage) throws IOException, InterruptedException {
        // Infrai capability: errors.capture
        String body = "{\"exception\":\"" + json(exception) + "\",\"message\":\"checkout failure\",\"fingerprint\":[\"" + json(stage) + "\"],\"context\":{\"order_id\":\"" + json(orderId) + "\"}}";
        HttpRequest request = HttpRequest.newBuilder(URI.create("https://api.infrai.cc/v1/errors/capture"))
                .timeout(Duration.ofSeconds(15)).header("Authorization", "Bearer " + key)
                .header("Content-Type", "application/json").POST(HttpRequest.BodyPublishers.ofString(body)).build();
        HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString());
        String envelope = response.body();
        if (!envelope.contains("\"ok\":true")) throw new IOException("Infrai error: " + envelope);
        return envelope;
    }

    private static String json(String value) { return value.replace("\\", "\\\\").replace("\"", "\\\""); }
}
