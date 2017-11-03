package clock;

import java.util.LinkedHashMap;
import java.util.Map;

public class VectorClock implements Clock {

    // suggested data structure ...
    private Map<String,Integer> clock = new LinkedHashMap<String,Integer>();


    @Override
    public void update(Clock other) {
    	
    }

    @Override
    public void setClock(Clock other) {
    	
    }

    @Override
    public void tick(Integer pid) {
    	tick(pid.toString());
    }
    
    public void tick(String pid) {
    	clock.put(pid, clock.get(pid)+1);
    }

    @Override
    public boolean happenedBefore(Clock other) {
        System.out.println(clock.keySet().size());
    	for(String key : clock.keySet()) {
        	System.out.println(other.getTime(Integer.valueOf(key))+" > "+clock.get(key));
        	if (other.getTime(Integer.valueOf(key)) > clock.get(key)) {
        		return false;
        	}
        	
        }
        return true;
    }

    public String toString() {
        String x = "{";
        for(String s : clock.keySet()) {
        	x+="\"s\":"+clock.get(s)+",";
        }
        x=x.substring(0, x.length()-1);
        x+="}";
        return x;
    }

    @Override
    public void setClockFromString(String clock) {

    }

    @Override
    public int getTime(int p) {
        return 0;
    }

    @Override
    public void addProcess(int p, int c) {
    	System.out.println("Adding: "+p+":"+c);
    	clock.put(String.valueOf(p), c);

    }
}
