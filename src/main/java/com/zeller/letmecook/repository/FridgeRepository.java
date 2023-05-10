package com.zeller.letmecook.repository;

import com.zeller.letmecook.model.Fridge;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FridgeRepository extends MongoRepository<Fridge, String> {
}
