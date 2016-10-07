package Evaluation;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.Vector;

class Evaluator {

	private static String query;
	private static HashMap<String, HashMap<Integer, Double>> scoredJudgments;
	private static HashMap<String, HashMap<Integer, Double>> relevantJudgments;
	private static Vector<Vector<String>> data;
	private static Vector<Double> recallValues;
	private static Vector<Double> precisionValues;

	public static void main(String[] args) throws IOException {
		scoredJudgments = new HashMap<String, HashMap<Integer, Double>>();
		relevantJudgments = new HashMap<String, HashMap<Integer, Double>>();

		String p = args[0];

		processRelevanceJudgments(p, relevantJudgments, scoredJudgments);

		data = new Vector<Vector<String>>();
		data = processInput();

		System.out.println("\nPrecision: ");
		for (int i = 1; i <= 100; i++) {
			System.out.println(calculatePrecision(relevantJudgments, data, i));
		}

		System.out.println("\nRecall: ");
		for (int i = 1; i <= 100; i++) {
			System.out.println(calculateRecall(relevantJudgments, data, i));
		}

		System.out.println("\nNDCG: ");
		for (int i = 1; i <= 100; i++) {
			System.out.println(computeNDCG(query, i));
		}

		System.out.println("\nDCG: ");
		for (int i = 1; i <= 100; i++) {
			System.out.println(computeDCG(query, i));
		}

		System.out.println("\nIDCG: ");
		for (int i = 1; i <= 100; i++) {
			System.out.println(computeIDCG(query, i));
		}

		System.out.println("Average Precision: " + averagePrecision(relevantJudgments, data));

	}
	
	public static double computeNDCG(String query, int point) {
		return computeDCG(query, point) / computeIDCG(query, point);
	}

	public static void processRelevanceJudgments(String p, HashMap<String, HashMap<Integer, Double>> relevanceJudgments,
			HashMap<String, HashMap<Integer, Double>> scoredJudgments) {
		try {
			BufferedReader reader = new BufferedReader(new FileReader(p));
			try {
				String line = null;
				while ((line = reader.readLine()) != null) {
					Scanner s = new Scanner(line).useDelimiter(" ");
					String query = s.next();
					String ignorableString = s.next();
					int did = Integer.parseInt(s.next().substring(5));
					String relevance = s.next();

					double relScore = Double.valueOf(relevance);
					double rel = Double.valueOf(relevance);

					if (relevanceJudgments.containsKey(query) == false) {
						HashMap<Integer, Double> qr = new HashMap<Integer, Double>();
						relevanceJudgments.put(query, qr);

						HashMap<Integer, Double> qr_scored = new HashMap<Integer, Double>();
						scoredJudgments.put(query, qr_scored);
					}

					HashMap<Integer, Double> qr = relevanceJudgments.get(query);
					qr.put(did, rel);

					HashMap<Integer, Double> qr_scored = scoredJudgments.get(query);
					qr_scored.put(did, relScore);
				}
			} finally {
				reader.close();
			}
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
	}

	public static Vector<Vector<String>> processInput() {
		Vector<Vector<String>> data = new Vector<Vector<String>>();
		Vector<String> row;
		try {
			System.out.println("Provide input: \n");
			BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

			String line = null;
			while ((line = reader.readLine()) != null) {
				row = new Vector<String>();
				Scanner s = new Scanner(line).useDelimiter("\t");
				query = s.next();
				String did = s.next();
				row.add(query);
				row.add(did);
				data.add(row);
			}
		} catch (Exception e) {
			System.err.println();
		}
		return data;
	}
	
	public static void computeRecallPrecision(HashMap<String, HashMap<Integer, Double>> relevance_judgments,
			Vector<Vector<String>> data) {

		recallValues = new Vector<Double>();
		precisionValues = new Vector<Double>();
		int averagePoint = data.size();
		for (int i = 1; i <= averagePoint; i++) {
			recallValues.add(calculateRecall(relevance_judgments, data, i));
			precisionValues.add(calculatePrecision(relevance_judgments, data, i));
		}
	}

	public static double averagePrecision(HashMap<String, HashMap<Integer, Double>> relevance_judgments,
			Vector<Vector<String>> data) {

		computeRecallPrecision(relevance_judgments, data);

		double precisionSum = precisionValues.get(0);
		double previous = recallValues.get(0);
		double count = 0;
		if (previous != 0d) {
			count = 1;
		}
		for (int i = 1; i < recallValues.size(); i++) {
			if (recallValues.get(i) != previous) {
				previous = recallValues.get(i);
				precisionSum += precisionValues.get(i);
				count++;
			}
		}

		if (count != 0.0d) {
			return (precisionSum / count);
		} else {
			return 0;
		}
	}

	public static double calculatePrecision(HashMap<String, HashMap<Integer, Double>> relevance_judgments,
			Vector<Vector<String>> data, int precisionPoint) {
		double RR = 0.0;
		double N = 0.0;
		try {
			int lineCount = 0;
			for (int i = 0; i < data.size(); i++) {
				lineCount++;
				if (lineCount > precisionPoint)
					break;
				Vector<String> row = data.get(i);
				String query = row.get(0);
				int did = Integer.parseInt(row.get(1));
				if (relevance_judgments.containsKey(query) == false) {
					throw new IOException("Information not available");
				}
				HashMap<Integer, Double> qr = relevance_judgments.get(query);
				if (qr.containsKey(did) != false) {
					RR += qr.get(did);
				}
				++N;
			}
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}

		if (RR != 0.0 && N != 0.0)
			return (RR / N);
		else
			return 0.0;
	}

	public static double calculateRecall(HashMap<String, HashMap<Integer, Double>> relevance_judgments,
			Vector<Vector<String>> data, int recallPoint) {
		double RR = 0.0;
		double N = 0.0;
		String query = "";
		try {
			int lineCount = 0;
			for (int i = 0; i < data.size(); i++) {
				lineCount++;
				if (lineCount > recallPoint)
					break;
				Vector<String> row = data.get(i);
				query = row.get(0);
				int did = Integer.parseInt(row.get(1));
				if (relevance_judgments.containsKey(query) == false) {
					throw new IOException("Information not available");
				}
				HashMap<Integer, Double> qr = relevance_judgments.get(query);
				if (qr.containsKey(did) != false) {
					RR += qr.get(did);
				}
			}
			if (relevance_judgments.containsKey(query) == false) {
				throw new IOException("Information not available");
			}
			HashMap<Integer, Double> qr = relevance_judgments.get(query);
			Iterator<Entry<Integer, Double>> it = qr.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry<Integer, Double> pairs = (Map.Entry<Integer, Double>) it.next();
				if (pairs.getValue() != 0d) {
					N += pairs.getValue();
				}
			}
		} catch (Exception e) {
			System.err.println("Error:" + e.getMessage());
		}
		if (RR != 0.0 && N != 0.0)
			return (RR / N);
		else
			return 0.0;
	}

