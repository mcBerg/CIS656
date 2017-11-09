package clock;

import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.json.JSONException;
import org.json.JSONObject;

import jdk.nashorn.internal.parser.JSONParser;
import jdk.nashorn.internal.runtime.ParserException;

public class VectorClock implements Clock {

	// suggested data structure ...
	private TreeMap<String, Integer> clock = new TreeMap<String, Integer>(new StringNumComparator()); // Automatically sort by pid.

	@Override
	public void update(Clock other) {
		Set<String> pids = new TreeSet<String>(new StringNumComparator());
		for (String s : clock.keySet()) {
			pids.add(s);
		}
		for (String s : getPids(other.toString())) {
			pids.add(s);
		}
		// Now ordered set of pids
		VectorClock clock = new VectorClock();
		for (String pid : pids) {
			//System.out.println(pid+" "+this.getTime(pid)+" "+other.getTime(Integer.valueOf(pid)));
			clock.addProcess(Integer.valueOf(pid), Math.max(this.getTime(pid), other.getTime(Integer.valueOf(pid))));
		}
		setClock(clock);

	}

	@Override
	public void setClock(Clock other) {
		setClockFromString(other.toString());
	}

	@Override
	public void tick(Integer pid) {
		tick(Integer.toString(pid));
	}

	public void tick(String pid) {
		clock.put(pid, clock.get(pid) + 1);
	}

	@Override
	public boolean happenedBefore(Clock other) {
		Set<String> pids = new TreeSet<String>(new StringNumComparator());
		for (String s : clock.keySet()) {
			pids.add(s);
		}
		for (String s : getPids(other.toString())) {
			pids.add(s);
		}
		boolean allGOE = true;
		for (String key : pids) {
			if (other.getTime(Integer.valueOf(key)) < getTime(Integer.valueOf(key))) {
				allGOE = false;
			}
		}
		return allGOE;
	}

	public static String[] getPids(String other) {
		JSONObject jsonObject = new JSONObject(other);
		return JSONObject.getNames(jsonObject);
	}

	public String toString() {
		String x = "{";
		for (String s : clock.keySet()) {
			x += "\"" + s + "\":" + clock.get(s) + ",";
		}
		if (x.contains(",")) {
			x = x.substring(0, x.length() - 1);
		}
		x += "}";
		return x;
	}

	@Override
	public void setClockFromString(String clock) {
		if ("{}".equals(clock)) {
			this.clock = new TreeMap<String, Integer>(new StringNumComparator());
			return;
		}
		JSONObject jsonObject = new JSONObject(clock);
		for (String s : JSONObject.getNames(jsonObject)) {
			try {
				jsonObject.getInt(s);
			} catch (JSONException e) {
				// Malformed String... fail silently, do not apply the whole string.
				return;
			}
		}
		for (String s : JSONObject.getNames(jsonObject)) {
			this.clock.put(s, jsonObject.getInt(s));
		}
	}

	@Override
	public int getTime(int p) {
		String key = Integer.toString(p);
		if (clock.containsKey(key)) {
			return clock.get(key);
		}
		return 0;
	}

	public int getTime(String p) {
		return getTime(Integer.valueOf(p));
	}

	@Override
	public void addProcess(int p, int c) {
		clock.put(Integer.toString(p), c);

	}
}
