package com.zeller.letmecook.controller;

import com.zeller.letmecook.model.Fridge;
import com.zeller.letmecook.model.Grocery;
import com.zeller.letmecook.model.Recipe;
import com.zeller.letmecook.service.LetmecookService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
	public ResponseEntity<Recipe> postRecipe(@RequestBody Recipe recipe) {
		logger.info("LetmecookController#postRecipe#call#" + recipe);
		return letmecookService.createRecipe(recipe)
				.map(createdRecipe -> new ResponseEntity<>(createdRecipe, HttpStatus.OK))
				.orElseGet(() -> new ResponseEntity<>(HttpStatus.CONFLICT));
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
	@GetMapping("/fridges/{id}")
	public ResponseEntity<Fridge> getFridge(@PathVariable String id) {
		logger.info("LetmecookController#getFridge#call#" + id);
		return letmecookService.getFridge(id)
				.map(fridge -> new ResponseEntity<>(fridge, HttpStatus.OK))
				.orElseGet(() -> new ResponseEntity<>(HttpStatus.CONFLICT));
	}

	@PostMapping("/fridges")
	public ResponseEntity<Fridge> createFridge() {
		return letmecookService.createFridge()
				.map(fridge -> new ResponseEntity<>(fridge, HttpStatus.OK))
				.orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}

	@GetMapping("/fridges/{id}/waste")
	public float getWasteAmount(@PathVariable String id) {
		logger.info("LetmecookController#getWasteAmount#call#" + id);
		return letmecookService.determineWasteAmount(id);
	}

	@PostMapping("/fridges/{id}/groceries")
	public ResponseEntity<Fridge> postGroceries(@PathVariable String id, @RequestBody List<Grocery> groceries) {
		logger.info("LetmecookController#postGroceries#call#" + id + "#" + groceries);
		return letmecookService.addGroceriesToFridge(id, groceries)
				.map(fridge -> new ResponseEntity<>(fridge, HttpStatus.OK))
				.orElseGet(() -> new ResponseEntity<>(HttpStatus.CONFLICT));
	}

	@DeleteMapping("/fridges/{id}/groceries/{name}")
	public ResponseEntity<Fridge> deleteGroceryByName(@PathVariable String id, @PathVariable String name) {
		logger.info("LetmecookController#deleteGroceryById#call#" + id + "#" + name);
		return letmecookService.removeGroceryFromFridge(id, name).map(fridge -> new ResponseEntity<>(fridge, HttpStatus.OK)).orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}

	/**
	 * ######################
	 * ####  Query APIs  ####
	 * ######################
	 */
	@GetMapping("/fridges/{id}/random")
	public ResponseEntity<Recipe> getRandomRecipe(@PathVariable String id) {
		logger.info("LetmecookController#getRandomRecipe#call");
		return letmecookService.determineRandomRecipe(id)
				.map(recipe -> new ResponseEntity<>(recipe, HttpStatus.OK))
				.orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}

	@GetMapping("/fridges/{id}/best")
	public ResponseEntity<Recipe> getBestRecipe(@PathVariable String id) {
		logger.info("LetmecookController#getBestRecipe#call#" + id);
		return letmecookService.determineBestRecipe(id)
				.map(recipe -> new ResponseEntity<>(recipe, HttpStatus.OK))
				.orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}
}
