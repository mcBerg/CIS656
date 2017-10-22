package client;

import java.io.Serializable;

import compute.Task;

public class Primes implements Task<String>, Serializable {

	private static final long serialVersionUID = 2L;
	private int max;
	private int min;
	
	public Primes(int min, int max) {
		this.max = max;
	}

	@Override
	public String execute() {
		return computePrimes(min, max);
	}

	public static String computePrimes(int min, int max) {
		return min+" "+max;
	}
	
}
