package com.zeller.letmecook.utility;

public class PostRecipeAPITracker {

	private int counter;
	// total latency in seconds
	private long totalLatency;

	private long latestLatency;

	public PostRecipeAPITracker() {
		this.counter = 0;
		this.totalLatency = 0;
		this.latestLatency = 0;
	}

	public void incrementCounter() {
		counter++;
	}

	public void updateLatencies(long latency) {
		setLatestLatency(latency);
		appendLatency(latency);
	}

	public void setLatestLatency(long latestLatency) {
		this.latestLatency = latestLatency;
	}

	public void appendLatency(long latency) {
		totalLatency += latency;
	}

	public int getCounter() {
		return counter;
	}

	public double getTotalLatency() {
		return totalLatency;
	}

	public long getLatestLatency() {
		return latestLatency;
	}
}
