package advisor;

import java.util.Scanner;

public class UserInterface {
    private final Scanner scanner;
    private final SpotifyController spotifyController;
    private boolean isUserAuthorized = false;
    private String lastActionRequested;
    private String lastCategorySearch = null;

    public UserInterface(Scanner scanner, String uriPath, String apiPath, int resultsPerPage) {
        this.scanner = scanner;
        this.spotifyController = new SpotifyController(uriPath, apiPath, resultsPerPage);
    }

    public void boot() {
        while (true) {
            String userInput = this.scanner.nextLine();

            if (userInput.equalsIgnoreCase("exit")) {
                continue;
            }

            processUserInput(userInput);
        }
    }

    private void processUserInput(String userInput) {
        if (userInput.contains("playlists")) {
            if (!this.isUserAuthorized) {
                System.out.println("Please, provide access for application.");
                return;
            }

            String[] userInputParts = userInput.split(" ");
            if (userInputParts.length < 2) {
                System.out.println("Please provide a category name.");
                return;
            }

            this.lastCategorySearch = null;

            int startIndex = userInput.indexOf(" ") + 1;
            String categoryName = userInput.substring(startIndex);
            this.spotifyController.resetPage();
            this.spotifyController.getPlaylistsByCategory(categoryName, 1);
            this.lastActionRequested = "playlists";
            this.lastCategorySearch = categoryName;
            return;
        }

        switch (userInput.toLowerCase()) {
            case "auth" -> {
                if (this.isUserAuthorized) {
                    System.out.println("You are already authorized.");
                } else {
                    boolean isAuthorized = this.spotifyController.isUserAuthorized();
                    if (!isAuthorized) {
                        System.out.println("---AUTHORIZATION FAILED---");
                    } else {
                        System.out.println("Success!");
                        this.isUserAuthorized = true;
                    }
                }
            }
            case "new" -> {
                if (this.isUserAuthorized) {
                    this.spotifyController.resetPage();
                    this.spotifyController.displayNewReleases(1);
                    this.lastActionRequested = "new";
                } else {
                    System.out.println("Please, provide access for application.");
                }
            }
            case "next" -> {
                if (this.isUserAuthorized) {
                    this.spotifyController.getNextResults(this.lastActionRequested, this.lastCategorySearch);
                } else {
                    System.out.println("Please, provide access for application.");
                }
            }
            case "featured" -> {
                if (this.isUserAuthorized) {
                    this.spotifyController.resetPage();
                    this.spotifyController.displayFeatured(1);
                    this.lastActionRequested = "featured";
                } else {
                    System.out.println("Please, provide access for application.");
                }
            }
            case "prev" -> {
                if (this.isUserAuthorized) {
                    this.spotifyController.getPrevResults(this.lastActionRequested , this.lastCategorySearch);
                } else {
                    System.out.println("Please, provide access for application.");
                }
            }
            case "categories" -> {
                if (this.isUserAuthorized) {
                    this.spotifyController.resetPage();
                    this.spotifyController.displayCategories(1);
                    this.lastActionRequested = "categories";
                } else {
                    System.out.println("Please, provide access for application.");
                }
            }
            default -> System.out.println("Invalid user input.");
        }
    }
}
