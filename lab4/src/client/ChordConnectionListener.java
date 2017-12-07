package client;

import java.io.IOException;
import java.io.Serializable;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Iterator;
import java.util.Set;

import chord.StringKey;
import de.uniba.wiai.lspi.chord.service.Chord;
import de.uniba.wiai.lspi.chord.service.ServiceException;

public class ChordConnectionListener implements Runnable {

	StringKey myKey;
	Chord chord;
	Set<Serializable> vals = null;

	public ChordConnectionListener(String hostname, Chord chord) {
		myKey = new StringKey(hostname);
		this.chord = chord;
	}

	@Override
	public void run() {
		while (true) {
			try {
				vals = chord.retrieve(myKey);
				Iterator<Serializable> it = vals.iterator();
				while (it.hasNext()) {
					String data = (String) it.next();
					System.out.println("Got [" + data + "]");
				}
			} catch (ServiceException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
