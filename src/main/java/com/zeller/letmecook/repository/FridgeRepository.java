package com.zeller.letmecook.repository;

import com.zeller.letmecook.model.Fridge;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FridgeRepository extends MongoRepository<Fridge, String> {
	Optional<Fridge> findFridgeById(String id);
}
