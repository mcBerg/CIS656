package lab1;

public class ProgramA {
	static Integer counter = 0;

	public static void main(String[] args) {
		System.out.println("Run ("+args[0]+")");
		Long startTime = System.currentTimeMillis();
		Thread[] threads = new Thread[Integer.valueOf(args[0])];
		for (int x = 0; x < Integer.valueOf(args[0]); x++) {
			threads[x] = new Thread(new Counter());
			threads[x].start();
		}
		for (Thread x : threads) {
			try {
				x.join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		System.out.println(counter);
		Long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	public static synchronized void inc() {
		counter++;
	}
}

class Counter implements Runnable {
	public void run() {
		for (Integer counts = 1000000; counts > 0; counts--) {
			ProgramA.inc();
		}
	}
}
