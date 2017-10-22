package client;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;
import java.math.BigDecimal;
import compute.Compute;



public class ComputePi {
	
	
	
	public static void main(String args[]) {
		Scanner input;
		input = new Scanner(System.in);
		
		if (System.getSecurityManager() == null) {
			System.setProperty("java.security.policy", "file:./policy");
			System.setSecurityManager(new SecurityManager());
		}
		
		String response = new String();
		while (!response.equals("1") && !response.equals("2") && !response.equals("3")) {
			System.out.println("Menu Options:");
			System.out.println("1: Compute Pi");
			System.out.println("2: Compute Primes");
			System.out.println("3: Exit");
			response = input.next();
		}
		
		try {
			String name = "Compute";
			Registry registry = LocateRegistry.getRegistry(args[0]);
			Compute comp = (Compute) registry.lookup(name);
			//System.out.println(comp.toString());
			Pi task = new Pi(Integer.parseInt(args[1]));
			BigDecimal pi = comp.executeTask(task);
			System.out.println(pi);
		} catch (Exception e) {
			input.close();
			System.err.println("ComputePi exception:");
			e.printStackTrace();
		}
		
		input.close();
	}
	
}