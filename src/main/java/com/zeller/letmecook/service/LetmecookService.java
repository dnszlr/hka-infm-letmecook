package com.zeller.letmecook.service;

import com.zeller.letmecook.basic.RandomGenerator;
import com.zeller.letmecook.model.*;
import com.zeller.letmecook.repository.FridgeRepository;
import com.zeller.letmecook.repository.RecipeRepository;
import io.micrometer.core.instrument.Metrics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class LetmecookService {

	private final Logger logger;
	private final FridgeRepository fridgeRepository;
	private final RecipeRepository recipeRepository;
	private final SessionWasteAmountTracker sessionWasteAmountTracker;
	private AtomicInteger gaugeValue;

	public LetmecookService(FridgeRepository fridgeRepository, RecipeRepository recipeRepository) {
		this.logger = LoggerFactory.getLogger(LetmecookService.class);
		this.fridgeRepository = fridgeRepository;
		this.recipeRepository = recipeRepository;
		this.sessionWasteAmountTracker = new SessionWasteAmountTracker();
		Metrics.more().counter("counter.session.waste.amount", Collections.emptyList(), sessionWasteAmountTracker, SessionWasteAmountTracker::getSessionWasteAmount);
		this.gaugeValue = Metrics.gauge("gauge.random.recipe.available.ingredients", new AtomicInteger(0));
	}

	/**
	 * ######################
	 * ### Recipe Logic  ####
	 * ######################
	 */

	public List<Recipe> getRecipes() {
		return recipeRepository.findAll();
	}

	public Optional<Recipe> createRecipe(Recipe recipe) {
		return Optional.of(recipeRepository.save(recipe));
	}

	public List<Recipe> removeRecipe(String id) {
		recipeRepository.deleteRecipeById(id);
		return getRecipes();
	}

	public List<Recipe> createRecipes(List<Recipe> recipes) {
		return recipeRepository.saveAll(recipes);
	}

	// TODO Delete, just for dev
	public void deleteAll() {
		recipeRepository.deleteAll();
	}

	/**
	 * ######################
	 * ####  Query Logic  ####
	 * ######################
	 */

	/**
	 * Accepts any fridge id and determines a random recipe for the ingredients it contains.
	 * It is not said how many of the ingredients are used in the recipe.
	 * @param id the ID of the fridge to check for available ingredients
	 * @return A random recipe
	 */
	public Optional<RecipeResponse> determineRandomRecipe(String id) {
		return fridgeRepository.findFridgeById(id)
				.map(fridge -> {
					List<String> groceryNames = getGroceryNames(fridge.getGroceries());
					List<Recipe> randomRecipes = new ArrayList<>();
					for(Recipe recipe : getRecipes()) {
						// checks if at least one ingredient of the recipe matches one of the groceries
						// stream is used here to make use of anyMatch, the stream stops if a matching ingredient is found
						if(recipe.getIngredients().stream().anyMatch(ingredient -> containsIngredientInGroceryList(groceryNames, ingredient))) {
							randomRecipes.add(recipe);
						}
					}
					Recipe recipe = randomRecipes.get(RandomGenerator.generateBetweenZeroAnd(randomRecipes.size()));
					// This seems redundant, but it makes more sense to determine the missingIngredients
					// and matchingIngredients only once for the random recipe.
					Pair<List<Ingredient>, List<Ingredient>> sortedIngredients = sortIngredients(groceryNames, recipe.getIngredients());
					List<Ingredient> availableIngredients = sortedIngredients.getFirst();
					gaugeValue.set(availableIngredients.size());
					// determine a random recipe from all randomRecipes
					return new RecipeResponse(recipe, availableIngredients, sortedIngredients.getSecond());
				});
	}

	/**
	 * Returns an optional containing a randomly selected best recipe based on the ingredients
	 * available in the fridge associated with the given ID. If the fridge with the given ID
	 * cannot be found, an empty optional is returned.
	 *
	 * @param id the ID of the fridge to check for available ingredients
	 * @return an optional containing a randomly selected best recipe, or an empty optional
	 *         if the fridge with the given ID cannot be found
	 */
	public Optional<RecipeResponse> determineBestRecipe(String id) {
		return fridgeRepository.findFridgeById(id)
				.map(fridge -> {
					List<String> groceryNames = getGroceryNames(fridge.getGroceries());
					List<RecipeResponse> bestRecipes = new ArrayList<>();
					long maxCounter = 0;
					for(Recipe recipe : getRecipes()) {
						Pair<List<Ingredient>, List<Ingredient>> sortedIngredients = sortIngredients(groceryNames, recipe.getIngredients());
						List<Ingredient> availableIngredients = sortedIngredients.getFirst();
						List<Ingredient> missingIngredients = sortedIngredients.getSecond();
						int counter = availableIngredients.size();
						if(counter == maxCounter) {
							// if the counter is equal, the recipe is added to the stored data
							bestRecipes.add(new RecipeResponse(recipe, availableIngredients, missingIngredients));
						} else if(counter > maxCounter) {
							// if the counter is greater than the current maxCounter, the stored data is reset
							maxCounter = counter;
							bestRecipes.clear();
							bestRecipes.add(new RecipeResponse(recipe, availableIngredients, missingIngredients));
						}
					}
					// determine a best recipe from all best recipes
					return bestRecipes.get(RandomGenerator.generateBetweenZeroAnd(bestRecipes.size()));
				});
	}

	/**
	 * Sorts the list of ingredients into matching and missing ingredients based on a list of grocery names.
	 * @param groceryNames the list of grocery names
	 * @param ingredients the list of ingredients
	 * @return A pair of lists containing the matching and missing ingredients
	 */
	private Pair<List<Ingredient>, List<Ingredient>> sortIngredients(List<String> groceryNames, List<Ingredient> ingredients) {
		List<Ingredient> missingIngredients = new ArrayList<>();
		List<Ingredient> matchingIngredients = new ArrayList<>();
		for(Ingredient ingredient : ingredients) {
			if(containsIngredientInGroceryList(groceryNames, ingredient)) {
				matchingIngredients.add(ingredient);
			} else {
				missingIngredients.add(ingredient);
			}
		}
		return Pair.of(matchingIngredients, missingIngredients);
	}

	/**
	 * Determines if the ingredient is contained at least once in a list of grocery names.
	 *
	 * @param groceryNames the list of grocery names
	 * @param ingredient the ingredients to check
	 * @return true if at least one ingredient is contained in the grocery names list, false otherwise
	 */
	public boolean containsIngredientInGroceryList(List<String> groceryNames, Ingredient ingredient) {
		return groceryNames.stream().anyMatch(groceryName -> groceryName.toLowerCase().contains(ingredient.getName().toLowerCase()));
	}

	/**
	 * Casts the grocery list to a list of Strings
	 * @param groceries The available ingredients
	 * @return The List of Strings of the names of passed groceries
	 */
	private List<String> getGroceryNames(List<Grocery> groceries) {
		return groceries.stream().map(Grocery::getName).toList();
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

		return fridgeRepository.findFridgeById(id);
	}

	public List<Fridge> getAllFridges() {
		return fridgeRepository.findAll();
	}

	public float determineWasteAmount(String id) {

		return fridgeRepository.findFridgeById(id).map(Fridge::getWasteAmount).orElse(0.0f);
	}

	public Optional<Fridge> addGroceriesToFridge(String id, List<Grocery> groceries) {
		return fridgeRepository.findFridgeById(id)
				.filter(fridge -> fridge.getGroceries().addAll(groceries))
				.map(fridgeRepository::save);
	}
	/**
	 * Removes the grocery with the given name from the fridge with the given id.
	 * The method also increases the wasteAmount of the fridge by the price of the removed grocery.
	 *
	 * @param id   The id of the fridge to remove the grocery from.
	 * @param groceryName The name of the grocery to be removed.
	 * @return An optional containing the updated fridge, or an empty optional if the fridge was not found or if the grocery was not found in the fridge.
	 */
	public Optional<Fridge> removeGroceryFromFridge(String id, String groceryName) {
		return fridgeRepository.findFridgeById(id)
				.map(fridge -> {
					Grocery groceryToBeRemoved = fridge.getGroceries().stream()
							.filter(grocery -> Objects.equals(grocery.getName(), groceryName))
							.findFirst().orElse(null);
					logger.info("LetmecookService#removeGroceryFromFridge#groceryToBeRemoved: " + groceryToBeRemoved);
					if (groceryToBeRemoved != null) {
						float wasteAmount = fridge.getWasteAmount() + groceryToBeRemoved.getPrice();
						fridge.setWasteAmount(wasteAmount);
						fridge.getGroceries().remove(groceryToBeRemoved);
						sessionWasteAmountTracker.addSessionWasteAmount(wasteAmount);
						logger.info("LetmecookService#removeGroceryFromFridge#fridge#wasteAmount: " + fridge.getWasteAmount());
						fridge = fridgeRepository.save(fridge);
					}
					return fridge;
				});
	}
}
