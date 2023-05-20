package com.zeller.letmecook.model;

import java.util.List;

public class RecipeResponse {

	private final Recipe recipe;
	private List<Ingredient> availableIngredients;
	private List<Ingredient> shoppingList;

	public RecipeResponse(Recipe recipe, List<Ingredient> availableIngredients, List<Ingredient> shoppingList) {
		this.recipe = recipe;
		this.availableIngredients = availableIngredients;
		this.shoppingList = shoppingList;
	}

	public Recipe getRecipe() {
		return recipe;
	}

	public List<Ingredient> getAvailableIngredients() {
		return availableIngredients;
	}

	public void setAvailableIngredients(List<Ingredient> availableIngredients) {
		this.availableIngredients = availableIngredients;
	}

	public List<Ingredient> getShoppingList() {
		return shoppingList;
	}

	public void setShoppingList(List<Ingredient> shoppingList) {
		this.shoppingList = shoppingList;
	}

	public String getRecipeName() {
		return this.recipe.getName();
	}
}
