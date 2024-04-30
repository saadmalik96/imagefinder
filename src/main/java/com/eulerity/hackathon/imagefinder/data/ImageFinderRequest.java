package com.eulerity.hackathon.imagefinder.data;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * The Image finder request class is responsible for holding the request parameters for the image finder.
 */
public class ImageFinderRequest {
    private final URL url;
    private final boolean allowIcons;
    private final boolean allowThumbnails;
    private final int searchDepth;

    /**
     * Instantiates a new Image finder request.
     *
     * @param url           the URL
     * @param allowIcons    the allow icons
     * @param allowThumbnails the allow thumbnails
     * @param searchDepth   the search depth
     */
    public ImageFinderRequest(String url, boolean allowIcons, boolean allowThumbnails, int searchDepth) {
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            url = "http://" + url;
        }
        try {
            this.url = new URL(url);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Invalid base URL provided");
        }
        this.allowIcons = allowIcons;
        this.allowThumbnails = allowThumbnails;
        this.searchDepth = searchDepth;
    }

    /**
     * Gets the URL.
     *
     * @return the URL
     */
    public URL getUrl() {
        return url;
    }

    /**
     * Gets the URL as a string.
     *
     * @return the URL as a string
     */
    public String getUrlString() {
        return url.toString();
    }

    /**
     * Checks if the request allows icons.
     *
     * @return true if icons are allowed, false otherwise
     */
    public boolean isAllowIcons() {
        return allowIcons;
    }

    /**
     * Checks if the request allows thumbnails.
     *
     * @return true if thumbnails are allowed, false otherwise
     */
    public boolean isAllowThumbnails() {
        return allowThumbnails;
    }

    /**
     * Gets the search depth.
     *
     * @return the search depth
     */
    public int getSearchDepth() {
        return searchDepth;
    }

    @Override
    public String toString() {
        return "ImageFinderRequest{" +
                "url=" + url +
                ", allowIcons=" + allowIcons +
                ", allowThumbnails=" + allowThumbnails +
                ", searchDepth=" + searchDepth +
                '}';
    }
}
