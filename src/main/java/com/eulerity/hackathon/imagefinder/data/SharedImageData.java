package com.eulerity.hackathon.imagefinder.data;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * The Shared image data class is responsible for holding the shared data between all threads.
 */
public class SharedImageData {
    private final Set<String> visitedUrls = Collections.synchronizedSet(new HashSet<>());
    private final ConcurrentLinkedQueue<String> toVisit = new ConcurrentLinkedQueue<>();
    private final ConcurrentHashMap<String, String> imageUrls = new ConcurrentHashMap<>();
    private final AtomicInteger visitedCount = new AtomicInteger(0);


    public int numImages() {
        return imageUrls.size();
    }

    /**
     * Increments the number of visited URLs.
     */
    private void incrementVisitedCount() {
        visitedCount.incrementAndGet();
    }

    /**
     * Gets the number of visited URLs.
     *
     * @return the number of visited URLs
     */
    public int getVisitedCount() {
        return visitedCount.get();
    }

    /**
     * Adds a URL to the list of visited URLs.
     *
     * @param url the URL to add
     */
    public void addVisitedUrl(String url) {
        this.incrementVisitedCount();
        visitedUrls.add(url);
    }

    /**
     * Checks if a URL has been visited.
     *
     * @param url the URL to check
     * @return true if the URL has been visited, false otherwise
     */
    public boolean isVisited(String url) {
        return visitedUrls.contains(url);
    }

    /**
     * Adds a URL to the list of URLs to visit.
     *
     * @param url the URL to visit
     */
    public void addUrlToVisit(String url) {
        if (!isVisited(url) && !toVisit.contains(url)) {
            toVisit.add(url);
        }
    }

    /**
     * Gets the next URL to visit.
     *
     * @return the next URL to visit
     */
    public void addImageUrl(String url, String type) {
        imageUrls.put(url, type);
    }

    /**
     * Returns all images to be displayed.
     *
     * @return the all images
     */
    public Map<String, String> getAllImages() {
        return new HashMap<>(imageUrls);
    }
}
