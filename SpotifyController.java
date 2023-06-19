package advisor;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

public class SpotifyController {
    private final SpotifyClient spotifyClient;
    private int resultsPerPage;
    private int currentPage;

    public SpotifyController(String uriPath, String apiPath, int resultsPerPage) {
        this.spotifyClient = new SpotifyClient(uriPath, apiPath);
        this.resultsPerPage = resultsPerPage;
    }

    public boolean isUserAuthorized() {
        return this.spotifyClient.authorize();
    }

    public void displayNewReleases(int page) {
        HttpResponse<String> response = this.spotifyClient.getNewReleases();
        if (response == null) {
            System.out.println("Failed to get response for `new releases`.");
            return;
        }

        JsonObject data = JsonParser.parseString(response.body()).getAsJsonObject();
        if (response.statusCode() >= 400 || data.has("error")) {
            JsonObject error = data.getAsJsonObject("error");
            System.out.println(error.get("message").getAsString());
            return;
        }

        JsonObject albums = data.getAsJsonObject("albums");
        JsonArray items = albums.getAsJsonArray("items");

        if (items.isEmpty()) {
            System.out.println("New Releases list is empty.");
            return;
        }

        int totalItems = items.size();
        int totalPages = (int) Math.ceil((double) totalItems / this.resultsPerPage);
        int startIndex = (page - 1) * this.resultsPerPage;
        int endIndex = Math.min(startIndex + this.resultsPerPage, totalItems);

        if (startIndex >= totalItems) {
            this.currentPage = totalPages;
            System.out.println("No more pages.");
            return;
        }

        for (int i = startIndex; i < endIndex; i++) {
            JsonObject itemObject = items.get(i).getAsJsonObject();

            List<String> artistNames = new ArrayList<>();
            JsonArray artistsArray = itemObject.getAsJsonArray("artists");
            for (JsonElement artistElement : artistsArray) {
                JsonObject artistObject = artistElement.getAsJsonObject();
                String artistName = artistObject.get("name").getAsString();
                artistNames.add(artistName);
            }

            String name = itemObject.get("name").getAsString();
            JsonObject urlObject = itemObject.getAsJsonObject("external_urls");
            String url = urlObject.get("spotify").getAsString();

            System.out.println(name);
            System.out.println(artistNames);
            System.out.println(url);
            System.out.println();
        }

        System.out.println("---PAGE " + page + " OF " + totalPages + "---");
    }

    public void displayFeatured(int page) {
        HttpResponse<String> response = this.spotifyClient.getFeatured();

        if (response == null) {
            System.out.println("Failed to get response for `featured`.");
            return;
        }

        JsonObject data = JsonParser.parseString(response.body()).getAsJsonObject();
        if (response.statusCode() >= 400 || data.has("error")) {
            JsonObject error = data.getAsJsonObject("error");
            System.out.println(error.get("message").getAsString());
            return;
        }

        JsonObject playlists = data.getAsJsonObject("playlists");
        JsonArray items = playlists.getAsJsonArray("items");

        if (items.isEmpty()) {
            System.out.println("Featured list is empty.");
            return;
        }

        int totalItems = items.size();
        int totalPages = (int) Math.ceil((double) totalItems / this.resultsPerPage);
        int startIndex = (page - 1) * this.resultsPerPage;
        int endIndex = Math.min(startIndex + this.resultsPerPage, totalItems);

        if (startIndex >= totalItems) {
            this.currentPage = totalPages;
            System.out.println("No more pages.");
            return;
        }

        for (int i = startIndex; i < endIndex; i++) {
            JsonObject itemObject = items.get(i).getAsJsonObject();

            String name = itemObject.get("name").getAsString();
            JsonObject urlObject = itemObject.getAsJsonObject("external_urls");
            String url = urlObject.get("spotify").getAsString();

            System.out.println(name);
            System.out.println(url);
            System.out.println();
        }

        System.out.println("---PAGE " + page + " OF " + totalPages + "---");
    }

