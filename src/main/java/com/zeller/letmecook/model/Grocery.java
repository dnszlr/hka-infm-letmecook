package com.zeller.letmecook.model;

import java.time.LocalDate;
import java.util.Objects;

public class Grocery extends Ingredient{

	private float price;
	private LocalDate purchase;
	private LocalDate expiration;

	public Grocery(String name, float amount, Unit unit, float price, LocalDate purchase, LocalDate expiration) {
		super(name, amount, unit);
		this.price = price;
		this.purchase = purchase;
		this.expiration = expiration;
	}

	public float getPrice() {
		return price;
	}

	public void setPrice(float price) {
		this.price = price;
	}

	public LocalDate getPurchase() {
		return purchase;
	}

	public void setPurchase(LocalDate purchase) {
		this.purchase = purchase;
	}

	public LocalDate getExpiration() {
		return expiration;
	}

	public void setExpiration(LocalDate expiration) {
		this.expiration = expiration;
	}

	@Override
	public boolean equals(Object o) {
		if(this == o)
			return true;
		if(o == null || getClass() != o.getClass())
			return false;
		if(!super.equals(o))
			return false;

		Grocery grocery = (Grocery) o;

		if(Float.compare(grocery.price, price) != 0)
			return false;
		if(!Objects.equals(purchase, grocery.purchase))
			return false;
		return Objects.equals(expiration, grocery.expiration);
	}

	@Override
	public int hashCode() {
		int result = super.hashCode();
		result = 31 * result + (price != +0.0f ? Float.floatToIntBits(price) : 0);
		result = 31 * result + (purchase != null ? purchase.hashCode() : 0);
		result = 31 * result + (expiration != null ? expiration.hashCode() : 0);
		return result;
	}
}
