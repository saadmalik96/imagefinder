# ImageFinder
ImageFinder is a multithreaded web crawler designed to extract and list all image URLs found on a user provided domain. It utilizes the Java library [Jsoup](https://jsoup.org/) to parse HTML content and efficiently extract image sources. Currently, it is only able to extract URLs from domains where images are served statically.

## Setup
To start, open a terminal window and navigate to wherever you unzipped to the root directory `imagefinder`. To build the project, run the command:

>`mvn package`

To clear this, you may run the command:

>`mvn clean`

To run the project, use the following command to start the server:

>`mvn clean test package jetty:run`

You should see a line at the bottom that says "Started Jetty Server". Now, if you navigate to `localhost:8080` into your browser, you should see the `index.html` welcome page!

## Features
- Extracts all images from a given domain, from `<img>`, `<a>`, `<picture>` as well as the `<head>` tags.
- Users can specify the search depth of the crawl, allowing for a more thorough search or a quick scan.
- Users have options to include or exclude icons and thumbnails based on their requirements.
- The crawler is careful to avoid paths disallowed in the /robots.txt paths

## Backend Structure
- **crawler/WebCrawler** - Initiates and controls the crawling of web pages using a pool of threads. It handles the task distribution and synchronization using a Phaser to manage the lifecycle of each crawl task.
- **crawler/WebPageScraper** - Once a page is fetched by the WebCrawler, each thread utilizes the WebPageScraper class to parses the HTML to extract image URLs.
- **data/SharedImageData** - Stores URLs and other image data collected during the crawling process. It ensures that data is shared and accessible across different threads safely.
- **data/ImageFinderRequest** - Stores all parameters necessary for a crawl, such as the URL, depth of search, and filter settings. This class acts as a data transfer object across different components of the system.
- **util/RobotsTxtParser** - Parses robots.txt to determine which parts of the site can be crawled, making the crawler slightly more friendlier

The backend is designed in a way where it can easily be extended to incoporate additional functionality at any level. For example, if we wanted to introduce rate limiting, we can introduce an additional RateLimiter class and include it in our WebCrawler class and utilize its methods in the request() method.