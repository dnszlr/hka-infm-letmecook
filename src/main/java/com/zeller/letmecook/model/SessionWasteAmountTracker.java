package com.zeller.letmecook.model;

public class SessionWasteAmountTracker {

	private float sessionWasteAmount;

	public SessionWasteAmountTracker() {
		sessionWasteAmount = 0.0f;
	}

	public float getSessionWasteAmount() {
		return sessionWasteAmount;
	}

	public void addSessionWasteAmount(float sessionWasteAmount) {
		this.sessionWasteAmount += sessionWasteAmount;
	}
}
