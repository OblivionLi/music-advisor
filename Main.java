package advisor;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        String uriPath = null;
        String apiPath = null;
        int resultsPerPage = 5;
        if (args.length >= 2) {
            if (args[0].equalsIgnoreCase("-access")) {
                uriPath = args[1];
            }

            if (args[2].equalsIgnoreCase("-resource")) {
                apiPath = args[3];
            }

            if (args[4].equalsIgnoreCase("-page")) {
                resultsPerPage = Integer.parseInt(args[5]);
            }
        }

        Scanner scanner = new Scanner(System.in);
        UserInterface ui = new UserInterface(scanner, uriPath, apiPath, resultsPerPage);
        ui.boot();
    }
}