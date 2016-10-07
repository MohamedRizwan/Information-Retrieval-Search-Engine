package indexingAndRetrieval;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class BM25 {

	static Map<String, HashMap<Integer, Integer>> tokenData = new HashMap<String, HashMap<Integer, Integer>>();
	static HashMap<Integer, Integer> documentLength = new HashMap<Integer, Integer>();
	static int displayCount;

	static double avdl = 0.0;
	static double k1;
	static double k2;
	static double b;

	static ArrayList<String> queries = new ArrayList<String>();
	
	PrintWriter writer = null;
	
	public BM25(int count){
		displayCount = count;
		k1 = 1.2;
		k2 = 100.0;
		b = 0.75;
	}

	public static void main(String[] args) {
		BM25 bm25 = new BM25(Integer.parseInt(args[2]));
		bm25.readIndexAndQuery(args[0], args[1]);		
		bm25.calculateBM25Rank();
	}
	
	private void readIndexAndQuery(String indexFileName, String queriesFileName) {
		BufferedReader queryReader = null;
		BufferedReader indexReader = null;

		try {
			queryReader = new BufferedReader(new FileReader(queriesFileName));
			indexReader = new BufferedReader(new FileReader(indexFileName));
			String queryLine = "";

			while ((queryLine = queryReader.readLine()) != null) {
				queries.add(queryLine);
			}

			String indexLine = "";
			while (!(indexLine = indexReader.readLine()).startsWith("#")) {
				int i = indexLine.indexOf(' ');
				String word = indexLine.substring(0, i);

				if (wordPresentInAnyQuery(word) == true) {
					String[] invertedLists = indexLine.split("\\{");

					HashMap<Integer, Integer> wordHm = new HashMap<Integer, Integer>();
					for (int j = 1; j < invertedLists.length; j++) {

						String[] furtherSplits = invertedLists[j].split(",");

						String documentId = furtherSplits[0];
						String queryTermFrequencyInDocument = furtherSplits[1].substring(0,
								furtherSplits[1].length() - 2);

						wordHm.put(Integer.parseInt(documentId), Integer.parseInt(queryTermFrequencyInDocument));
					}
					tokenData.put(word, wordHm);
				}
			}
			
			while ((indexLine = indexReader.readLine()) != null) {
				String[] lineSplits = indexLine.split(" ");
				documentLength.put(Integer.parseInt(lineSplits[0]), Integer.parseInt(lineSplits[1]));
			}			
			computeAvdl();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				queryReader.close();
				indexReader.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	private void computeAvdl() {
		double sum = 0.0;
		for (Entry<Integer, Integer> docLength : documentLength.entrySet()) {
			sum = sum + (double) docLength.getValue();
		}
		avdl = sum / (double) documentLength.size();
	}

	private void calculateBM25Rank() {		
		try {
			writer = new PrintWriter("results.eval", "UTF-8");
			
			String[] wordsInQuery = null;
			for (int i = 0; i < queries.size(); i++) {
				wordsInQuery = queries.get(i).split(" ");

				ArrayList<Integer> docIdsOfQuery = getDocIdsOfQuery(wordsInQuery);
				HashMap<Integer, Double> docBMScore = new HashMap<Integer, Double>();

				for (int doc : docIdsOfQuery) {
					double bm25Score = 0.0;

					for (int queryWordCount = 0; queryWordCount < wordsInQuery.length; queryWordCount++) {
						
						double N = (double) documentLength.size();
						double ni = (double) (tokenData.get(wordsInQuery[queryWordCount]).size());
						double numerator = 1; //(0 + 0.5)/(0 - 0 + 0.5)
						double denominator = (ni + 0.5) / (N - ni + 0.5);

						double firstTerm = Math.log(numerator / denominator);

						double K = k1 * ((1 - b) + b * (double) documentLength.get(doc) / avdl);

						double fi = 0.0;

						if (((HashMap<Integer, Integer>) tokenData.get(wordsInQuery[queryWordCount])).containsKey(doc)){
							fi = ((HashMap<Integer, Integer>) tokenData.get(wordsInQuery[queryWordCount])).get(doc);
						}

						double secondTerm = (k1 + 1) * fi / (K + fi);
						double qfi = 1.0;
						double thirdTerm = (k2 + 1) * qfi / (k2 + qfi);

						bm25Score = bm25Score + (firstTerm * secondTerm * thirdTerm);
					}
					docBMScore.put(doc, bm25Score);
				}
				
				//Persisting BM Score
				
				int count = 1;
				String hostname = "";
				
				try {
					hostname = InetAddress.getLocalHost().getHostName();
				}catch (UnknownHostException e){
					e.printStackTrace();
				}
				List<Map.Entry<Integer, Double>> list = new LinkedList<Map.Entry<Integer, Double>>(docBMScore.entrySet());
				Collections.sort(list, new Comparator<Map.Entry<Integer, Double>>() {

					public int compare(Map.Entry<Integer, Double> p1, Map.Entry<Integer, Double> p2) {
						return -(p1.getValue().compareTo(p2.getValue()));
					}
				});

				for (Map.Entry<Integer, Double> entry : list) {
					if (count <= displayCount) {
						writer.println(i + 1 + " Q0 " + entry.getKey() + " " + count + " " + entry.getValue() + " " + hostname);
						count++;
					} else
						break;
				}
				
				//Persisting BM Score ends
			}
			writer.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	private ArrayList<Integer> getDocIdsOfQuery(String[] wordsInQuery) {

		ArrayList<Integer> docIDsOfQuery = new ArrayList<Integer>();
		for (int wordCount = 0; wordCount < wordsInQuery.length; wordCount++) {
			HashMap<Integer, Integer> posting = tokenData.get(wordsInQuery[wordCount]);

			for (Entry<Integer, Integer> pointer : posting.entrySet()) {
				if (!docIDsOfQuery.contains(pointer.getKey()))
					docIDsOfQuery.add(pointer.getKey());
			}
		}
		return docIDsOfQuery;

	}

	private boolean wordPresentInAnyQuery(String word) {
		String[] listOfWordsInQuery = null;

		for (int i = 0; i < queries.size(); i++) {
			listOfWordsInQuery = queries.get(i).split(" ");

			for (int j = 0; j < listOfWordsInQuery.length; j++) {
				if (word.equalsIgnoreCase(listOfWordsInQuery[j]))
					return true;
			}
		}
		return false;
	}

}
