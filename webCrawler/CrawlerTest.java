package webCrawler;

public class CrawlerTest {
	/**
	 * This method is used to test the crawler functionality
	 * 
	 * @param args
	 *            - not used
	 */
	public static void main(String[] args) {
		long startTime = System.currentTimeMillis();
		Crawler spider = new Crawler();
		spider.crawl("http://en.wikipedia.org/wiki/Hugh_of_Saint-Cher","concordance");
		//spider.crawl("http://en.wikipedia.org/wiki/Hugh_of_Saint-Cher");
		long endTime = System.currentTimeMillis();
		System.out.println("Running TIme: " + (endTime - startTime));

	}

}