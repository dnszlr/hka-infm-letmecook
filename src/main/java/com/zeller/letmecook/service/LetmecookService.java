package com.zeller.letmecook.service;

import com.zeller.letmecook.utility.RandomGenerator;
import com.zeller.letmecook.model.*;
import com.zeller.letmecook.repository.FridgeRepository;
import com.zeller.letmecook.repository.RecipeRepository;
import com.zeller.letmecook.utility.SessionWasteAmountTracker;
import io.micrometer.core.instrument.Metrics;
import io.micrometer.core.instrument.MultiGauge;
import io.micrometer.core.instrument.MultiGauge.Row;
import io.micrometer.core.instrument.Tags;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class LetmecookService {

	private final Logger logger;
	private final FridgeRepository fridgeRepository;
	private final RecipeRepository recipeRepository;
	private final SessionWasteAmountTracker sessionWasteAmountTracker;
	private AtomicInteger gaugeValue;

	private MultiGauge multiGauge;

	public LetmecookService(FridgeRepository fridgeRepository, RecipeRepository recipeRepository) {
		this.logger = LoggerFactory.getLogger(LetmecookService.class);
		this.fridgeRepository = fridgeRepository;
		this.recipeRepository = recipeRepository;
		this.sessionWasteAmountTracker = new SessionWasteAmountTracker();
		this.micrometerConfiguration();
	}

	public void micrometerConfiguration() {
		Metrics.more().counter("counter.session.waste.amount", Collections.emptyList(), sessionWasteAmountTracker, SessionWasteAmountTracker::getSessionWasteAmount);
		this.gaugeValue = Metrics.gauge("gauge.random.recipe.available.ingredients", new AtomicInteger(0));
		this.multiGauge = MultiGauge.builder("gauge.multi.best.recipes")
				.description("the number of available Ingredients from the recipes that qualify for the best recipe")
				.baseUnit("available ingredients")
				.register(Metrics.globalRegistry);
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
	 * @return an optional containing a randomly selected random recipe, or an empty optional
	 * 	 *         if the fridge with the given ID cannot be found
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
					RecipeResponse response = createRecipeResponse(recipe, groceryNames);
					List<Ingredient> availableIngredients = response.getAvailableIngredients();
					gaugeValue.set(availableIngredients.size());
					// determine a random recipe from all randomRecipes
					// TODO Nullcheck
					return response;
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
					HashMap<Integer, List<RecipeResponse>> sortedRecipes = new HashMap<>();
					int maxKey = 0;
					for(Recipe recipe : getRecipes()) {
						RecipeResponse recipeResponse = createRecipeResponse(recipe, groceryNames);
						int currentKey = recipeResponse.getAvailableIngredients().size();
						maxKey = Math.max(currentKey, maxKey);
						// If the key exists in the hashmap the RecipeResponse is added immediately, if not
						// a new ArrayList is created and the value is added afterward
						sortedRecipes
								.computeIfAbsent(currentKey, key -> new ArrayList<>())
								.add(recipeResponse);
					}
					appendMultiMeterEntry(sortedRecipes);
					// determine a best recipe from all best recipes
					List<RecipeResponse> bestRecipes = sortedRecipes.get(maxKey);
					return bestRecipes.get(RandomGenerator.generateBetweenZeroAnd(bestRecipes.size()));
				});
	}

	public void appendMultiMeterEntry(HashMap<Integer, List<RecipeResponse>> sortedRecipes) {
		multiGauge.register(sortedRecipes.entrySet().stream()
				.map(result ->
						Row.of(Tags.of("Recipes", result.getValue().stream().map(RecipeResponse::getRecipeName)
								.collect(Collectors.joining(", "))), result.getKey()))
				.collect(Collectors.toList()));
	}

	/**
	 * Creates a RecipeResponse object based on the given Recipe and list of grocery names.
	 *
	 * @param recipe       The Recipe object for which to create the RecipeResponse.
	 * @param groceryNames The list of grocery names to check against the Recipe's ingredients.
	 * @return The created RecipeResponse object.
	 */
	private RecipeResponse createRecipeResponse(Recipe recipe, List<String> groceryNames) {
		List<Ingredient> missingIngredients = new ArrayList<>();
		List<Ingredient> matchingIngredients = new ArrayList<>();
		for(Ingredient ingredient : recipe.getIngredients()) {
			if(containsIngredientInGroceryList(groceryNames, ingredient)) {
				matchingIngredients.add(ingredient);
			} else {
				missingIngredients.add(ingredient);
			}
		}
		return new RecipeResponse(recipe, matchingIngredients, missingIngredients);
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
