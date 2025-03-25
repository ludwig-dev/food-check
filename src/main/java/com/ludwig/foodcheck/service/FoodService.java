package com.ludwig.foodcheck.service;

import com.ludwig.foodcheck.model.Food;
import com.ludwig.foodcheck.repository.FoodRepository;
import org.springframework.stereotype.Service;

@Service
public class FoodService {
    private final FoodRepository foodRepository;

    public FoodService(FoodRepository foodRepository) {
        this.foodRepository = foodRepository;
    }

    public Food getFoodById(int id){
        return foodRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Food not found"));
    }
}
