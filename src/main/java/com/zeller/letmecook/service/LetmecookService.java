package com.zeller.letmecook.service;

import com.zeller.letmecook.repository.FridgeRepository;
import com.zeller.letmecook.repository.RecipeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

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


}
