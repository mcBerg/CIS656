package client;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;
import java.util.Set;
import java.util.StringTokenizer;

import chord.StringKey;
import compute.ChordPresenceService;
import de.uniba.wiai.lspi.chord.data.URL;
import de.uniba.wiai.lspi.chord.service.Chord;
import de.uniba.wiai.lspi.chord.service.PropertiesLoader;
import de.uniba.wiai.lspi.chord.service.ServiceException;
import de.uniba.wiai.lspi.chord.service.impl.ChordImpl;

public class ChordClient implements Serializable, ChordPresenceService {

	private static final long serialVersionUID = 5144150709351448519L;
	private ChordInfo reg;
	private Chord chord;
	private URL localURL;
	private URL masterURL;
	private boolean master;

	public ChordClient(ChordInfo reg, boolean master) {
		this.reg = reg;
		this.master = master;
	}

	@Override
	public boolean updateChordInfo() {
		try {
			StringKey myKey = new StringKey(reg.getUserName());
			for (Serializable s : chord.retrieve(myKey)) {
				System.out.println("Found registration: " + ((ChordInfo) s).toString());
				if (!s.equals(reg)) {
					chord.remove(myKey, (ChordInfo) s);
				}
			}
			if (chord.retrieve(myKey).size() == 0) {
				System.out.println("Adding new registration " + reg.toString());
				chord.insert(new StringKey(this.reg.getUserName()), this.reg);
			}
		} catch (ServiceException e) {
			System.out.println("Could not update info");
			return false;
		}
		return true;
	}

	@Override
	public ChordInfo lookup(String name) throws ServiceException {
		ChordInfo reg = null;
		StringKey key = new StringKey(name);
		Set<Serializable> registrations = this.chord.retrieve(key);
		for (Serializable s : registrations) {
			System.out.println("Lookup Returns: " + ((ChordInfo) s).toString());
		}
		if (registrations.size() > 0) {
			reg = (ChordInfo) registrations.toArray()[0];
		}

		return reg;
	}

	private void talk(StringTokenizer tk) {
		try {
			if (tk.hasMoreTokens()) {
				ChordInfo target = lookup(tk.nextToken());
				if (target != null) {
					if (target.getStatus()) {
						if (tk.hasMoreTokens()) {
							Socket send = new Socket(target.getHost(), target.getPort());
							OutputStreamWriter writer = new OutputStreamWriter(send.getOutputStream());
							String x = "";
							x += tk.nextToken("");
							writer.write("~" + reg.getUserName() + ":" + x);
							writer.flush();
							send.close();
						} else {
							System.out.println(
									"You'll need to type a message to send. Proper syntax is talk {username} {message}.");
						}
					} else {
						System.out.println("That user is busy.");
					}
				} else {
					System.out.println("Could not find that user. Proper syntax is talk {username} {message}.");
				}
			}
		} catch (ServiceException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void busy() {
		/*
		 * busy � The client updates its registration with the presence
		 * server, indicating it is not currently available. If the client is
		 * already in not available when this command is entered, nothing needs
		 * to be done, though it would be good to prompt the user and indicate
		 * they already are not available. A client that is busy should not
		 * receive any messages whether they be sent with the talk or the
		 * broadcast command.
		 */
		reg.setStatus(false);
		updateChordInfo();
	}

	private void available() {
		/*
		 * available � The client updates its registration information with
		 * the presence server, indicating it is now available. If the client is
		 * already available when this command is entered, nothing needs to be
		 * done, though it would be good to prompt the user and indicate they
		 * are already registered as available.
		 */
		reg.setStatus(true);
		updateChordInfo();
	}

	@Override
	public boolean register() {
		try {
			if (master) {
				System.out.println("Creating network on " + reg.getHost());
				localURL = new URL(
						URL.KNOWN_PROTOCOLS.get(URL.SOCKET_PROTOCOL) + "://" + reg.getHost() + ":" + 8181 + "/");
				chord = new ChordImpl();
				chord.create(localURL);
				chord.insert(new StringKey(this.reg.getUserName()), this.reg);
			}

			else {
				System.out.println("Joining network on " + reg.getHost());
				localURL = new URL(URL.KNOWN_PROTOCOLS.get(URL.SOCKET_PROTOCOL) + "://" + reg.getHost() + ":"
						+ (reg.getPort() + 1) + "/");
				masterURL = new URL(
						URL.KNOWN_PROTOCOLS.get(URL.SOCKET_PROTOCOL) + "://" + reg.getHost() + ":" + 8181 + "/");
				chord = new ChordImpl();
				chord.join(localURL, masterURL);

				if (chord.retrieve(new StringKey(this.reg.getUserName())).size() != 0) {
					System.out.println("Username already exists on the network, goodbye");
					System.exit(0);
				}
				chord.insert(new StringKey(this.reg.getUserName()), this.reg);
			}
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(-1);
		} catch (ServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}

	@Override
	public void unregister() {
		StringKey myKey = new StringKey(reg.getUserName());
		try {
			for (Serializable s : chord.retrieve(myKey)) {
				System.out.println("Removing registration: " + ((ChordInfo) s).toString());
				chord.remove(myKey, (ChordInfo) s);
			}
			chord.leave();
		} catch (ServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}

	public ChordInfo getReg() {
		return reg;
	}

	public void setReg(ChordInfo reg) {
		this.reg = reg;
	}

	public Chord getChord() {
		return chord;
	}

	public void setChord(Chord chord) {
		this.chord = chord;
	}

	public static void main(String args[]) {
		PropertiesLoader.loadPropertyFile();

		if (args.length < 2 || args.length > 3) {
			System.out.println("usage:\n\tjava ChordClient [-master] {user} {host}");
			return;
		}

		ChordInfo reg = new ChordInfo("", "", 0, true);
		ChordClient chat = new ChordClient(reg, false);

		if (args.length == 2) {
			reg.setUserName(args[0]);
			reg.setHost(args[1]);
			reg.setPort((int) (Math.random() * 60535) + 5000);
		}

		if (args.length == 3) {
			if (!args[0].equals("-master")) {
				System.out.println("usage:\n\tjava ChordClient [-master] {user} {host}");
				return;
			}
			chat.master = true;
			reg.setUserName(args[1]);
			reg.setHost(args[2]);
			reg.setPort((int) (Math.random() * 60535) + 5000);
		}

		try {
			reg.setHost(InetAddress.getLocalHost().getHostAddress());
		} catch (UnknownHostException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		// Finish Registration info
		reg.setStatus(true);

		// Start listener
		ChordConnectionListener listener = new ChordConnectionListener(reg);
		Thread t = new Thread(listener);
		t.start();

		chat.register(); // Starts the network, or joins the network

		Scanner input = new Scanner(System.in);
		String command = new String();

		while (!command.equals("quit")) {
			System.out.print(chat.reg.getUserName() + ": ");
			command = input.nextLine();
			if (command.equals("")) {
				command = "default";
			}
			StringTokenizer tk = new StringTokenizer(command);
			String commandPhrase = tk.nextToken();
			// Commands go here!
			switch (commandPhrase) {
			case "talk":
				chat.talk(tk);
				break;
			case "busy":
				chat.busy();
				break;
			case "available":
				chat.available();
				break;
			case "exit":
				/*
				 * exit � When this command is entered, the ChordClient will
				 * unregister itself with the ChordPresenceService and
				 * terminate.
				 */
				chat.unregister();
				command = "quit";
				break;
			default:
				break;
			}

		}
		input.close();
		listener.close();
		System.exit(0);
	}

}