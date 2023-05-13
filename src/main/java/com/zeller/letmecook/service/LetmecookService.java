package com.zeller.letmecook.service;

import com.zeller.letmecook.basic.RandomGenerator;
import com.zeller.letmecook.model.Fridge;
import com.zeller.letmecook.model.Grocery;
import com.zeller.letmecook.model.Ingredient;
import com.zeller.letmecook.model.Recipe;
import com.zeller.letmecook.repository.FridgeRepository;
import com.zeller.letmecook.repository.RecipeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class LetmecookService {

	private Logger logger;
	private final FridgeRepository fridgeRepository;
	private final RecipeRepository recipeRepository;

	public LetmecookService(FridgeRepository fridgeRepository, RecipeRepository recipeRepository) {
		this.logger = LoggerFactory.getLogger(LetmecookService.class);
		this.fridgeRepository = fridgeRepository;
		this.recipeRepository = recipeRepository;
	}

	/**
	 * ######################
	 * ### Recipe Logic  ####
	 * ######################
	 */
	public List<Recipe> getAllRecipes() {
		return recipeRepository.getAll();
	}

	public Optional<Recipe> createRecipe(Recipe recipe) {
		return Optional.of(recipeRepository.save(recipe));
	}

	public List<Recipe> removeRecipe(String id) {
		return recipeRepository.removeRecipeById(id);
	}

	public List<Recipe> createRecipes(List<Recipe> recipes) {
		return recipeRepository.saveAll(recipes);
	}

	/**
	 * ######################
	 * #### Query Logic  ####
	 * ######################
	 */

	/**
	 * Accepts any fridge id and determines a random recipe for the ingredients it contains.
	 * It is not said how many of the ingredients are used in the recipe.
	 * @param id
	 * @return A random recipe
	 */
	public Optional<Recipe> determineRandomRecipe(String id) {
		return fridgeRepository.getFridgeById(id)
				.map(fridge -> {
					List<Recipe> recipes = recipeRepository.getAll();
					return recipes.stream()
							.filter(recipe -> recipe.getIngredients()
									.stream()
									.anyMatch(ingredient -> fridge.getGroceries().stream()
											.anyMatch(grocery -> Objects.equals(grocery.getName(), ingredient.getName()))))
							.toList().get(RandomGenerator.generate(0, recipes.size()));
				});
	}

	/**
	 * Accepts any fridge id and determines a random and best matching recipe for the ingredients it contains.
	 * @param id
	 * @return A recipe that consumes the most groceries
	 */
	public Optional<Recipe> determineBestRecipe(String id) {
		return fridgeRepository.getFridgeById(id)
				.map(fridge -> {
					List<String> groceryNames = fridge.getGroceries().stream().map(Grocery::getName).toList();
					List<Recipe> recipes = recipeRepository.getAll();
					List<Recipe> bestRecipes = new ArrayList<>();
					long maxCounter = 0;
					for(Recipe recipe : recipes) {
						long counter = countMatchingElements(groceryNames, recipe.getIngredients());
						if(counter == maxCounter) {
							bestRecipes.add(recipe);
						} else if(counter > maxCounter) {
							maxCounter = counter;
							bestRecipes = new ArrayList<>();
							bestRecipes.add(recipe);
						}
					}
					return bestRecipes.get(RandomGenerator.generate(0, bestRecipes.size()));
				});
	}

	/**
	 * Counts the amount of ingredient names, that occure in the groceryNames List
	 * @param groceryNames
	 * @param ingredients
	 * @return The amount of matching elements in both lists
	 */
	private long countMatchingElements(List<String> groceryNames, List<Ingredient> ingredients) {
		return ingredients.stream()
				.map(Ingredient::getName)
				.filter(groceryNames::contains)
				.count();
	}

	/**
	 * ######################
	 * #### Fridge Logic ####
	 * ######################
	 */

	public Optional<Fridge> createFridge() {

		return Optional.of(fridgeRepository.save(new Fridge(Collections.emptyList(), 0f)));
	}
	public Optional<Fridge> getFridge(String id) {

		return fridgeRepository.getFridgeById(id);
	}

	public float determineWasteAmount(String id) {

		return fridgeRepository.getFridgeById(id).map(Fridge::getWasteAmount).orElse(0.0f);
	}

	public Optional<Fridge> addGroceriesToFridge(String id, List<Grocery> groceries) {
		return fridgeRepository.getFridgeById(id)
				.filter(fridge -> fridge.getGroceries().addAll(groceries))
				.map(fridgeRepository::save);
	}

	public Optional<Fridge> removeGroceryFromFridge(String id, String name) {
		return fridgeRepository.getFridgeById(id)
				.map(fridge -> {
					Grocery groceryToBeRemoved = fridge.getGroceries().stream()
							.filter(grocery -> Objects.equals(grocery.getName(), name))
							.findFirst().orElse(null);
					logger.info("LetmecookService#removeGroceryFromFridge#groceryToBeRemoved: " + groceryToBeRemoved);
					if (groceryToBeRemoved != null) {
						fridge.setWasteAmount(groceryToBeRemoved.getPrice());
						fridge.getGroceries().remove(groceryToBeRemoved);
						logger.info("LetmecookService#removeGroceryFromFridge#fridge: " + fridge);
						fridge = fridgeRepository.save(fridge);
					}
					return fridge;
				});
	}
}
