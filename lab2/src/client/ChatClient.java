package client;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Random;
import java.util.Scanner;
import java.util.Vector;

import compute.PresenceService;

public class ChatClient implements Serializable, PresenceService {

	private static final long serialVersionUID = 5144150709351448519L;
	private RegistrationInfo reg;
	private Registry registry;
	private PresenceService comp;

	public ChatClient(RegistrationInfo reg) throws RemoteException, NotBoundException {
		registry = LocateRegistry.getRegistry("127.0.0.1");
		comp = (PresenceService) registry.lookup("ChatServer");
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
		getSecurity();
		String userName = args[0];
		Random r = new Random();
		userName += r.nextInt(Integer.MAX_VALUE);
		URL url = getUrl(args);
		RegistrationInfo reg = new RegistrationInfo(userName, url.getHost(), url.getPort(), true);
		ChatClient chat = getChatClient(reg);
		
		Scanner input = new Scanner(System.in);
		String command = new String();
		while (!command.equals("quit")) {
			System.out.print(userName + ": ");
			command = input.nextLine();

			// Commands go here!

		}
		input.close();
		try {
			chat.unregister(reg.getUserName());
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	private static ChatClient getChatClient(RegistrationInfo reg) {
		ChatClient chat = null;
		try {
			chat = new ChatClient(reg);
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (NotBoundException e) {
			e.printStackTrace();
		}
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
	public boolean register(RegistrationInfo reg) throws RemoteException {
		return comp.register(reg);
	}

	@Override
	public boolean updateRegistrationInfo(RegistrationInfo reg) throws RemoteException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void unregister(String userName) throws RemoteException {
		comp.unregister(reg.getUserName());
	}

	@Override
	public RegistrationInfo lookup(String name) throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Vector<RegistrationInfo> listRegisteredUsers() throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

}