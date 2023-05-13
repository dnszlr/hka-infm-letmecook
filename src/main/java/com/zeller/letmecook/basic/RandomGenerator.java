package com.zeller.letmecook.basic;

import java.util.concurrent.ThreadLocalRandom;

public class RandomGenerator {

	public static int generate(int min, int max) {
		return ThreadLocalRandom.current().nextInt(min, max);
	}
}
