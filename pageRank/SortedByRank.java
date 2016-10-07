package pageRank;

import java.util.Comparator;
import java.util.Map;

public class SortedByRank implements Comparator<String>{
	Map<String, Double> pair;

	public SortedByRank(Map<String, Double> pair) {
		this.pair = pair;
	}

	public int compare(String to, String from) {
		return (pair.get(to) >= pair.get(from)) ? -1 : 1;
	}
}
