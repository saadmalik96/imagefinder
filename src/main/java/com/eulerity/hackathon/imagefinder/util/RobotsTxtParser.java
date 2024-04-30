package com.eulerity.hackathon.imagefinder.util;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * The RobotsTxtParser class is responsible for parsing the robots.txt file of a given URL.
 */
public class RobotsTxtParser {
    private final Map<String, Boolean> disallowedPaths = new HashMap<>();

    /**
     * Instantiates a new Robots txt parser.
     *
     * @param baseUrl the base URL
     */
    public RobotsTxtParser(URL baseUrl) {
        try {
            URL robotsTxtUrl = new URL(baseUrl, "/robots.txt");
            Scanner scanner = new Scanner(robotsTxtUrl.openStream());
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (line.startsWith("Disallow: ")) {
                    String path = line.substring("Disallow: ".length()).trim();
                    if (!path.isEmpty()) {
                        disallowedPaths.put(path, true);
                    }
                }
            }
            scanner.close();
        } catch (IOException e) {
            System.out.println("Failed to retrieve or parse robots.txt: " + e.getMessage());
        }
    }

    /**
     * Checks if a path is allowed.
     *
     * @param path the path
     * @return true if the path is allowed, false otherwise
     */
    public boolean isAllowed(String path) {
        return !disallowedPaths.containsKey(path);
    }
}