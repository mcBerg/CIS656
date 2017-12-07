package client;

import java.io.IOException;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;

public class ChordConnectionListener implements Runnable {

	ChordInfo reg;

	public ChordConnectionListener(ChordInfo reg) {
		this.reg = reg;

	}

	@Override
	public void run() {
		ServerSocket socketListener = null;
		while (socketListener == null) {
			try {
				socketListener = new ServerSocket(reg.getPort());
			} catch (BindException e) {
				System.out.println("Port in use. Will increment.");
				reg.setPort(reg.getPort() + 1);
				if (reg.getPort() > 9999) {
					System.out.println("No open ports. Will exit.");
					System.exit(1);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		while (true) {
			Socket s;
			try {
				s = socketListener.accept();
				Thread t = new Thread(new ChordConnection(s, reg));
				t.start();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