    public void displayCategories(int page) {
        HttpResponse<String> response = this.spotifyClient.getCategories();

        if (response == null) {
            System.out.println("Failed to get response for `categories`.");
            return;
        }

        JsonObject data = JsonParser.parseString(response.body()).getAsJsonObject();
        if (response.statusCode() >= 400 || data.has("error")) {
            JsonObject error = data.getAsJsonObject("error");
            System.out.println(error.get("message").getAsString());
            return;
        }

        JsonObject categories = data.getAsJsonObject("categories");
        JsonArray items = categories.getAsJsonArray("items");

        if (items.isEmpty()) {
            System.out.println("Categories list is empty.");
            return;
        }

        int totalItems = items.size();
        int totalPages = (int) Math.ceil((double) totalItems / this.resultsPerPage);
        int startIndex = (page - 1) * this.resultsPerPage;
        int endIndex = Math.min(startIndex + this.resultsPerPage, totalItems);

        if (startIndex >= totalItems) {
            this.currentPage = totalPages;
            System.out.println("No more pages.");
            return;
        }

        for (int i = startIndex; i < endIndex; i++) {
            JsonObject itemObject = items.get(i).getAsJsonObject();
            System.out.println(itemObject.get("name").getAsString());
        }

        System.out.println("---PAGE " + page + " OF " + totalPages + "---");
    }

    public void getPlaylistsByCategory(String categoryName, int page) {
        HttpResponse<String> response = this.spotifyClient.getPlaylistsByCategory(categoryName);

        if (response == null) {
            System.out.println("Failed to get response for `categories`.");
            return;
        }

        JsonObject data = JsonParser.parseString(response.body()).getAsJsonObject();
        if (response.statusCode() >= 400 || data.has("error")) {
            JsonObject error = data.getAsJsonObject("error");
            System.out.println(error.get("message").getAsString());
            return;
        }

        JsonObject playlists = data.getAsJsonObject("playlists");
        JsonArray items = playlists.getAsJsonArray("items");

        if (items.isEmpty()) {
            System.out.println("Category Playlists list is empty.");
            return;
        }

        int totalItems = items.size();
        int totalPages = (int) Math.ceil((double) totalItems / this.resultsPerPage);
        int startIndex = (page - 1) * this.resultsPerPage;
        int endIndex = Math.min(startIndex + this.resultsPerPage, totalItems);

        if (startIndex >= totalItems) {
            this.currentPage = totalPages;
            System.out.println("No more pages.");
            return;
        }

        for (int i = startIndex; i < endIndex; i++) {
            JsonObject itemObject = items.get(i).getAsJsonObject();
            String name = itemObject.get("name").getAsString();
            JsonObject urlObject = itemObject.getAsJsonObject("external_urls");
            String url = urlObject.get("spotify").getAsString();
            System.out.println(name);
            System.out.println(url);
            System.out.println();
        }

        System.out.println("---PAGE " + page + " OF " + totalPages + "---");
    }

    public void getNextResults(String requestType, String categoryName) {
        this.currentPage++;

        this.callRequestWithFilterData(requestType, categoryName);
    }

    public void resetPage() {
        this.currentPage = 1;
    }

    public void getPrevResults(String requestType, String categoryName) {
        if (this.currentPage < 2) {
            System.out.println("No more pages.");
            return;
        }

        this.currentPage--;
        this.callRequestWithFilterData(requestType, categoryName);
    }

    private void callRequestWithFilterData(String requestType, String categoryName) {
        switch (requestType) {
            case "new" -> {
                this.displayNewReleases(this.currentPage);
            }
            case "featured" -> {
                this.displayFeatured(this.currentPage);
            }
            case "categories" -> {
                this.displayCategories(this.currentPage);
            }
            case "playlists" -> {
                if (categoryName != null) {
                    this.getPlaylistsByCategory(categoryName, this.currentPage);
                } else {
                    System.out.println("Category name not provided to get the playlists.");
                }
            }
        }
    }
}
