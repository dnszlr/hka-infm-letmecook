package com.zeller.letmecook.service;

import com.zeller.letmecook.model.Fridge;
import com.zeller.letmecook.model.Grocery;
import com.zeller.letmecook.model.Recipe;
import com.zeller.letmecook.repository.FridgeRepository;
import com.zeller.letmecook.repository.RecipeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LetmecookService {

	private Logger logger;
	private FridgeRepository fridgeRepository;
	private RecipeRepository recipeRepository;

	public LetmecookService(FridgeRepository fridgeRepository, RecipeRepository recipeRepository) {
		this.logger = LoggerFactory.getLogger(LetmecookService.class);
		this.fridgeRepository = fridgeRepository;
		this.recipeRepository = recipeRepository;
	}

	public List<Recipe> getAllRecipes() {
		return null;
	}

	public List<Recipe> createRecipe(Recipe recipe) {
		return null;
	}

	public List<Recipe> removeRecipe(String id) {
		return null;
	}

	public List<Recipe> createRecipes(List<Recipe> recipes) {
		return null;
	}

	public Fridge getFridge() {
		return null;
	}

	public float determineWasteAmount() {
		return 0f;
	}

	public List<Grocery> addGroceriesToFridge(List<Grocery> groceries) {
		return null;
	}

	public List<Grocery> removeGroceryFromFridge(String id) {
		return null;
	}

	public Recipe determineRandomRecipe() {
		return null;
	}

	public Recipe determineBestRecipe() {
		return null;
	}
}
