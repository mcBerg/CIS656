package clock;

import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.json.JSONObject;

import jdk.nashorn.internal.parser.JSONParser;

public class VectorClock implements Clock {

	// suggested data structure ...
	private TreeMap<String, Integer> clock = new TreeMap<String, Integer>(new StringNumComparator()); //Automatically sort by pid.

	@Override
	public void update(Clock other) {
		Set<String> pids = new TreeSet<String>(new StringNumComparator()); 
		for(String s : clock.keySet()) {
			pids.add(s);
		}
		for (String s : getPids(other.toString())) {
			pids.add(s);
		}
		//Now ordered set of pids
		Clock clock = new VectorClock();
		for (String pid : pids) {
			clock.addProcess(Integer.valueOf(pid), Math.max(clock.getTime(Integer.valueOf(pid)), other.getTime(Integer.valueOf(pid))));
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
		System.out.println(other.toString() + " > the hell " + this.toString());
		System.out.println("Here: "+other.toString());
		Set<String> pids = new TreeSet<String>(new StringNumComparator()); 
		System.out.println("Here: "+other.toString());
		for(String s : clock.keySet()) {
			pids.add(s);
		}
		System.out.println("Here: "+other.toString());
		for (String s : getPids(other.toString())) {
			pids.add(s);
		}
		//Now ordered set of pids
		for (String key : pids) {
			System.out.println(other.getTime(Integer.valueOf(key))+" > "+clock.get(key));
			if (other.getTime(Integer.valueOf(key)) > clock.get(key)) {
				return false;
			}
		}
		return true;
	}

	public String[] getPids(String other) {
		System.out.println("Made it here");
		System.out.println("Get Pids from: "+other);
		System.out.println("what the hell man");
		JSONObject jsonObject = new JSONObject(other);
		return JSONObject.getNames(jsonObject);
	}
	
	public String toString() {
		String x = "{";
		for (String s : clock.keySet()) {
			x += "\""+s+"\":" + clock.get(s) + ",";
		}
		x = x.substring(0, x.length() - 1);
		x += "}";
		return x;
	}

	@Override
	public void setClockFromString(String clock) {
		System.out.println("Json String: "+clock);
		JSONParser jsonParser = new JSONParser(clock, null, false);
		JSONObject jsonObject = (JSONObject) jsonParser.parse();
		this.clock = new TreeMap<String, Integer>(new StringNumComparator());
		for(String s : JSONObject.getNames(jsonObject)) {
			System.out.println("Name: "+s+" Value: "+jsonObject.get(s));
			this.clock.put(s, (Integer)jsonObject.get(s));
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

	@Override
	public void addProcess(int p, int c) {
		clock.put(Integer.toString(p), c);

	}
}
