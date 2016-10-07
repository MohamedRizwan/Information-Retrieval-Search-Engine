package pageRank;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.Map.Entry;

public class PageRankCalculator {

	private static final double DAMPING_FACTOR = 0.85;
	private static final String SPACE_DELIMITER = " ";
	private static final String MINUS = "-";
	private static int iteration = 1;

	PageRank pageRank = new PageRank();

	public void generateGraph(String filePath) {
		String line = "";
		BufferedReader bufferedReader = null;
		try {
			bufferedReader = new BufferedReader(new FileReader(new File(filePath)));
			while ((line = bufferedReader.readLine()) != null) {
				constructLinks(line);
			}
			pageRank.setCombinedPages(pageRank.getPages().size());

			double initial_page_rank = 1.0 / pageRank.getCombinedPages();

			// initialize page rank for all pages
			for (String page : pageRank.getPages()) {
				// initialize sink nodes for all pages
				if (!pageRank.getOutLinkes().containsKey(page)) {
					pageRank.getSinkNodes().add(page);
				}
				pageRank.getPageRank().put(page, initial_page_rank);
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void constructLinks(String line) {
		// get the links by splitting the line with space delimiter
		String[] currentPageLinks = line.split(SPACE_DELIMITER, 2);
		
		//ignore the pages which doesn't have inlinks
		if (currentPageLinks.length > 1) {

			// put the first link as a key and second link as a
			// value inside
			// the hashmap containing pages with links
			pageRank.getPagesHavingLinks().put(currentPageLinks[0], currentPageLinks[1]);
			pageRank.setCurrentPage(currentPageLinks[0]);

			// if current page not in the all pages set add to it.
			if (!pageRank.getPages().contains(pageRank.getCurrentPage())) {
				pageRank.getPages().add(pageRank.getCurrentPage());
			}

			String[] links = currentPageLinks[1].split(SPACE_DELIMITER);
			// for all links in the current page

			for (String link : links) {
				// add each link to pages set
				if (!pageRank.getPages().contains(link)) {
					pageRank.getPages().add(link);
				}
				// check is the link exist in the out links map
				// If exist increment the out link value by one
				// else insert new entry in the map for the link with value 1
				if (pageRank.getOutLinkes().containsKey(link)) {
					pageRank.getOutLinkes().put(link, 1 + pageRank.getOutLinkes().get(link));
				} else {
					pageRank.getOutLinkes().put(link, 1);
				}
			}
		} else {
			// if the link doesn't have any inlinks, set the value to '-'
			pageRank.getPagesHavingLinks().put(currentPageLinks[0], MINUS);
			pageRank.getPages().add(currentPageLinks[0]);
		}
	}

	public void calculateRank() {
		// calculate and set the initial perplexity
		pageRank.setPerplexity(calculatePerplexity(pageRank.getPageRank()));

		// run the PageRank algorithm till the perplexity value converges
		while (!hasConverged()) {
			pageRank.setPerplexity(calculatePerplexity(pageRank.getPageRank()));
			System.out.println("Iteration:" + iteration + " Perplexity:" + pageRank.getPerplexity());

			// calculate sink rank of sink pages
			pageRank.setSinkRank(0.0);
			for (String sinkPage : pageRank.getSinkNodes()) {
				pageRank.setSinkRank(pageRank.getSinkRank() + pageRank.getPageRank().get(sinkPage));
			}

			// for all pages calculate intermediate page rank
			for (String page : pageRank.getPages()) {
				pageRank.getNewPageRank().put(page, (1.0 - DAMPING_FACTOR) / pageRank.getCombinedPages());
				pageRank.getNewPageRank().put(page, pageRank.getNewPageRank().get(page)
						+ (DAMPING_FACTOR * pageRank.getSinkRank() / pageRank.getCombinedPages()));
				
				if (pageRank.getPagesHavingLinks().containsKey(page)) {
					// if the page have in links to it
					if (!pageRank.getPagesHavingLinks().get(page).equals(MINUS)) {
						// for each in link pages to the page
						for (String link : pageRank.getPagesHavingLinks().get(page).split(SPACE_DELIMITER)) {
							pageRank.getNewPageRank().put(page,
									pageRank.getNewPageRank().get(page)
											+ DAMPING_FACTOR * pageRank.getPageRank().get(link)
													/ pageRank.getOutLinkes().get(link));
						}
					}
				}
			}

			for (String page : pageRank.getPageRank().keySet()) {
				pageRank.getPageRank().put(page, pageRank.getNewPageRank().get(page));
			}
			iteration++;
		}
	}

	public double calculatePerplexity(Map<String, Double> map) {
		return Math.pow(2.0, getEntropy(map));
	}

	public static double getEntropy(Map<String, Double> map) {
		double val = 0.0;
		for (String page : map.keySet()) {
			val += ((Math.log(map.get(page))) / (Math.log(2.0)) * map.get(page));
		}
		return -val;
	}

	private boolean hasConverged() {
		boolean result = true;
		int[] combinedPerplexity = pageRank.getCombinedPerplexity();
		if (combinedPerplexity.length != 4) {
			combinedPerplexity[iteration % 4] = (int) pageRank.getPerplexity();
			pageRank.setCombinedPerplexity(combinedPerplexity);
			result = false;
		} else {
			combinedPerplexity[0] = combinedPerplexity[1];
			combinedPerplexity[1] = combinedPerplexity[2];
			combinedPerplexity[2] = combinedPerplexity[3];
			combinedPerplexity[3] = (int) pageRank.getPerplexity();

			pageRank.setCombinedPerplexity(combinedPerplexity);

			// calculate total perplexity
			int totalPerplexity = combinedPerplexity[0] + combinedPerplexity[1] + combinedPerplexity[2]
					+ combinedPerplexity[3];

			// if total perplexity = 4 * current perplexity print, then return false
			if (totalPerplexity - (4 * (int) pageRank.getPerplexity()) == 0) {
				System.out.println("Perplexity at iteration: " + (iteration - 1) + " : " + pageRank.getPerplexity());
				result = true;
			} else {
				result = false;
			}
		}
		return result;
	}

	public void getTopRankedDocuments() {
		pageRank.getSortedPagesByRank().putAll(pageRank.getPageRank());
		int count = 1;
		System.out.println("\nDocument IDs of top 50 pages sorted by Pagerank");
		for (Entry<String, Double> entry : pageRank.getSortedPagesByRank().entrySet()) {
			if (count > 50) {
				break;
			} else
				System.out.println((count++) + ". " + entry.getKey() + ": " + entry.getValue());
		}
		//uncomment if you want to generate a file with page rank
		//persistSortedPagesByRank();
	}

	public void getTopDocumentsByInlinks() {
		pageRank.getSortedPagesByInLinks().putAll(pageRank.getPagesHavingLinks());
		int count = 1;
		System.out.println("\nDocument IDs of top 50 pages sorted by Iin-link count");
		for (Entry<String, String> entry : pageRank.getSortedPagesByInLinks().entrySet()) {
			if (count > 50) {
				break;
			} else
				System.out.println((count++) + ". " + entry.getKey() + ": " + entry.getValue().split(SPACE_DELIMITER).length);
		}
		//uncomment if you want to generate a file with in links count
		// persistSortedPagesByInLink();
	}
	
	private void persistSortedPagesByRank() {
		PrintWriter writer;
		try {
			File file = new File("F:/PageRank/Pages Sorted by PageRank.txt");
			file.createNewFile();
			FileWriter fw = new FileWriter(file);
			writer = new PrintWriter(fw);
			int count = 1;
			for (Entry<String, Double> entry : pageRank.getSortedPagesByRank().entrySet()) {
				writer.println((count++) + ". " + entry.getKey() + ": " + entry.getValue());
			}
			writer.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	private void persistSortedPagesByInLink() {
		PrintWriter writer;
		try {
			File file = new File("F:/PageRank/Pages sorted by In-Links.txt");
			file.createNewFile();
			FileWriter fw = new FileWriter(file);
			writer = new PrintWriter(fw);
			int count = 1;
			for (Entry<String, String> entry : pageRank.getSortedPagesByInLinks().entrySet()) {
				writer.println((count++) + ". " + entry.getKey() + ": " + entry.getValue().split(SPACE_DELIMITER).length);
			}
			writer.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	public void calculateProportions() {
		int numberOfPagesWithoutInLinks = 0;
		int numberOfPagesWithLowerRankThanInitial = 0;
		for (Entry<String,String> entry : pageRank.getPagesHavingLinks().entrySet()) {
			if(entry.getValue().equals(MINUS))
				numberOfPagesWithoutInLinks ++;
		}
		
		Double propOfPagesWithoutInLinks = (double) numberOfPagesWithoutInLinks / pageRank.getPages().size();
		Double propOfSinks = (double) pageRank.getSinkNodes().size() / pageRank.getPages().size();
		
		System.out.println("Proportion of pages with no in-links (sources): "+ propOfPagesWithoutInLinks);
		System.out.println("Proportion of pages with no out-links (sinks) : " + propOfSinks);
		
		Double initial_page_rank = (Double) 1.0 / pageRank.getCombinedPages();		
		
		for(Entry<String,Double> entry : pageRank.getPageRank().entrySet()){
			if(entry.getValue().compareTo(initial_page_rank) < 0){
				numberOfPagesWithLowerRankThanInitial ++;
			}
		}
		
		Double propOfPagesWithLowerRank = (double) numberOfPagesWithLowerRankThanInitial / pageRank.getPages().size();
		System.out.println("Proportion of pages whose PageRank is less than their initial, uniform values : "+ propOfPagesWithLowerRank);
		
	}
}
