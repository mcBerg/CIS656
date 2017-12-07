package client;

import java.io.Serializable;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.util.Properties;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.Vector;

import chord.StringKey;
import compute.ChordPresenceService;
import de.uniba.wiai.lspi.chord.com.local.Registry;
import de.uniba.wiai.lspi.chord.data.URL;
import de.uniba.wiai.lspi.chord.service.Chord;
import de.uniba.wiai.lspi.chord.service.PropertiesLoader;
import de.uniba.wiai.lspi.chord.service.ServiceException;
import de.uniba.wiai.lspi.chord.service.impl.ChordImpl;

public class ChordClient implements Serializable, ChordPresenceService {

	private static final long serialVersionUID = 5144150709351448519L;
	private ChordInfo reg;
	private Registry registry;
	private ChordPresenceService comp;
	private Chord chord;

	public ChordClient(ChordInfo reg, boolean master) {
		URL localURL;
		URL masterURL;

		try {
			if (master) {
				System.out.println("Creating network on " + reg.getHost());
				String urlString = URL.KNOWN_PROTOCOLS.get(URL.SOCKET_PROTOCOL) + "://" + reg.getHost() + ":" + reg.getPort()+"/";
				System.out.println(urlString);
				localURL = new URL(urlString);
				chord = new ChordImpl();
				chord.create(localURL);
			}

			else {
				System.out.println("Joining network on " + reg.getHost());
				localURL = new URL(
						URL.KNOWN_PROTOCOLS.get(URL.SOCKET_PROTOCOL) + "://" + reg.getHost() + ":" + reg.getPort()+"/");
				masterURL = new URL(URL.KNOWN_PROTOCOLS.get(URL.SOCKET_PROTOCOL) + "://" + reg.getHost() + ":" + 8080+"/");
				this.chord = new ChordImpl();
				this.chord.join(localURL, masterURL);
			}
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(-1);
		} catch (ServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void main(String args[]) {
		
		PropertiesLoader.loadPropertyFile();
		for(Object p :  System.getProperties().keySet()) {
			System.out.println(p.toString() + " : " +System.getProperty(p.toString()));
		}
		System.out.println("Property " +System.getProperty("log4j.rootLogger"));
		System.setProperty("log4j.rootLogger", "FATAL, FILE");
		System.out.println("Property " +System.getProperty("log4j.rootLogger"));
		
		System.exit(-1);
		
		
		if (args.length < 2 || args.length > 3) {
			System.out.println("usage:\n\tjava ChordClient [-master] {user} {host}");
			return;
		}

		String userName = "";
		String host = "";
		boolean master = false;
		int myPort = 0;

		if (args.length == 2) {
			userName = args[0];
			host = args[1];
			myPort = (int) (Math.random() * 5535) + 60000;
		}

		if (args.length == 3) {
			if (!args[0].equals("-master")) {
				System.out.println("usage:\n\tjava ChordClient [-master] {user} {host}");
				return;
			}
			;
			master = true;
			userName = args[1];
			host = args[2];
			myPort = 8080;
		}
		
		try {
			host = InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		



		ChordInfo reg = new ChordInfo(userName, host, myPort, true);
		Scanner input = new Scanner(System.in);
		String command = new String();
		ChordClient chat = getChordClient(reg, master);
		ChordConnectionListener listener = new ChordConnectionListener(reg.getUserName(), chat.chord);
		Thread t = new Thread(listener);
		t.start();
		

		while (!command.equals("quit")) {
			System.out.print(userName + ": ");
			command = input.nextLine();
			StringTokenizer tk = new StringTokenizer(command);
			String commandPhrase = tk.nextToken();
			// Commands go here!
			switch (commandPhrase) {
			case "talk":
				talk(reg, chat, tk);
				break;
			case "busy":
				busy(reg, chat);
				break;
			case "available":
				available(reg, chat);
				break;
			case "exit":
				/*
				 * exit – When this command is entered, the ChordClient will
				 * unregister itself with the ChordPresenceService and
				 * terminate.
				 */
				try {
					chat.chord.leave();
				} catch (ServiceException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				command = "quit";
				break;
			default:
				break;
			}

		}
		input.close();
		System.exit(0);
	}

	private static void available(ChordInfo reg, ChordClient chat) {
		/*
		 * available – The client updates its registration information with the
		 * presence server, indicating it is now available. If the client is
		 * already available when this command is entered, nothing needs to be
		 * done, though it would be good to prompt the user and indicate they
		 * are already registered as available.
		 */

		chat.reg.setStatus(true);
	}

	private static void busy(ChordInfo reg, ChordClient chat) {
		/*
		 * busy – The client updates its registration with the presence server,
		 * indicating it is not currently available. If the client is already in
		 * not available when this command is entered, nothing needs to be done,
		 * though it would be good to prompt the user and indicate they already
		 * are not available. A client that is busy should not receive any
		 * messages whether they be sent with the talk or the broadcast command.
		 */

		chat.reg.setStatus(false);
	}

	private static void talk(ChordInfo reg, ChordClient chat, StringTokenizer tk) {
		try {
			if (tk.hasMoreTokens()) {
				StringKey keyVal = new StringKey(tk.nextToken());
					if (tk.hasMoreTokens()) {
						String data = "~" + reg.getUserName() + ":" +tk.nextToken("");
						chat.chord.insert(keyVal, data);
					} else {
					System.out.println(
					"You'll need to type a message to send. Proper syntax is talk {username} {message}.");
					}
				} else {
				System.out.println("Could not find that user. Proper syntax is talk {username} {message}.");
			}
		} catch (ServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static ChordClient getChordClient(ChordInfo reg, boolean master) {
		ChordClient chat = null;

		chat = new ChordClient(reg, master);

		System.out.println("Connected as " + reg.toString());

		return chat;
	}

	private static URL getUrl(String[] args) {
		URL url = null;
		try {
			url = new URL("https://" + args[1]);
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
		}
		return url;
	}

	private static void getSecurity() {
		if (System.getSecurityManager() == null) {
			System.setProperty("java.security.policy", "file:./policy");
			System.setSecurityManager(new SecurityManager());
		}
	}

	@Override
	public boolean register(ChordInfo reg) throws RemoteException {
		return comp.register(reg);
	}

	@Override
	public boolean updateChordInfo(ChordInfo reg) throws RemoteException {
		return comp.updateChordInfo(reg);
	}

	@Override
	public void unregister(String userName) throws RemoteException {
		comp.unregister(reg.getUserName());
	}

	@Override
	public ChordInfo lookup(String name) throws RemoteException {
		return comp.lookup(name);
	}

	@Override
	public Vector<ChordInfo> listRegisteredUsers() throws RemoteException {
		return comp.listRegisteredUsers();
	}

}