	public static Vector<Double> getRecallValues() {
		return recallValues;
	}

	public static Vector<Double> getPrecisionValues() {
		return precisionValues;
	}

	public static Map<Integer, Double> sort(HashMap<Integer, Double> unsortedMap) {

		List<Map.Entry<Integer, Double>> list = new LinkedList<Map.Entry<Integer, Double>>(unsortedMap.entrySet());

		Collections.sort(list, new Comparator<Map.Entry<Integer, Double>>() {
			@Override
			public int compare(Map.Entry<Integer, Double> o1, Map.Entry<Integer, Double> o2) {
				return (o2.getValue()).compareTo(o1.getValue());
			}
		});

		Map<Integer, Double> result = new LinkedHashMap<Integer, Double>();
		for (Map.Entry<Integer, Double> entry : list) {
			result.put(entry.getKey(), entry.getValue());
		}
		return result;
	}

	public static double computeDCG(String query, int point) {
		double dcg = 1.0;
		HashMap<Integer, Double> qr = scoredJudgments.get(query);

		if (point == 1) {
			for (Vector<String> queryDid : data) {
				if (qr.get(Integer.parseInt(queryDid.get(1))) != null) {
					dcg = qr.get(Integer.parseInt(queryDid.get(1)));
				}
				break;
			}
		} else {
			for (Vector<String> queryDid : data) {
				if (qr.get(Integer.parseInt(queryDid.get(1))) != null) {
					dcg = qr.get(Integer.parseInt(queryDid.get(1)));
				}
				break;
			}

			int count = 0;
			for (Vector<String> queryDid : data) {
				count++;
				if (count == 1) {
					continue;
				} else if (count > point) {
					break;
				} else {
					if (qr.get(Integer.parseInt(queryDid.get(1))) != null) {
						dcg += qr.get(Integer.parseInt(queryDid.get(1))) / Math.log(count);
					}
				}
			}
		}
		return dcg;
	}

	public static double computeIDCG(String query, int point) {
		double idealDcg = 0.0;
		HashMap<Integer, Double> qr = scoredJudgments.get(query);
		Map<Integer, Double> qrSorted = sort(qr);
		if (point == 1) {
			for (Map.Entry<Integer, Double> entry : qrSorted.entrySet()) {
				idealDcg = entry.getValue();
				break;
			}
		} else {
			for (Map.Entry<Integer, Double> entry : qrSorted.entrySet()) {
				idealDcg = entry.getValue();
				break;
			}
			int count = 0;

			for (Map.Entry<Integer, Double> entry : qrSorted.entrySet()) {
				count++;
				if (count == 1) {
					continue;
				} else if (count > point) {
					break;
				} else {
					idealDcg += (entry.getValue() / Math.log(count));
				}
			}
		}
		return idealDcg;
	}
}