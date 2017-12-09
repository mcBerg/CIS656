package client;

import java.io.IOException;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;

public class ChordConnectionListener implements Runnable {

	ChordInfo reg;
	ServerSocket socketListener = null;

	public ChordConnectionListener(ChordInfo reg) {
		this.reg = reg;

	}

	public void close() {
		try {
			if (!socketListener.isClosed()) {
				socketListener.close();
			}
		} catch (IOException e) {
			System.out.println("Socket Listener could not be closed");
		}
	}

	@Override
	public void run() {
		while (socketListener == null) {
			try {
				socketListener = new ServerSocket(reg.getPort());
			} catch (BindException e) {
				System.out.println("Port in use. Will increment.");
				reg.setPort(reg.getPort() + 1);
				if (reg.getPort() > 65535) {
					System.out.println("No open ports. Will exit.");
					System.exit(1);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		System.out.println("Listening for incoming sockets on: " + socketListener.getLocalPort());
		try {
			Socket s = null;

			while (s == null || !s.isClosed()) {
				s = socketListener.accept();
				Thread t = new Thread(new ChordConnection(s, reg));
				t.start();
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
