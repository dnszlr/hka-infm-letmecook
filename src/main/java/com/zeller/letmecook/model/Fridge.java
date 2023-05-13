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
    private List<Grocery> content;
    private float wasteAmount;

    public Fridge(List<Grocery> content, float wasteAmount) {
        this.content = content;
        this.wasteAmount = wasteAmount;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<Grocery> getContent() {
        return content;
    }

    public void setContent(List<Grocery> content) {
        this.content = content;
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
        return Objects.equals(content, fridge.content);
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + (content != null ? content.hashCode() : 0);
        result = 31 * result + (wasteAmount != +0.0f ? Float.floatToIntBits(wasteAmount) : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Fridge{" +
                "id='" + id + '\'' +
                ", content=" + content +
                ", throwAwayAmount=" + wasteAmount +
                '}';
    }
}
