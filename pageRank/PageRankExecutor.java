package pageRank;

public class PageRankExecutor {

	public static void main(String[] args) {
		PageRankCalculator pageRankCalculator = new PageRankCalculator();

		//String filePath = "F:/PageRank/test-inlinks.txt";
		String filePath = "F:/PageRank/wt2g-inlinks.txt";
		pageRankCalculator.generateGraph(filePath);
		pageRankCalculator.calculateRank();
		
		pageRankCalculator.getTopRankedDocuments();
		pageRankCalculator.getTopDocumentsByInlinks();
		
		pageRankCalculator.calculateProportions();
	}
}
 