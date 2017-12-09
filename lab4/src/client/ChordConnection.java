package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class ChordConnection implements Runnable {

	Socket connection;
	ChordInfo reg;

	public ChordConnection(Socket s, ChordInfo reg) {
		connection = s;
		this.reg = reg;
	}

	@Override
	public void run() {
		InputStreamReader in;
		try {
			in = new InputStreamReader(connection.getInputStream());
			BufferedReader reader = new BufferedReader(in);
			while (!connection.isClosed()) {
				if (reader.ready()) {
					String x = reader.readLine();
					if (reg.getStatus()) {
						System.out.println();
						System.out.println(x);
						System.out.print(reg.getUserName()+": ");
					}
				}
			}
		} catch (IOException e) {
			// fail silently on disconnect.
			System.out.println(this.connection.getPort()+ " disconnected");
			//e.printStackTrace();
		} finally {
			try {
				this.connection.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

}
