package pageRank;

import java.util.Comparator;
import java.util.Map;

public class SortedByInLink implements Comparator<String>{
	Map<String, String> pair;
	
	private static final String SPACE = " ";

	public SortedByInLink(Map<String, String> pair) {
		this.pair = pair;
	}

	public int compare(String to, String with) {
		return (pair.get(to).split(SPACE).length >= pair.get(with).split(SPACE).length) ? -1 : 1;
	}

}
