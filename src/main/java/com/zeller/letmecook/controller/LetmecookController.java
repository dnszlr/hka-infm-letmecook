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
		return null;
	}

	@PostMapping("/recipes")
	public List<Recipe> addRecipe() {
		return null;
	}

	@DeleteMapping("/recipes")
	public List<Recipe> deleteRecipe() {
		return null;
	}

	@PostMapping("/recipes/import")
	public List<Recipe> importRecipes(@RequestBody List<Recipe> recipes) {
		return null;
	}

	/**
	 * ######################
	 * #### Fridge APIs  ####
	 * ######################
	 */
	@GetMapping("/fridge")
	public Fridge getFridge() {
		return null;
	}

	@PostMapping("/groceries")
	public List<Grocery> addGrocery(@RequestBody Grocery grocery) {
		return null;
	}

	@DeleteMapping("/groceries/{id}")
	public List<Grocery> throwAwayGrocery(@PathVariable String id) {
		return null;
	}


	/**
	 * ######################
	 * ####  Query APIs  ####
	 * ######################
	 */
	@GetMapping("/recipes/random")
	public Recipe getRandomRecipe() {
		return null;
	}

	@GetMapping("/recipes/best")
	public Recipe getBestRecipe() {
		return null;
	}
}
