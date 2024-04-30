package com.eulerity.hackathon.imagefinder.crawler;

import com.eulerity.hackathon.imagefinder.data.ImageFinderRequest;
import com.eulerity.hackathon.imagefinder.data.SharedImageData;
import com.eulerity.hackathon.imagefinder.util.RobotsTxtParser;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Phaser;
import java.util.concurrent.TimeUnit;

/**
 * The Web Crawler Class crawls a given URL.
 */
public class WebCrawler {

    private final ExecutorService poolManager;
    SharedImageData sharedImageData;
    ImageFinderRequest imageFinderRequest;
    private final Phaser phaser;
    private final RobotsTxtParser robotsTxtParser;

    /**
     * Instantiates a new Web crawler.
     *
     * @param sharedImageData the image data
     * @param imageFinderRequest the image finder request
     */
    public WebCrawler(SharedImageData sharedImageData, ImageFinderRequest imageFinderRequest) {
        this.imageFinderRequest = imageFinderRequest;
        this.sharedImageData = sharedImageData;
        this.poolManager = Executors.newFixedThreadPool(10);
        this.phaser = new Phaser(1);
        this.robotsTxtParser = new RobotsTxtParser(imageFinderRequest.getUrl());
        System.out.println(imageFinderRequest);
    }

    private class WebPageWorker implements Runnable {
        private final String url;
        private final int depth;

        /**
         * Instantiates a new Web page worker.
         *
         * @param url   the url
         * @param depth the depth
         */
        public WebPageWorker(String url, int depth) {
            this.url = url;
            this.depth = depth;
        }

        @Override
        public void run() {
            try {
                System.out.println(Thread.currentThread().getName() + " started processing: " + url);
                if (depth > imageFinderRequest.getSearchDepth() || sharedImageData.isVisited(url)) {
                    return;
                }

                sharedImageData.addVisitedUrl(url);
                Document doc = request(url);
                if (doc != null) {
                    new WebPageScraper(sharedImageData, imageFinderRequest).extractImagesFromWebpage(doc);
                    if (depth < imageFinderRequest.getSearchDepth()) {
                        processPageLinks(doc, depth);
                    }
                }
            } finally {
                phaser.arriveAndDeregister();
                System.out.println(Thread.currentThread().getName() + " finished processing: " + url);
            }
        }
    }

    /**
     * Method to find all the links on a page and add them to the queue.
     *
     * @param doc   the doc
     * @param depth the depth
     */
    private void processPageLinks(Document doc, int depth) {
        doc.select("a[href]").stream()
                .map(e -> e.absUrl("href"))
                .filter(this::shouldVisit)
                .distinct()
                .forEach(href -> {
                    if (!sharedImageData.isVisited(href)) {
                        sharedImageData.addUrlToVisit(href);
                        phaser.register();
                        poolManager.submit(new WebPageWorker(href, depth + 1));
                    }
                });
    }

    /**
     * Method to start crawling a domain.
     */
    public void startCrawl() {
        phaser.register();
        System.out.println("Starting crawl at " + imageFinderRequest.getUrlString());
        poolManager.submit(new WebPageWorker(imageFinderRequest.getUrlString(), 0));
        phaser.arriveAndAwaitAdvance();
        phaser.awaitAdvance(0);
        System.out.println("*** Crawl complete ***");
        System.out.println("Total pages visited: " + sharedImageData.getVisitedCount());
        System.out.println("Total images found: " + sharedImageData.numImages());
        this.shutdown();
    }

    /**
     * Request the webpage.
     *
     * @param url the url
     * @return the document
     */
    private Document request(String url) {
        try {
            Connection connection = Jsoup.connect(url).userAgent("Mozilla/5.0");
            Document doc = connection.get();

            if (connection.response().statusCode() == 200) {
                System.out.println("\nReceived webpage at " + url);
                String title = doc.title();
                System.out.println("Title: " + title);
                return doc;
            }
            return null;
        } catch (IOException e) {
            System.err.println("Error fetching webpage: " + url);
            return  null;
        }
    }

    /**
     * Determine if the URL should be visited by one of the threads.
     *
     * @param href the href
     * @return the boolean
     */
    private boolean shouldVisit(String href) {
        if (href.isEmpty() || href.contains("#") || href.startsWith("mailto:") || href.startsWith("javascript:")) {
            return false;
        }

        try {
            URL targetURL = new URL(imageFinderRequest.getUrl(), href);
            if (!targetURL.getProtocol().equals("http") && !targetURL.getProtocol().equals("https")) {
                return false;
            }
            if (!robotsTxtParser.isAllowed(targetURL.getPath())) {
                return false;
            }
            String baseHost = imageFinderRequest.getUrl().getHost().replaceFirst("^www\\.", "");
            String targetHost = targetURL.getHost().replaceFirst("^www\\.", "");

            return targetHost.equals(baseHost);
        } catch (MalformedURLException e) {
            System.err.println("Malformed URL: " + href);
            return false;
        }
    }

    /**
     * Shutdown the Executor Service.
     */
    public void shutdown() {
        poolManager.shutdown();
        try {
            poolManager.awaitTermination(10, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            System.out.println(e);
        }
    }
}
