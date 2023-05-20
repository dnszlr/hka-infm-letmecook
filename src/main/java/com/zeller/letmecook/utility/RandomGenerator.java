package com.zeller.letmecook.utility;

import java.util.concurrent.ThreadLocalRandom;

public class RandomGenerator {

	public static int generate(int min, int max) {
		return ThreadLocalRandom.current().nextInt(min, max);
	}

	public static int generateBetweenZeroAnd(int value) {
		return ThreadLocalRandom.current().nextInt(0, value);
	}
}
