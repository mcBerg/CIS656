package client;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.rmi.ConnectException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.Vector;

import compute.ChordPresenceService;

public class ChordClient implements Serializable, ChordPresenceService {

	
	
	private static final long serialVersionUID = 5144150709351448519L;
	private ChordInfo reg;
	private Registry registry;
	private ChordPresenceService comp;
	private Socket connection;

	public ChordClient(ChordInfo reg) throws NotBoundException, IOException {
		registry = LocateRegistry.getRegistry("127.0.0.1");
		comp = (ChordPresenceService) registry.lookup("ChatServer");
		this.reg = reg;
		if (reg == null) {
			System.out.println("Empty Registration Info");
			System.exit(1);
		}
		if (!register(reg)) {
			System.out.println("Could Not Register, Goodbye");
			System.exit(0);
		}

	}

	public static void main(String args[]) {
		
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
			};
			userName = args[1];
			host = args[2];
			myPort = 8080;
		}
		
		getSecurity();
		
		
		ChordInfo reg = new ChordInfo(userName, url.getHost(), url.getPort(), true);
		Scanner input = new Scanner(System.in);
		String command = new String();
		ChordConnectionListener listener = new ChordConnectionListener(reg);
		Thread t = new Thread(listener);
		t.start();
		ChordClient chat = getChordClient(reg);
		while (!command.equals("quit")) {
			System.out.print(userName + ": ");
			command = input.nextLine();
			StringTokenizer tk = new StringTokenizer(command);
			String commandPhrase = tk.nextToken();
			// Commands go here!
			switch (commandPhrase) {
			case "friends":
				friends(chat);
				break;
			case "talk":
				talk(reg, chat, tk);
				break;
			case "broadcast":
				broadcast(reg, chat, tk);
				break;
			case "busy":
				busy(reg, chat);
				break;
			case "available":
				available(reg, chat);
				break;
			case "exit":
				/*
				 * exit  When this command is entered, the ChordClient will
				 * unregister itself with the ChordPresenceService and terminate.
				 */
				command = "quit";
				break;
			default:
				break;
			}

		}
		input.close();
		try {
			chat.unregister(reg.getUserName());
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		System.exit(0);
	}

	private static void friends(ChordClient chat) {
		/*
		 * friends  When this command is entered, the client determines (via
		 * the Presence Service) which users are registered with the presence
		 * server and prints out the users names and whether they are available
		 * or not available (via the status field in the registration
		 * information).
		 */
		try {
			Vector<ChordInfo> users = chat.listRegisteredUsers();
			for (ChordInfo info : users) {
				System.out.println(
						info.getUserName() + " " + (info.getStatus() == true ? " is Available " : " is Busy "));
			}
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void available(ChordInfo reg, ChordClient chat) {
		/*
		 * available  The client updates its registration information with the
		 * presence server, indicating it is now available. If the client is
		 * already available when this command is entered, nothing needs to be
		 * done, though it would be good to prompt the user and indicate they
		 * are already registered as available.
		 */

		chat.reg.setStatus(true);
		try {
			chat.updateChordInfo(reg);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void busy(ChordInfo reg, ChordClient chat) {
		/*
		 * busy  The client updates its registration with the presence server,
		 * indicating it is not currently available. If the client is already in
		 * not available when this command is entered, nothing needs to be done,
		 * though it would be good to prompt the user and indicate they already
		 * are not available. A client that is busy should not receive any
		 * messages whether they be sent with the talk or the broadcast command.
		 */

		chat.reg.setStatus(false);
		try {
			chat.updateChordInfo(reg);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void broadcast(ChordInfo reg, ChordClient chat, StringTokenizer tk) {
		/*
		 * broadcast {message}  The client broadcasts the message to every user
		 * that is currently registered and available. Clients should NOT
		 * broadcast the message to themselves.
		 */

		try {
			Vector<ChordInfo> users = chat.listRegisteredUsers();

			if (tk.hasMoreTokens()) {
				String message = tk.nextToken("");
				for (ChordInfo target : users) {
					if (target.getUserName().equals(reg.getUserName())) {
						continue;
					}
					if (target.getStatus()) {
						Socket send = new Socket(target.getHost(), target.getPort());
						OutputStreamWriter writer = new OutputStreamWriter(send.getOutputStream());
						writer.write(reg.getUserName() + ":" + message);
						writer.flush();
						send.close();
					}
				}
			} else {
				System.out.println("No message found. Proper syntax is broadcast {message}.");
			}
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void talk(ChordInfo reg, ChordClient chat, StringTokenizer tk) {
		/*
		 * talk {username} {message} - When this command is entered, the client
		 * 1) first checks to see if the user is present and available (via the
		 * ChordPresenceService). 2) If the user is registered and available, a
		 * connection to the target client is established, based on their
		 * registration info, and they are sent the given message. Note that
		 * when a client receives a message, it will simply print it out to the
		 * console, and re- prompt the user to enter his/her next command. (i.e.
		 * you will need multiple threads of execution here within your client
		 * process.)
		 */

		try {
			if (tk.hasMoreTokens()) {
				ChordInfo target = chat.lookup(tk.nextToken());
				if (target != null) {
					if (target.getStatus()) {
						if (tk.hasMoreTokens()) {
							Socket send = new Socket(target.getHost(), target.getPort());
							OutputStreamWriter writer = new OutputStreamWriter(send.getOutputStream());
							String x = "";
							x += tk.nextToken("");
							writer.write("~"+reg.getUserName() + ":" + x);
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
		} catch (ConnectException e) {
			System.out.println("The server has disconnected. Will exit.");
			System.exit(1);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static ChordClient getChordClient(ChordInfo reg) {
		ChordClient chat = null;
		try {
			chat = new ChordClient(reg);
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (NotBoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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