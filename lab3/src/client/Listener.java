package client;

public class Listener implements Runnable{

	VectorClockClient client;
	
	public Listener(VectorClockClient client) {
		this.client = client;
	}

	@Override
	public void run() {
		client.listen();
	}
	
	
	
}
