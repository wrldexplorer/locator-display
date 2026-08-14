package com.explorer.locatordisplay.client;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class UuidResolver {

    private static final String MOJANG_API_URL = "https://api.mojang.com/users/profiles/minecraft/";
    private static final HttpClient HTTP_CLIENT = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .build();

    private static final Map<String, CompletableFuture<UUID>> cache = new ConcurrentHashMap<>();

    /**
     * Returns a CompletableFuture that resolves to the player's Mojang UUID.
     * The result is cached and only one request per name is made.
     */
    public static CompletableFuture<UUID> getUuid(String playerName) {
        String key = playerName.toLowerCase();
        return cache.computeIfAbsent(key, k -> fetchFromMojang(playerName));
    }

    private static CompletableFuture<UUID> fetchFromMojang(String playerName) {
        String encodedName = URLEncoder.encode(playerName, StandardCharsets.UTF_8);
        String url = MOJANG_API_URL + encodedName;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .timeout(Duration.ofSeconds(10))
                .build();

        return HTTP_CLIENT.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> {
                    if (response.statusCode() == 200) {
                        return parseUuidFromResponse(response.body());
                    }
                    return null;
                })
                .exceptionally(ex -> null);
    }

    // from 00000000000000000000000000000000
    // to   00000000-0000-0000-0000-000000000000
    private static UUID parseUuidFromResponse(String body) {
        try {
            JsonObject json = JsonParser.parseString(body).getAsJsonObject();
            String rawId = json.get("id").getAsString();
            String withDashes = rawId.replaceFirst(
                    "(\\p{XDigit}{8})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}{12})",
                    "$1-$2-$3-$4-$5");
            return UUID.fromString(withDashes);
        } catch (Exception e) {
            return null;
        }
    }
}