package com.zeller.letmecook.controller;

import com.zeller.letmecook.model.*;
import com.zeller.letmecook.service.LetmecookService;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Metrics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

@RestController
@RequestMapping()
public class LetmecookController {

	private final Logger logger;
	private final LetmecookService letmecookService;

	private final Counter apiCounter;
	private AtomicLong msTimeGauge;

	public LetmecookController(LetmecookService letmecookService) {
		this.logger = LoggerFactory.getLogger(LetmecookController.class);
		this.letmecookService = letmecookService;
		this.apiCounter = Metrics.counter("counter.api");
		msTimeGauge = new AtomicLong(0);
		Metrics.more().timeGauge("gauge.time.post.groceries", Collections.emptyList(), msTimeGauge, TimeUnit.MILLISECONDS, AtomicLong::get);
	}

	/**
	 * ######################
	 * #### Recipe APIs #####
	 * ######################
	 */

	@GetMapping("/recipes")
	public List<Recipe> getRecipes() {
		apiCounter.increment();
		logger.info("LetmecookController#getRecipes#call");
		return letmecookService.getRecipes();
	}

	@PostMapping("/recipes")
	public ResponseEntity<Recipe> postRecipe(@RequestBody Recipe recipe) {
		apiCounter.increment();
		logger.info("LetmecookController#postRecipe#call#" + recipe);
		return letmecookService.createRecipe(recipe)
				.map(createdRecipe -> new ResponseEntity<>(createdRecipe, HttpStatus.OK))
				.orElseGet(() -> new ResponseEntity<>(HttpStatus.BAD_REQUEST));
	}

	@DeleteMapping("/recipes/{id}")
	public List<Recipe> deleteRecipe(@PathVariable String id) {
		apiCounter.increment();
		logger.info("LetmecookController#deleteRecipe#call#" + id);
		return letmecookService.removeRecipe(id);
	}

	@PostMapping("/recipes/import")
	public List<Recipe> importRecipes(@RequestBody List<Recipe> recipes) {
		apiCounter.increment();
		logger.info("LetmecookController#importRecipes#call#" + recipes);
		return letmecookService.createRecipes(recipes);
	}

	/**
	 * Only for development TODO delete
	 */
	@GetMapping("/recipes/deleteall")
	public void deleteAllForDev() {
		apiCounter.increment();
		logger.info("LetmecookController#deleteAllForDev");
		letmecookService.deleteAll();
	}

	/**
	 * ######################
	 * ####  Query APIs  ####
	 * ######################
	 */

	@GetMapping("/fridges/{id}/random")
	public ResponseEntity<RecipeResponse> getRandomRecipe(@PathVariable String id) {
		apiCounter.increment();
		logger.info("LetmecookController#getRandomRecipe#call");
		return letmecookService.determineRandomRecipe(id)
				.map(recipe -> new ResponseEntity<>(recipe, HttpStatus.OK))
				.orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}

	@GetMapping("/fridges/{id}/best")
	public ResponseEntity<RecipeResponse> getBestRecipe(@PathVariable String id) {
		apiCounter.increment();
		logger.info("LetmecookController#getBestRecipe#call#" + id);
		return letmecookService.determineBestRecipe(id)
				.map(recipe -> new ResponseEntity<>(recipe, HttpStatus.OK))
				.orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}

	/**
	 * ######################
	 * #### Fridge APIs  ####
	 * ######################
	 */

	@GetMapping("/fridges")
	public List<Fridge> getAllFridges() {
		apiCounter.increment();
		logger.info("LetmecookController#getAllFridges#call");
		return letmecookService.getAllFridges();
	}

	@GetMapping("/fridges/{id}")
	public ResponseEntity<Fridge> getFridge(@PathVariable String id) {
		apiCounter.increment();
		logger.info("LetmecookController#getFridge#call#" + id);
		return letmecookService.getFridge(id)
				.map(fridge -> new ResponseEntity<>(fridge, HttpStatus.OK))
				.orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}

	@PostMapping("/fridges")
	public ResponseEntity<Fridge> postFridge() {
		apiCounter.increment();
		logger.info("LetmecookController#postFridge#call");
		return letmecookService.createFridge()
				.map(fridge -> new ResponseEntity<>(fridge, HttpStatus.OK))
				.orElseGet(() -> new ResponseEntity<>(HttpStatus.BAD_REQUEST));
	}

	@GetMapping("/fridges/{id}/waste")
	public float getWasteAmount(@PathVariable String id) {
		apiCounter.increment();
		logger.info("LetmecookController#getWasteAmount#call#" + id);
		return letmecookService.determineWasteAmount(id);
	}

	@PostMapping("/fridges/{id}/groceries")
	public ResponseEntity<Fridge> postGroceries(@PathVariable String id, @RequestBody List<Grocery> groceries) {
		apiCounter.increment();
		Instant start = Instant.now();
		ResponseEntity<Fridge> result = letmecookService.addGroceriesToFridge(id, groceries)
				.map(fridge -> new ResponseEntity<>(fridge, HttpStatus.OK))
				.orElseGet(() -> new ResponseEntity<>(HttpStatus.BAD_REQUEST));
		Instant finish = Instant.now();
		msTimeGauge.set(Duration.between(start, finish).toMillis());
		logger.info("LetmecookController#postGroceries#call#" + id + "#" + groceries);
		return result;
	}

	@DeleteMapping("/fridges/{id}/groceries/{name}")
	public ResponseEntity<Fridge> deleteGroceryByName(@PathVariable String id, @PathVariable String name) {
		apiCounter.increment();
		logger.info("LetmecookController#deleteGroceryById#call#" + id + "#" + name);
		return letmecookService.removeGroceryFromFridge(id, name)
				.map(fridge -> new ResponseEntity<>(fridge, HttpStatus.OK))
				.orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}
}
