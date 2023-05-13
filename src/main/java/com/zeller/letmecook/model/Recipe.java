package com.zeller.letmecook.model;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.FieldType;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.util.List;
import java.util.Objects;

@Document
public class Recipe {
    @MongoId(value = FieldType.OBJECT_ID)
    private String id;
    @Indexed(unique = true)
    private String name;
    private List<Ingredient> ingredients;
    private String description;

    public Recipe(String name, List<Ingredient> ingredients, String description) {
        this.name = name;
        this.ingredients = ingredients;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<Ingredient> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<Ingredient> ingredients) {
        this.ingredients = ingredients;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if(this == o)
            return true;
        if(o == null || getClass() != o.getClass())
            return false;

        Recipe recipe = (Recipe) o;

        if(!id.equals(recipe.id))
            return false;
        if(!Objects.equals(name, recipe.name))
            return false;
        if(!Objects.equals(ingredients, recipe.ingredients))
            return false;
        return Objects.equals(description, recipe.description);
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (ingredients != null ? ingredients.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Recipe{" +
                "name='" + name + '\'' +
                ", ingredients=" + ingredients +
                ", description='" + description + '\'' +
                '}';
    }
}
