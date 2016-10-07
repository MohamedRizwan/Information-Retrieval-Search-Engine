package webCrawler;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class CrawlerHelper {
	
	//USER_AGENT to establish a connection to the web server.
	private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/535.1 (KHTML, like Gecko) Chrome/13.0.782.112 Safari/535.1";
	private List<String> links = new LinkedList<String>();
	private Document document;
	private Integer depth;

	/**
	 * This method performs crawling by making an HTTP request. Checks the
	 * response and gather the URLs in the page for further crawling
	 * 
	 * @param url - URL to visit
	 * @param depth - depth of the URL we are going to crawl
	 * 
	 * @return whether the crawl was successful
	 */
	public boolean crawl(String url, Integer depth) {
		try {
			// Thread.sleep(1000);
			Connection connection = Jsoup.connect(url).userAgent(USER_AGENT);
			Document htmlDocument = connection.get();
			this.document = htmlDocument;
			
			// 200 is the HTTP Success status code
			if (connection.response().statusCode() == 200){
				System.out.println("\nCrawling web page at " + url + " Depth: " + depth);
			}
			if (!connection.response().contentType().contains("text/html")) {
				System.out.println("Failure!! Retrieved document is not an HTML");
				return false;
			}
			Elements urlsOnPage = htmlDocument.select("a[href]");
			// System.out.println("Found (" + linksOnPage.size() + ") URLs");
			if (!urlsOnPage.isEmpty()) {
				setDepth(++depth);
				for (Element link : urlsOnPage) {
					this.links.add(link.absUrl("href"));
				}
			}

			return true;
		} catch (IOException ioe) {
			// We were not successful in our HTTP request
			return false;
		} /*
			 * catch (InterruptedException e) { // TODO Auto-generated catch
			 * block return false; }
			 */
	}

	/**
	 * This method searches the keyword on the HTML document that is retrieved.
	 * It searches through the body of the document.
	 * 
	 * @param keyWord
	 *            - The word or string to look for
	 * @return whether or not the word was found
	 */
	public boolean search(String keyWord) {
		//should only be used after a successful crawl.
		if (this.document == null) {
			System.out.println("ERROR: crawl method not called before parsing the document");
			return false;
		}
		System.out.println("Searching for the word " + keyWord);
		String bodyText = this.document.body().text();
		return bodyText.toLowerCase().contains(keyWord.toLowerCase());
	}

	public List<String> getLinks() {
		return this.links;
	}

	public Integer getDepth() {
		return depth;
	}

	public void setDepth(Integer depth) {
		this.depth = depth;
	}

}