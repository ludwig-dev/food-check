package com.ludwig.foodcheck.controller;


import com.ludwig.foodcheck.model.Food;
import com.ludwig.foodcheck.service.FoodService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/food")
public class FoodController {
    private final FoodService foodService;

    public FoodController(FoodService foodService) {
        this.foodService = foodService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Food> getFoodById(@PathVariable int id) {
        return ResponseEntity.ok(foodService.getFoodById(id));
    }
}
