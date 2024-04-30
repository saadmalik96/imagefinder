package com.eulerity.hackathon.imagefinder.crawler;

import com.eulerity.hackathon.imagefinder.data.ImageFinderRequest;
import com.eulerity.hackathon.imagefinder.data.SharedImageData;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashSet;


/**
 * The Web page scraper class is responsible for extracting all images from a given webpage.
 */
public class WebPageScraper {
    SharedImageData sharedImageData;
    ImageFinderRequest imageFinderRequest;
    HashSet<String> imageUrls = new HashSet<>();

    /**
     * Instantiates a new Web page scraper.
     *
     * @param sharedImageData    the shared image data
     * @param imageFinderRequest the image finder request
     */
    public WebPageScraper(SharedImageData sharedImageData, ImageFinderRequest imageFinderRequest) {
        this.sharedImageData = sharedImageData;
        this.imageFinderRequest = imageFinderRequest;
    }

    /**
     * Extract images from webpage.
     *
     * @param doc the doc
     */
    public void extractImagesFromWebpage(Document doc) {
        getAllImgUrls(doc);
        getAllHrefImageUrls(doc);
        getAllPictureUrls(doc);
        getAllFaviconUrls(doc);
        addImagesToImageData();
        System.out.println("* Extracted all images from web page: " + doc.baseUri() + " *");
    }

    /**
     * Add all required images to image data shared between all threads.
     */
    public void addImagesToImageData() {
        for (String url : imageUrls) {
            if (shouldSkipImage(url)) {
                System.out.println("Skipping image: " + url);
                continue;
            }
            System.out.println("Adding image to dataset: " + url);
            sharedImageData.addImageUrl(url, getImageType(url));
        }
    }

    /**
     * Gets all images from <picture> tags.
     *
     * @param doc the doc
     */
    private void getAllPictureUrls(Document doc) {
        Elements pictures = doc.select("picture");
        for (Element picture : pictures) {
            Elements sources = picture.select("source");
            for (Element source : sources) {
                String srcset = source.attr("srcset");
                if (!srcset.isEmpty()) {
                    for (String src : srcset.split(",")) {
                        String urlPart = src.split("\\s+")[0];  // Split by whitespace and take the URL part
                        String absoluteUrl = source.absUrl(urlPart);
                        if (!absoluteUrl.isEmpty()) {
                            imageUrls.add(absoluteUrl);
                        }
                    }
                }
            }

            Element img = picture.selectFirst("img");
            if (img != null) {
                String imgSrc = img.absUrl("src");
                if (!imgSrc.isEmpty()) {
                    imageUrls.add(imgSrc);
                }
            }
        }
    }

    /**
     * Gets all images from <img> tags.
     *
     * @param doc the doc
     */
    private void getAllImgUrls(Document doc) {
        Elements images = doc.select("img");
        images.forEach(img -> {
            String imageUrl = img.absUrl("src");
            if (!imageUrl.isEmpty()) {
                imageUrls.add(imageUrl);
            }
        });
    }

    /**
     * Gets all images from <a> tags.
     *
     * @param doc the doc
     */
    private void getAllHrefImageUrls(Document doc) {
        Elements links = doc.select("a[href]");
        links.stream()
                .map(link -> link.absUrl("href"))
                .filter(href -> href.matches(".*\\.(jpg|jpeg|png|webp)$"))
                .forEach(href -> {
                    imageUrls.add(href);
                });
    }

    /**
     * Return whether image is icon or not.
     *
     * @param imageUrl the image url
     * @return the boolean
     */
    private boolean isIcon(String imageUrl) {

        if (imageUrl.contains("icon") || imageUrl.contains("favicon")) {
            return true;
        }

        return imageUrl.endsWith(".ico") || imageUrl.endsWith(".svg");
    }

    /**
     * Return whether image ss thumbnail not.
     *
     * @param imageUrl the image url
     * @return the boolean
     */
    private boolean isThumbnail(String imageUrl) {
        return imageUrl.contains("thumb") || imageUrl.contains("thumbnail") || imageUrl.contains("small");
    }

    /**
     * Gets all favicon urls.
     *
     * @param doc the doc
     */
    private void getAllFaviconUrls(Document doc) {
        Elements links = doc.head().select("link[href][rel=icon], link[href][rel='shortcut icon']");
        for (Element link : links) {
            String href = link.absUrl("href");
            if (!href.isEmpty()) {
                imageUrls.add(href);
                System.out.println("Favicon found: " + href);
            }
        }
    }

    /**
     * Should skip image boolean.
     *
     * @param url the url
     * @return the boolean
     */
    private boolean shouldSkipImage(String url) {
        url = url.toLowerCase();
        boolean containsSkippedPath = url.contains("/asset/") || url.contains("/static/");

        return containsSkippedPath ||
                (isIcon(url) && !imageFinderRequest.isAllowIcons()) ||
                (isThumbnail(url) && !imageFinderRequest.isAllowThumbnails());
    }


    /**
     * Gets image type.
     *
     * @param url the url
     * @return the image type
     */
    private String getImageType(String url) {
        url = url.toLowerCase();
        if (isIcon(url)) return "icon";
        if (isThumbnail(url)) return "thumbnail";
        return "regular";
    }
}
