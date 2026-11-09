package com.fooddel.repository;

import com.fooddel.model.Food;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface FoodRepository extends MongoRepository<Food, String> {

    /** Case-insensitive search on the food name. Caller must pass an escaped regex. */
    @Query("{ 'name': { $regex: ?0, $options: 'i' } }")
    List<Food> searchByName(String regex);
}
