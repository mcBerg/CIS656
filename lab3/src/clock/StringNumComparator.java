package clock;

import java.util.Comparator;

public class StringNumComparator implements Comparator<String> {
	@Override
	public int compare(String o1, String o2) {
		return Integer.compare(Integer.valueOf(o1), Integer.valueOf(o2));
	}
}
