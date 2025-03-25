package com.ludwig.foodcheck.repository;

import com.ludwig.foodcheck.model.Food;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FoodRepository extends JpaRepository<Food, Integer> {
    List<Food> findByNamnContainingIgnoreCase(String namn);
}

