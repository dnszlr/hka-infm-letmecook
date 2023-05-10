package com.zeller.letmecook.model;

import java.time.LocalDate;
import java.util.Objects;

public class Food {

    private float amount;
    private Unit unit;
    private float price;
    private LocalDate purchase;
    private LocalDate expiration;

    public Food(float amount, Unit unit, float price, LocalDate purchase, LocalDate expiration) {
        this.amount = amount;
        this.unit = unit;
        this.price = price;
        this.purchase = purchase;
        this.expiration = expiration;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public Unit getUnit() {
        return unit;
    }

    public void setUnit(Unit unit) {
        this.unit = unit;
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
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Food food = (Food) o;

        if (Float.compare(food.amount, amount) != 0) return false;
        if (Float.compare(food.price, price) != 0) return false;
        if (unit != food.unit) return false;
        if (!Objects.equals(purchase, food.purchase)) return false;
        return Objects.equals(expiration, food.expiration);
    }

    @Override
    public int hashCode() {
        int result = (amount != +0.0f ? Float.floatToIntBits(amount) : 0);
        result = 31 * result + (unit != null ? unit.hashCode() : 0);
        result = 31 * result + (price != +0.0f ? Float.floatToIntBits(price) : 0);
        result = 31 * result + (purchase != null ? purchase.hashCode() : 0);
        result = 31 * result + (expiration != null ? expiration.hashCode() : 0);
        return result;
    }
}
