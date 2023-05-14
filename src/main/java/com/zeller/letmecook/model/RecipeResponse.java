package com.zeller.letmecook.model;

import java.util.List;

public class RecipeResponse {

	private final Recipe recipe;
	private List<Ingredient> availableIngredient;
	private List<Ingredient> shoppingList;

	public RecipeResponse(Recipe recipe, List<Ingredient> availableIngredient, List<Ingredient> shoppingList) {
		this.recipe = recipe;
		this.availableIngredient = availableIngredient;
		this.shoppingList = shoppingList;
	}

	public void setShoppingList(List<Ingredient> shoppingList) {
		this.shoppingList = shoppingList;
	}

	public void setAvailableIngredient(List<Ingredient> availableIngredient) {
		this.availableIngredient = availableIngredient;
	}
}