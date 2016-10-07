package webCrawler;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class Crawler {
	// TODO: Change it to 1000 after testing
	private static final int maxPagesToCrawl = 1000;
	private static final Integer maxDepthToCrawl = 5;
	private Set<String> crawledPages = new HashSet<String>(); //keeps track of the crawled pages
	private Set<String> relevantPages = new HashSet<String>(); //stores the URLs which contains the keyword
	private List<String> pagesToCrawl = new LinkedList<String>(); //stores all the URLs that has to be linked. Used for BFS traversal of the URLs
	private List<Integer> depthTracker = new LinkedList<Integer>(); //stores the depth of each URL in the pagesToCrawl in the same order
	private static Integer depth = 1; //counter used to calculate the depth of each URL

	private String requiredUrlPrefix = "https://en.wikipedia.org/wiki/"; //Seed URL
	private String urlNotAllowed = "https://en.wikipedia.org/wiki/Main_Page"; //Excluded URL
	
	private String filePath = "F:/IRWorkspace/file.txt";

	/**
	 * Method to crawl from the given seed URL. Internally it calls methods
	 * in CrawlerHelper class which makes the HTTP request and parsing of the 
	 * web page.
	 * @param url - Seed URL for starting the crawl
	 */
	public void crawl(String url) {
		//condition to crawl till max pages and the depth instantiated
		while (this.crawledPages.size() < maxPagesToCrawl && depth <= maxDepthToCrawl) {
			String currentUrl;
			CrawlerHelper crawlerHelper = new CrawlerHelper();
			
			if (this.pagesToCrawl.isEmpty()) {
				//Crawling haven't started yet. Set the starting URL as the seed URL
				currentUrl = url;
				// setDepth(1);
				this.crawledPages.add(url);
			} else {
				//Get the next URL to be crawled from pagesToCrawl 
				currentUrl = this.nextUrl();
			}
			crawlerHelper.crawl(currentUrl, getDepth());

			this.populatePagesToVisit(crawlerHelper.getLinks(), crawlerHelper.getDepth());
		}
		System.out.println("\nNumber of Pages Crawled: " + this.crawledPages.size());
		persistCrawledUrls(crawledPages);
	}

	/**
	 * Method to crawl from the given seed URL. Internally it calls methods
	 * in CrawlerHelper class which makes the HTTP request and parsing of the 
	 * web page. This method also search for the keyword in the crawled web pages.
	 * Focused crawling is performed in this method.
	 * 
	 * @param url - Seed URL for starting the crawl
	 * @param keyWord - The string that you are searching for
	 */
	public void crawl(String url, String keyWord) {
		//condition to crawl till max pages and the depth instantiated
		while (this.relevantPages.size() < maxPagesToCrawl && depth <= maxDepthToCrawl) {
			String currentUrl;
			CrawlerHelper leg = new CrawlerHelper();
			if (this.pagesToCrawl.isEmpty()) {
				currentUrl = url;
				setDepth(1);
				this.crawledPages.add(url);
			} else {
				currentUrl = this.nextUrl();
			}
			leg.crawl(currentUrl, getDepth());

			boolean success = leg.search(keyWord);
			if (success) {
				System.out.println(String.format("Keyword %s found at %s", keyWord, currentUrl));
				//keep track of the pages with the keyword
				this.relevantPages.add(currentUrl);
				
				//populate the list of pagesT0oCrawl only if the keyword you are searching is found in the currentURL
				this.populatePagesToVisit(leg.getLinks(), leg.getDepth());
			}
		}
		System.out.println("\nNumber of Pages Crawled: " + this.crawledPages.size());
		persistCrawledUrls(relevantPages);
	}

	/**
	 * Returns the next URL to be crawled in the order that they were added in
	 * the list. Also this method make sure that it is not returning the URL
	 * which is already crawled
	 * 
	 * @return - next URL to be crawled
	 */
	private String nextUrl() {
		String nextUrl;
		do {
			nextUrl = this.pagesToCrawl.remove(0);
			setDepth(this.depthTracker.remove(0));
		} while (this.crawledPages.contains(nextUrl));
		this.crawledPages.add(nextUrl);
		return nextUrl;
	}

	/**
	 * This method populates the list pagesToCrawl. This method internally calls
	 * other method which check whether the URL inserted is relevant to the 
	 * problem statement. It also parses the URL to remove Ref attribute in it.
	 * 
	 * @param links - List of links identified in the crawled page
	 * @param depth - depth of the URLs identified. 
	 */
	private void populatePagesToVisit(List<String> links, Integer depth) {
		if (!links.isEmpty()) {
			for (String url : links) {
				url = removeRefFromURL(url);
				if (isUrlAcceptable(url)) {
					this.pagesToCrawl.add(url);
					this.depthTracker.add(depth);
				}
			}
		}
	}

	/**
	 * Parses the URL for removing the Ref attribute in the URL.
	 * Part of the URL following '#' is the reference in a webpage
	 * 
	 * @param url - URL to be parsed
	 * 
	 * @return - parsed URL
	 */
	private String removeRefFromURL(String url){
		if(!url.isEmpty()){
			if(url.contains("#"))
				return url.substring(0, url.indexOf("#"));
		}
		return url;
	}

	/**
	 * This method checks whether the URL that is going to be crawled
	 * satisfies the criterias in the problem statement
	 * 
	 * @param url - URL that has to be checked for acceptance
	 * 
	 * @return - whether URL is acceptable or not
	 */
	private boolean isUrlAcceptable(String url) {

		if (!url.equalsIgnoreCase(urlNotAllowed)) {
			if (url.startsWith(this.requiredUrlPrefix)) {
				String documentPath = url.substring(requiredUrlPrefix.length());
				if (!documentPath.contains(":")) {
					return true;
				}
			}
		}
		return false;
	}

	public static Integer getDepth() {
		return depth;
	}

	public static void setDepth(Integer depth) {
		Crawler.depth = depth;
	}

	/**
	 * This method persists the URLs crawled in a text file at the path specified.
	 * 
	 * @param - list of URLs crawled
	 */
	private void persistCrawledUrls(Set<String> crawledPages) {
		PrintWriter writer;
		try {
			File file = new File(this.filePath);
			file.createNewFile();
			FileWriter fw = new FileWriter(file);
			writer = new PrintWriter(fw);
			// writer = new PrintWriter("crawledURLS.txt", "UTF-8");
			for (String crawledUrl : crawledPages) {
				writer.println(crawledUrl);
			}
			writer.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
