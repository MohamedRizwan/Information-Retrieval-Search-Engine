package indexingAndRetrieval;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class Indexer {

	static Map<String, HashMap<Integer, Integer>> tokenData = new HashMap<String, HashMap<Integer, Integer>>();
	static HashMap<Integer, Integer> documentLength = new HashMap<Integer, Integer>();

	public static void main(String[] args) {
		Indexer indexer = new Indexer();
		indexer.computeIndex(args[0]);
		indexer.generateIndexFile();
	}

	private void generateIndexFile() {
		PrintWriter writer = null;
		try {
			writer = new PrintWriter("index.out", "UTF-8");

			for (Entry<String, HashMap<Integer, Integer>> posting : tokenData.entrySet()) {
				writer.print(posting.getKey() + " :");
				for (Entry<Integer, Integer> pointer : posting.getValue().entrySet())
					writer.print(" {" + pointer.getKey() + "," + pointer.getValue() + "}");
				writer.print("|");
				writer.println();
			}

			writer.println("#####Document Length#####");
			for (Entry<Integer, Integer> docLength : documentLength.entrySet()) {
				writer.println(docLength.getKey() + " " + docLength.getValue());
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} finally {
			writer.close();
		}
	}

	private void computeIndex(String fileName) {
		String line;
		int document = 0;
		int docLength = 0;
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(fileName));

			while ((line = reader.readLine()) != null) {
				if (!line.startsWith("#")) {
					String[] words = line.split(" ");

					// ignoring numerics
					List<String> newWords = new ArrayList<String>();
					for (int i = 0; i < words.length; i++) {
						if (!isNumeric(words[i]))
							newWords.add(words[i]);
					}

					docLength = docLength + newWords.size();
					// docLength = docLength + words.length; if numerics has to
					// be considered for doc length

					for (int i = 0; i < words.length; i++) {
						//ignoring numerics
						if (!isNumeric(words[i])) {
							if (tokenData.containsKey(words[i])) {
								HashMap<Integer, Integer> pointer = tokenData.get(words[i]);
								if (!pointer.containsKey(document))
									pointer.put(document, 1);
								else {
									pointer.put(document, pointer.get(document) + 1);
								}
							} else {
								HashMap<Integer, Integer> pointer = new HashMap<Integer, Integer>();
								pointer.put(document, 1);
								tokenData.put(words[i], pointer);
							}
						}
					}
				} else {
					if (document > 0)
						documentLength.put(document, docLength);

					document = Integer.parseInt(line.substring(2, line.length()));
					docLength = 0;
				}
			}
			documentLength.put(document, docLength);

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				reader.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static boolean isNumeric(String str) {
		try {
			double d = Double.parseDouble(str);
		} catch (NumberFormatException nfe) {
			return false;
		}
		return true;
	}
}
