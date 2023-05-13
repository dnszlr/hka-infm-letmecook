package com.zeller.letmecook.model;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.FieldType;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.util.List;
import java.util.Objects;

@Document
public class Fridge {
    @MongoId(value = FieldType.OBJECT_ID)
    private String id;
    private List<Grocery> groceries;
    private float wasteAmount;

    public Fridge(List<Grocery> groceries, float wasteAmount) {
        this.groceries = groceries;
        this.wasteAmount = wasteAmount;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<Grocery> getGroceries() {
        return groceries;
    }

    public void setGroceries(List<Grocery> groceries) {
        this.groceries = groceries;
    }

    public float getWasteAmount() {
        return wasteAmount;
    }

    public void setWasteAmount(float wasteAmount) {
        this.wasteAmount = wasteAmount;
    }

    @Override
    public boolean equals(Object o) {
        if(this == o)
            return true;
        if(o == null || getClass() != o.getClass())
            return false;

        Fridge fridge = (Fridge) o;

        if(Float.compare(fridge.wasteAmount, wasteAmount) != 0)
            return false;
        if(!id.equals(fridge.id))
            return false;
        return Objects.equals(groceries, fridge.groceries);
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + (groceries != null ? groceries.hashCode() : 0);
        result = 31 * result + (wasteAmount != +0.0f ? Float.floatToIntBits(wasteAmount) : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Fridge{" +
                "id='" + id + '\'' +
                ", content=" + groceries +
                ", throwAwayAmount=" + wasteAmount +
                '}';
    }
}
