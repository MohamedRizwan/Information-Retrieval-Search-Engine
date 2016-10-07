package pageRank;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

public class PageRank {

	private String currentPage;
	private double sinkRank;
	private Set<String> pages;
	private Set<String> sinkNodes;
	private Map<String, String> pagesHavingLinks;
	private Map<String, Double> pageRank;
	private Map<String, Double> newPageRank;
	private Map<String, Integer> outLinkes;
	private int[] combinedPerplexity;
	private int combinedPages;
	private double perplexity;
	private SortedByInLink sortedByInLink;
	private SortedByRank sortedByRank;
	private SortedMap<String, Double> sortedPagesByRank;
	private SortedMap<String, String> sortedPagesByInLinks;
	
	public PageRank() {
		pages = new HashSet<String>();
		sinkNodes = new HashSet<String>();
		pagesHavingLinks = new HashMap<String, String>();
		pageRank = new HashMap<String, Double>();
		newPageRank = new HashMap<String, Double>();
		outLinkes = new HashMap<String, Integer>();
		sortedByInLink = new SortedByInLink(pagesHavingLinks);
		sortedByRank = new SortedByRank(pageRank);
		sortedPagesByRank = new TreeMap<String, Double>(sortedByRank);
		sortedPagesByInLinks = new TreeMap<String, String>(sortedByInLink);
		combinedPerplexity = new int[4];
	}
	
	public String getCurrentPage() {
		return currentPage;
	}
	public void setCurrentPage(String currentPage) {
		this.currentPage = currentPage;
	}
	public double getSinkRank() {
		return sinkRank;
	}
	public void setSinkRank(double sinkRank) {
		this.sinkRank = sinkRank;
	}
	public Set<String> getPages() {
		return pages;
	}
	public void setPages(Set<String> pages) {
		this.pages = pages;
	}
	public Set<String> getSinkNodes() {
		return sinkNodes;
	}
	public void setSinkNodes(Set<String> sinkNodes) {
		this.sinkNodes = sinkNodes;
	}
	public Map<String, String> getPagesHavingLinks() {
		return pagesHavingLinks;
	}
	public void setPagesHavingLinks(Map<String, String> pagesHavingLinks) {
		this.pagesHavingLinks = pagesHavingLinks;
	}
	public Map<String, Double> getPageRank() {
		return pageRank;
	}
	public void setPageRank(Map<String, Double> pageRank) {
		this.pageRank = pageRank;
	}
	public Map<String, Double> getNewPageRank() {
		return newPageRank;
	}
	public void setNewPageRank(Map<String, Double> newPageRank) {
		this.newPageRank = newPageRank;
	}
	public Map<String, Integer> getOutLinkes() {
		return outLinkes;
	}
	public void setOutLinkes(Map<String, Integer> outLinkes) {
		this.outLinkes = outLinkes;
	}
	public int[] getCombinedPerplexity() {
		return combinedPerplexity;
	}
	public void setCombinedPerplexity(int[] combinedPerplexity) {
		this.combinedPerplexity = combinedPerplexity;
	}
	public int getCombinedPages() {
		return combinedPages;
	}
	public void setCombinedPages(int combinedPages) {
		this.combinedPages = combinedPages;
	}
	public double getPerplexity() {
		return perplexity;
	}
	public void setPerplexity(double perplexity) {
		this.perplexity = perplexity;
	}
	public SortedByInLink getSortedByInLink() {
		return sortedByInLink;
	}
	public void setSortedByInLink(SortedByInLink sortedByInLink) {
		this.sortedByInLink = sortedByInLink;
	}
	public SortedByRank getSortedByRank() {
		return sortedByRank;
	}
	public void setSortedByRank(SortedByRank sortedByRank) {
		this.sortedByRank = sortedByRank;
	}
	public SortedMap<String, Double> getSortedPagesByRank() {
		return sortedPagesByRank;
	}
	public void setSortedPagesByRank(SortedMap<String, Double> sortedPagesByRank) {
		this.sortedPagesByRank = sortedPagesByRank;
	}
	public SortedMap<String, String> getSortedPagesByInLinks() {
		return sortedPagesByInLinks;
	}
	public void setSortedPagesByInLinks(SortedMap<String, String> sortedPagesByInLinks) {
		this.sortedPagesByInLinks = sortedPagesByInLinks;
	}
}
