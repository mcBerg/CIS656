package engine;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import compute.Compute;
import compute.Task;

public class ComputeEngine implements Compute {

	public ComputeEngine() {
		super();
	}

	public <T> T executeTask(Task<T> t) {
		return t.execute();
	}

	public static void main(String[] args) {
		Registry registry = null;

		if (System.getSecurityManager() == null) {
			if (System.getProperty("java.security.policy") == null) {
				System.setProperty("java.security.policy", "file:./lab2/policy");
			}
			System.setSecurityManager(new SecurityManager());

		}

		try {
			System.out.println("Creating RmiRegistry");
			registry = java.rmi.registry.LocateRegistry.createRegistry(1099);
		} catch (RemoteException e1) {
			e1.printStackTrace();
		}

		try {
			String name = "Compute";
			Compute engine = new ComputeEngine();
			Compute stub = (Compute) UnicastRemoteObject.exportObject(engine, 0);
			registry = LocateRegistry.getRegistry();
			registry.rebind(name, stub);
			System.out.println("ComputeEngine bound");
			System.out.println(registry.lookup("Compute").toString());
		} catch (Exception e) {
			System.err.println("ComputeEngine exception:");
			e.printStackTrace();
		}
	}
}
