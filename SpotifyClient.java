package advisor;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;

public class SpotifyClient {
    private final String CLIENT_ID = "your-client-id";
    private final String CLIENT_SECRET = "your-client-secret";
    private final String REDIRECT_URL = "http://localhost:8089";
    private String API_URL = "https://api.spotify.com";
    private String AUTH_SERVER_URL = "https://accounts.spotify.com";
    private String authorizationCode;
    private String accessToken;

    public SpotifyClient(String uriAddress, String apiAddress) {
        if (uriAddress != null) {
            this.AUTH_SERVER_URL = uriAddress;
        }

        if (apiAddress != null) {
            this.API_URL = apiAddress;
        }
    }

    public boolean authorize() {
        try {
            this.requestAuthCode();
            HttpResponse<String> response = this.requestAccessToken();
            JsonObject jo = JsonParser.parseString(response.body()).getAsJsonObject();
            this.accessToken = jo.get("access_token").getAsString();
            return true;
        } catch (IOException | InterruptedException | NullPointerException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    public HttpResponse<String> getNewReleases() {
        HttpRequest request = HttpRequest.newBuilder()
                .header("Authorization", "Bearer " + this.accessToken)
                .uri(URI.create(this.API_URL + "/v1/browse/new-releases"))
                .GET()
                .build();

        HttpClient client = HttpClient.newBuilder().build();
        try {
            return client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public HttpResponse<String> getFeatured() {
        HttpRequest request = HttpRequest.newBuilder()
                .header("Authorization", "Bearer " + this.accessToken)
                .uri(URI.create(this.API_URL + "/v1/browse/featured-playlists"))
                .GET()
                .build();

        HttpClient client = HttpClient.newBuilder().build();
        try {
            return client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public HttpResponse<String> getCategories() {
        HttpRequest request = HttpRequest.newBuilder()
                .header("Authorization", "Bearer " + this.accessToken)
                .uri(URI.create(this.API_URL + "/v1/browse/categories"))
                .GET()
                .build();

        HttpClient client = HttpClient.newBuilder().build();
        try {
            return client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public HttpResponse<String> getPlaylistsByCategory(String categoryName) {
        String categoryId = this.getCategoryId(categoryName);
        if (categoryId == null) {
            System.out.println("Unknown category name.");
            return null;
        }

        HttpRequest request = HttpRequest.newBuilder()
                .header("Authorization", "Bearer " + this.accessToken)
                .uri(URI.create(this.API_URL + "/v1/browse/categories/" + categoryId + "/playlists"))
                .GET()
                .build();

        HttpClient client = HttpClient.newBuilder().build();
        try {
            return client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    private void requestAuthCode() throws IOException, InterruptedException {
        HttpServer server = HttpServer.create();

        server.bind(new InetSocketAddress(8089), 0);
        server.start();
        this.printAuthInstructions();
        server.createContext("/",
                exchange -> {
                    String query = exchange.getRequestURI().getQuery();
                    String response;

                    if (query != null && query.contains("code")) {
                        Map<String, String> queryParams = parseQueryParameters(query);
                        authorizationCode = queryParams.get("code");
                        response = "Got the code. Return back to your program.";
                        System.out.println("code received ");
                    } else {
                        response = "Authorization code not found. Try again.";
                    }

                    exchange.sendResponseHeaders(200, response.length());
                    exchange.getResponseBody().write(response.getBytes());
                    exchange.getResponseBody().close();
                }
        );

        while (authorizationCode == null) {
            Thread.sleep(10);
        }

        server.stop(10);
    }

    private Map<String, String> parseQueryParameters(String query) {
        Map<String, String> params = new HashMap<>();
        String[] pairs = query.split("[?&]");
        for (String pair : pairs) {
            String[] keyValue = pair.split("=");
            if (keyValue.length == 2) {
                params.put(keyValue[0], keyValue[1]);
            }
        }
        return params;
    }

    private HttpResponse<String> requestAccessToken() throws IOException, InterruptedException {
        System.out.println("making http request for access_token...");

        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(
                    "grant_type=authorization_code" +
                            "&code=" + this.authorizationCode +
                            "&redirect_uri=" + this.REDIRECT_URL
                ))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .uri(URI.create(this.AUTH_SERVER_URL + "/api/token"))
                .header("Authorization", "Basic " + Base64.getEncoder().encodeToString((this.CLIENT_ID + ":" + this.CLIENT_SECRET).getBytes()))
                .build();

        HttpClient client = HttpClient.newBuilder().build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private void printAuthInstructions() {
        System.out.println("use this link to request the access code:");
        System.out.printf("%s/authorize?client_id=%s&redirect_uri=%s&response_type=code", this.AUTH_SERVER_URL, this.CLIENT_ID, this.REDIRECT_URL);
        System.out.println();
        System.out.println("waiting for code...");
    }

    private String getCategoryId(String categoryName) {
        HttpRequest request = HttpRequest.newBuilder()
                .header("Authorization", "Bearer " + this.accessToken)
                .uri(URI.create(this.API_URL + "/v1/browse/categories"))
                .GET()
                .build();

        HttpClient client = HttpClient.newBuilder().build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            JsonObject data = JsonParser.parseString(response.body()).getAsJsonObject();
            JsonObject categories = data.getAsJsonObject("categories");
            JsonArray items = categories.getAsJsonArray("items");

            for (JsonElement itemElement : items) {
                JsonObject itemObject = itemElement.getAsJsonObject();
                if (itemObject.get("name").getAsString().equalsIgnoreCase(categoryName)) {
                    return itemObject.get("id").getAsString();
                }
            }
        } catch (IOException | InterruptedException e) {
            e.getStackTrace();
            return null;
        }

        return null;
    }
}

