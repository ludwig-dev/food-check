package com.ludwig.foodcheck.service;

import com.ludwig.foodcheck.dto.FoodDTO;
import com.ludwig.foodcheck.model.Food;
import com.ludwig.foodcheck.repository.FoodRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@Service
public class FoodService {
    private final FoodRepository foodRepository;

    public FoodService(FoodRepository foodRepository) {
        this.foodRepository = foodRepository;
    }

    public Food getFoodById(int id) {
        return foodRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Food not found"));
    }

    public List<FoodDTO> searchByName(String query) {
        List<Food> foods = foodRepository.findByNamnContainingIgnoreCase(query);
        return foods.stream().map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public FoodDTO convertToDTO(Food food) {
        return new FoodDTO(food.getNummer(), food.getNamn());
    }
}
