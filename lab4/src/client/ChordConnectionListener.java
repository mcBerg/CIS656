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
import de.uniba.wiai.lspi.chord.service.ChordRetrievalFuture;
import de.uniba.wiai.lspi.chord.service.ServiceException;

public class ChordConnectionListener implements Runnable {

	StringKey myKey;
	Chord chord;
	Set<Serializable> vals = null;
	String username;
	ChordInfo reg;

	public ChordConnectionListener(ChordClient chat) {
		this.reg = chat.getReg();
		this.username = chat.getReg().getUserName();
		myKey = new StringKey(username);
		this.chord = chat.getChord();
	}

	public ChordConnectionListener(ChordInfo reg, Chord chord) {
		this.reg = reg;
		this.username = reg.getUserName();
		myKey = new StringKey(username);
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

					if (reg.getStatus()) {
						System.out.println();
						System.out.println(data);
						System.out.print(username + ":");
					}
					chord.remove(myKey, data);
				}
			} catch (ServiceException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
