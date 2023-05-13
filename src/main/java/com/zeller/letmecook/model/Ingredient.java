package com.zeller.letmecook.model;

import java.util.Objects;

public class Ingredient {

	private String name;
	private float amount;
	private String unit;

	public Ingredient(String name, float amount, String unit) {
		this.name = name;
		this.amount = amount;
		this.unit = unit;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public float getAmount() {
		return amount;
	}

	public void setAmount(float amount) {
		this.amount = amount;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Ingredient that = (Ingredient) o;

		if (Float.compare(that.amount, amount) != 0) return false;
		if (!Objects.equals(name, that.name)) return false;
		return Objects.equals(unit, that.unit);
	}

	@Override
	public int hashCode() {
		int result = name != null ? name.hashCode() : 0;
		result = 31 * result + (amount != +0.0f ? Float.floatToIntBits(amount) : 0);
		result = 31 * result + (unit != null ? unit.hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {
		return "Ingredient{" +
				"name='" + name + '\'' +
				", amount=" + amount +
				", unit='" + unit + '\'' +
				'}';
	}
}
