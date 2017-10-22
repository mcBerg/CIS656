package client;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import compute.Task;

public class Primes implements Task<String>, Serializable {

	private static final long serialVersionUID = 2L;
	private int max;
	private int min;

	public Primes(int min, int max) {
		this.min = min;
		this.max = max;
	}

	@Override
	public String execute() {
		return computePrimes(min, max);
	}

	public static String computePrimes(int min, int max) {
		ArrayList<Integer> primes = new ArrayList<Integer>();
		primes.add(2);
		primes.add(3);
		primes.add(5);
		primes.add(7);
		primes.add(11);
		primes.add(13);
		primes.add(17);
		primes.add(19);
		// starter values for time saving.

		int next = 20;
		boolean prime;
		while (next <= max) {
			prime = true;
			for (Integer i : primes) {
				if (i > Math.sqrt(next)) {
					continue;
				}
				if (next % i == 0) {
					prime = false;
				}
			}
			if (prime == true) {
				primes.add(next);
			}
		next++;
		}
		String result = "";
		for (Integer i : primes) {
			if (i >= min && i <= max) {
				result += i+" ";
			}
		}
		return result;
	}
}
