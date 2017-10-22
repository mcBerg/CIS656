package client;

import java.math.BigDecimal;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;

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
		while (!response.equals("3")) {
			while (!response.equals("1") && !response.equals("2") && !response.equals("3")) {
				System.out.println("Menu Options:");
				System.out.println("1: Compute Pi");
				System.out.println("2: Compute Primes");
				System.out.println("3: Exit");
				response = input.next();
			}

			switch (response) {
			case "1":
				response = "";
				computePi(args, input);
				break;
			case "2":
				response = "";
				computePrimes(args, input);
				break;
			case "3":
				response = "";
				input.close();
				System.exit(0);
				break;
			default:
				break;
			}
		}
		input.close();
		System.exit(0);
	}

	private static void computePi(String[] args, Scanner input) {
		int digits = 0;

		while (digits <= 0) {
			System.out.println("input number of digits");
			if (input.hasNextInt()) {
				digits = input.nextInt();
			} else {
				System.out.println("The value you have input is not a valid integer");
				input.next();
			}
		}

		try {
			String name = "Compute";
			Registry registry = LocateRegistry.getRegistry(args[0]);
			Compute comp = (Compute) registry.lookup(name);
			// System.out.println(comp.toString());
			Pi task = new Pi(digits);
			BigDecimal pi = comp.executeTask(task);
			System.out.println(pi);
		} catch (Exception e) {
			input.close();
			System.err.println("ComputePi exception:");
			e.printStackTrace();
		}
	}

	private static void computePrimes(String[] args, Scanner input) {
		int min = 0;
		int max = 0;

		while (min <= 0 || max <=0) {
			System.out.println("input min and max");
			if (input.hasNextInt()) {
				min = input.nextInt();
			}
			if (input.hasNextInt()) {
				max = input.nextInt();
			} else {
				System.out.println("The values you have input are not valid integers");
				input.next();
			}
		}

		
		try {
			String name = "Compute";
			Registry registry = LocateRegistry.getRegistry(args[0]);
			Compute comp = (Compute) registry.lookup(name);
			// System.out.println(comp.toString());
			Primes task = new Primes(min, max);
			String primes = comp.executeTask(task);
			System.out.println(primes);
		} catch (Exception e) {
			input.close();
			System.err.println("ComputePi exception:");
			e.printStackTrace();
		}
	}
}