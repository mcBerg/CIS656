package client;

import java.io.Serializable;

import compute.Task;

public class Primes implements Task<String>, Serializable {

	private static final long serialVersionUID = 2L;
	private int max;
	
	public Primes(int max) {
		this.max = max;
	}

	@Override
	public String execute() {
		return computePrimes(max);
	}

	public static String computePrimes(int max) {
		return "2";
	}
	
}
