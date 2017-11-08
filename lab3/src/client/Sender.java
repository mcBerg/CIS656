package client;

public class Sender implements Runnable {
	
	VectorClockClient client;
	
	public Sender(VectorClockClient client) {
		this.client = client;
	}

	@Override
	public void run() {
		client.send();
	}

}
