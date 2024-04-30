package com.eulerity.hackathon.imagefinder;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.eulerity.hackathon.imagefinder.crawler.WebCrawler;
import com.eulerity.hackathon.imagefinder.data.ImageFinderRequest;
import com.eulerity.hackathon.imagefinder.data.SharedImageData;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


@WebServlet(
    name = "ImageFinder",
    urlPatterns = {"/main"}
)
public class ImageFinder extends HttpServlet{
	private static final long serialVersionUID = 1L;
	protected static final Gson GSON = new GsonBuilder().create();

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.setContentType("text/json");
		String url = req.getParameter("url");
		boolean allowIcons = Boolean.parseBoolean(req.getParameter("allowIcons"));
		boolean allowThumbnails = Boolean.parseBoolean(req.getParameter("allowThumbnails"));
		int searchDepth = Integer.parseInt(req.getParameter("searchDepth"));

		if (url == null || url.isEmpty()) {
			resp.getWriter().print(GSON.toJson("No URL provided"));
			return;
		}

		ImageFinderRequest imageFinderRequest = new ImageFinderRequest(url, allowIcons, allowThumbnails, searchDepth);
		SharedImageData sharedImageData = new SharedImageData();
		WebCrawler webCrawler = new WebCrawler(sharedImageData, imageFinderRequest);
		webCrawler.startCrawl();
		Map<String, String> imageUrls = sharedImageData.getAllImages();
		resp.getWriter().print(GSON.toJson(imageUrls));
	}

}
