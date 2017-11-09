package message;

import java.util.Comparator;

import clock.VectorClockComparator;

/**
 * Message comparator class. Use with PriorityQueue.
 */
public class MessageComparator implements Comparator<Message> {

	private VectorClockComparator vcc = new VectorClockComparator();
	
    @Override
    public int compare(Message lhs, Message rhs) {
    	return vcc.compare(lhs.ts, rhs.ts);
    }

}
