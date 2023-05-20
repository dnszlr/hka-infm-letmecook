package com.zeller.letmecook.controller;

import com.zeller.letmecook.model.*;
import com.zeller.letmecook.service.LetmecookService;
import com.zeller.letmecook.utility.PostRecipeAPITracker;
import io.micrometer.core.instrument.*;
import jakarta.servlet.http.HttpServletRequest;
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
import java.util.concurrent.atomic.AtomicLong;

@RestController
@RequestMapping()
public class LetmecookController {

	private final Logger logger;
	private final LetmecookService letmecookService;

	private Counter apiCounter;
	private Timer randomRecipeTimer;
	private AtomicLong msTimeGauge;
	private final PostRecipeAPITracker postRecipeAPITracker;

	private DistributionSummary importRecipesDistributionSummary;

	private LongTaskTimer mergeLongTaskTimer;

	public LetmecookController(LetmecookService letmecookService) {
		this.logger = LoggerFactory.getLogger(LetmecookController.class);
		this.letmecookService = letmecookService;
		this.postRecipeAPITracker = new PostRecipeAPITracker();
		this.micrometerConfiguration();
	}

	public void micrometerConfiguration() {
		this.apiCounter = Metrics.counter("counter.api");
		this.randomRecipeTimer = Metrics.timer("timer.random.recipe");
		Metrics.more().timeGauge("gauge.time.post.groceries", Collections.emptyList(), this.msTimeGauge = new AtomicLong(0), TimeUnit.MILLISECONDS, AtomicLong::get);
		FunctionTimer.builder("timer.function.post.recipe.latency", this.postRecipeAPITracker,
						PostRecipeAPITracker::getCounter,
						PostRecipeAPITracker::getTotalLatency,
						TimeUnit.MILLISECONDS)
				.description("post recipe api timer")
				.register(Metrics.globalRegistry);
		this.importRecipesDistributionSummary = DistributionSummary.builder("distribution.summary.import.recipe.request.size")
				.description("determines the size for the importRecipes endpoint")
				.baseUnit("bytes")
				.register(Metrics.globalRegistry);
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
		postRecipeAPITracker.incrementCounter();
		Instant start = Instant.now();
		logger.info("LetmecookController#postRecipe#call#" + recipe);
		ResponseEntity<Recipe> response = letmecookService.createRecipe(recipe)
				.map(createdRecipe -> new ResponseEntity<>(createdRecipe, HttpStatus.OK))
				.orElseGet(() -> new ResponseEntity<>(HttpStatus.BAD_REQUEST));
		Instant finish = Instant.now();
		postRecipeAPITracker.updateLatencies(Duration.between(start, finish).toMillis());
		return response;
	}

	@DeleteMapping("/recipes/{id}")
	public List<Recipe> deleteRecipe(@PathVariable String id) {
		apiCounter.increment();
		logger.info("LetmecookController#deleteRecipe#call#" + id);
		return letmecookService.removeRecipe(id);
	}

	@PostMapping("/recipes/import")
	public List<Recipe> importRecipes(HttpServletRequest request, @RequestBody List<Recipe> recipes) {
		importRecipesDistributionSummary.record(request.getContentLengthLong());
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
		Instant start = Instant.now();
		logger.info("LetmecookController#getRandomRecipe#call");
		ResponseEntity<RecipeResponse> response = letmecookService.determineRandomRecipe(id)
				.map(recipe -> new ResponseEntity<>(recipe, HttpStatus.OK))
				.orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
		Instant finish = Instant.now();
		randomRecipeTimer.record(Duration.between(start, finish));
		return response;
	}

	@GetMapping("/fridges/{id}/best")
	public ResponseEntity<RecipeResponse> getBestRecipe(@PathVariable String id) {
		apiCounter.increment();
		Timer.Sample sample = Timer.start(Metrics.globalRegistry);
		logger.info("LetmecookController#getBestRecipe#call#" + id);
		ResponseEntity<RecipeResponse> response = letmecookService.determineBestRecipe(id)
				.map(recipe -> new ResponseEntity<>(recipe, HttpStatus.OK))
				.orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
		sample.stop(Metrics.timer("timer.sample.best.recipe", "response", response.getStatusCode().toString()));
		return response;
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
		logger.info("LetmecookController#postGroceries#call#" + id + "#" + groceries);
		ResponseEntity<Fridge> response = letmecookService.addGroceriesToFridge(id, groceries)
				.map(fridge -> new ResponseEntity<>(fridge, HttpStatus.OK))
				.orElseGet(() -> new ResponseEntity<>(HttpStatus.BAD_REQUEST));
		Instant finish = Instant.now();
		msTimeGauge.set(Duration.between(start, finish).toMillis());
		return response;
	}

	@DeleteMapping("/fridges/{id}/groceries/{name}")
	public ResponseEntity<Fridge> deleteGroceryByName(@PathVariable String id, @PathVariable String name) {
		apiCounter.increment();
		logger.info("LetmecookController#deleteGroceryById#call#" + id + "#" + name);
		return letmecookService.removeGroceryFromFridge(id, name)
				.map(fridge -> new ResponseEntity<>(fridge, HttpStatus.OK))
				.orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}

	@GetMapping("/fridges/{id}/groceries/merge")
	public ResponseEntity<Fridge> mergeDuplicateGroceries(@PathVariable String id) {
		apiCounter.increment();
		logger.info("LetmecookController#mergeDuplicateGroceries#call#" + id);
		ResponseEntity<Fridge> response = letmecookService.findDuplicatedGroceriesAndMerge(id)
				.map(fridge -> new ResponseEntity<>(fridge, HttpStatus.OK))
				.orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
		return response;
	}
}
