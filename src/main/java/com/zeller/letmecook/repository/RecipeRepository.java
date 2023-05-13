package com.zeller.letmecook.repository;

import com.zeller.letmecook.model.Fridge;
import com.zeller.letmecook.model.Recipe;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecipeRepository extends MongoRepository<Recipe, String> {

	List<Recipe> getAll();
	List<Recipe> removeRecipeById(String id);
}
