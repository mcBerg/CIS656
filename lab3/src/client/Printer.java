package client;

public class Printer implements Runnable {

	VectorClockClient client;
	
	public Printer(VectorClockClient client) {
		this.client = client;
	}
	
	@Override
	public void run() {
		client.print();
	}

}
