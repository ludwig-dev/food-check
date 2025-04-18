package com.ludwig.foodcheck.food;

import com.ludwig.foodcheck.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FoodService {
    private final FoodRepository foodRepository;

    public FoodService(FoodRepository foodRepository) {
        this.foodRepository = foodRepository;
    }

    public Food getFoodById(int id) {
        return foodRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Food not found with id: " + id));
    }

    public List<FoodDTO> searchByName(String query) {
        List<Food> foods = foodRepository.findByNamnContainingIgnoreCase(query);
        return foods.stream().map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private FoodDTO convertToDTO(Food food) {
        return new FoodDTO(food.getNummer(), food.getNamn());
    }
}
