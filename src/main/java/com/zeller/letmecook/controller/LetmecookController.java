package com.zeller.letmecook.controller;

import com.zeller.letmecook.model.Fridge;
import com.zeller.letmecook.model.Grocery;
import com.zeller.letmecook.model.Recipe;
import com.zeller.letmecook.service.LetmecookService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/lmc")
public class LetmecookController {

	private Logger logger;
	private LetmecookService letmecookService;

	public LetmecookController(LetmecookService letmecookService) {
		this.logger = LoggerFactory.getLogger(LetmecookController.class);
		this.letmecookService = letmecookService;
	}

	/**
	 * ######################
	 * #### Recipe APIs #####
	 * ######################
	 */
	@GetMapping("/recipes")
	public List<Recipe> getRecipes() {
		logger.info("LetmecookController#getRecipes#call");
		return letmecookService.getAllRecipes();
	}

	@PostMapping("/recipes")
	public List<Recipe> postRecipe(@RequestBody Recipe recipe) {
		logger.info("LetmecookController#postRecipe#call#" + recipe);
		return letmecookService.createRecipe(recipe);
	}

	@DeleteMapping("/recipes/{id}")
	public List<Recipe> deleteRecipe(@PathVariable String id) {
		logger.info("LetmecookController#deleteRecipe#call#" + id);
		return letmecookService.removeRecipe(id);
	}

	@PostMapping("/recipes/import")
	public List<Recipe> importRecipes(@RequestBody List<Recipe> recipes) {
		logger.info("LetmecookController#importRecipes#call#" + recipes);
		return letmecookService.createRecipes(recipes);
	}

	/**
	 * ######################
	 * #### Fridge APIs  ####
	 * ######################
	 */
	@GetMapping("/fridge")
	public Fridge getFridge() {
		logger.info("LetmecookController#getFridge#call");
		return letmecookService.getFridge();
	}

	@GetMapping("/fridge/waste")
	public float getWasteAmount() {
		logger.info("LetmecookController#getWasteAmount#call");
		return letmecookService.determineWasteAmount();
	}

	@PostMapping("/groceries")
	public List<Grocery> postGroceries(@RequestBody List<Grocery> groceries) {
		logger.info("LetmecookController#postGroceries#call#" + groceries);
		return letmecookService.addGroceriesToFridge(groceries);
	}

	@DeleteMapping("/groceries/{id}")
	public List<Grocery> deleteGroceryById(@PathVariable String id) {
		logger.info("LetmecookController#deleteGroceryById#call#" + id);
		return letmecookService.removeGroceryFromFridge(id);
	}


	/**
	 * ######################
	 * ####  Query APIs  ####
	 * ######################
	 */
	@GetMapping("/recipes/random")
	public Recipe getRandomRecipe() {
		logger.info("LetmecookController#getRandomRecipe#call");
		return letmecookService.determineRandomRecipe();
	}

	@GetMapping("/recipes/best")
	public Recipe getBestRecipe() {
		logger.info("LetmecookController#getBestRecipe#call");
		return letmecookService.determineBestRecipe();
	}
}
