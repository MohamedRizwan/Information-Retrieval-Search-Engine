package webCrawler;

import static org.junit.Assert.*;

import org.junit.Test;

public class CrawlTest {

	@Test
	public void testCrawlerHelper() {
		CrawlerHelper crawlerHelper = new CrawlerHelper();
		String url = "https://en.wikipedia.org/wiki/Caelian_Hill";
		
		assertTrue(crawlerHelper.crawl(url, 1));
		assertTrue(crawlerHelper.search("Titus Livy"));
		
		assertFalse(crawlerHelper.getLinks().isEmpty());
		assertEquals(crawlerHelper.getDepth(), new Integer(2));
		
		assertFalse(crawlerHelper.search("Northeastern"));
		
		url = "http://shell.cas.usf.edu/mccook/uwy/hyperlinks.html";
		
		assertTrue(crawlerHelper.crawl(url, 1));
		assertTrue(crawlerHelper.getLinks().isEmpty());
		assertTrue(crawlerHelper.search("Contact Livy"));
		assertFalse(crawlerHelper.search("Concordance"));
		
	}

}
