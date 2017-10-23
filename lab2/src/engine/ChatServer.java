package engine;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Vector;

import client.RegistrationInfo;
import compute.PresenceService;

public class ChatServer implements PresenceService {

	private static final long serialVersionUID = 715588383496103659L;

	HashMap<String, RegistrationInfo> clients = new HashMap<String, RegistrationInfo>();

	public ChatServer() {
		super();
	}

	public static void main(String[] args) {
		Registry registry = null;

		if (System.getSecurityManager() == null) {
			System.setProperty("java.security.policy", "file:./policy");
			System.setSecurityManager(new SecurityManager());
		}

		try {
			System.out.println("Creating RmiRegistry");
			registry = java.rmi.registry.LocateRegistry.createRegistry(1099);
		} catch (RemoteException e1) {
			e1.printStackTrace();
		}

		try {
			String name = "ChatServer";
			PresenceService chat = new ChatServer();
			PresenceService stub = (PresenceService) UnicastRemoteObject.exportObject(chat, 0);
			registry = LocateRegistry.getRegistry();
			registry.rebind(name, stub);
			System.out.println("ChatServer bound");
			System.out.println(registry.lookup("ChatServer").toString());
		} catch (Exception e) {
			System.err.println("ChatServer exception:");
			e.printStackTrace();
		}
	}

	@Override
	public boolean register(RegistrationInfo reg) throws RemoteException {
		if (clients.containsKey(reg.getUserName())) {
			return false;
		}
		clients.put(reg.getUserName(), reg);
		displayUsers();
		return true;
	}

	private void displayUsers() {
		for (int i = 0; i < 20; i++) {
			System.out.println();
		}
		for (String client : clients.keySet()) {
			System.out.println(client);
		}
	}

	@Override
	public boolean updateRegistrationInfo(RegistrationInfo reg) throws RemoteException {
		if (clients.containsKey(reg.getUserName()) && clients.get(reg.getUserName()) != null) {
			if (clients.get(reg.getUserName()).getStatus() == reg.getStatus()) {
				System.out.println("Status already set weirdo: " + reg);
				return true;
			} else {
				clients.get(reg.getUserName()).setStatus(reg.getStatus());
				return true;
			}
		} else {
			System.out.println("Unregistered User trying to update: " + reg);
		}
		return false;
	}

	@Override
	public void unregister(String userName) throws RemoteException {
		clients.remove(userName);
		displayUsers();
	}

	@Override
	public RegistrationInfo lookup(String name) throws RemoteException {
		return clients.get(name);
	}

	@Override
	public Vector<RegistrationInfo> listRegisteredUsers() throws RemoteException {
		Vector<RegistrationInfo> v = new Vector<RegistrationInfo>();
		v.addAll(clients.values());
		return v;
	}
}
