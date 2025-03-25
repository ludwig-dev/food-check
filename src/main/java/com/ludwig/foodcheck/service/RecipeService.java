package com.ludwig.foodcheck.service;

import com.ludwig.foodcheck.dto.recipe.RecipeIngredientRequest;
import com.ludwig.foodcheck.dto.recipe.RecipeIngredientResponse;
import com.ludwig.foodcheck.dto.recipe.RecipeResponse;
import com.ludwig.foodcheck.model.Food;
import com.ludwig.foodcheck.model.Recipe;
import com.ludwig.foodcheck.model.RecipeIngredient;
import com.ludwig.foodcheck.model.User;
import com.ludwig.foodcheck.repository.FoodRepository;
import com.ludwig.foodcheck.repository.RecipeRepository;
import com.ludwig.foodcheck.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class RecipeService {
    private final RecipeRepository recipeRepository;
    private final UserRepository userRepository;
    private final FoodRepository foodRepository;

    public RecipeService(RecipeRepository recipeRepository, UserRepository userRepository, FoodRepository foodRepository) {
        this.recipeRepository = recipeRepository;
        this.userRepository = userRepository;
        this.foodRepository = foodRepository;
    }

    public Recipe createRecipe(String recipeName, List<RecipeIngredientRequest> ingredients, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User does not exist"));

        Recipe recipe = new Recipe();
        recipe.setName(recipeName);
        recipe.setUser(user);

        List<RecipeIngredient> recipeIngredients = new ArrayList<>();
        for (RecipeIngredientRequest ing : ingredients) {
            Optional<Food> food = foodRepository.findById(ing.getFoodId());
            if (food.isEmpty())
                throw new RuntimeException("Livsmedel with nummer " + ing.getFoodId() + " does not exist");

            RecipeIngredient ri = new RecipeIngredient();
            ri.setFood(food.get());
            ri.setAmountInGrams(ing.getAmountInGrams());
            ri.setRecipe(recipe);

            recipeIngredients.add(ri);
        }

        recipe.setIngredients(recipeIngredients);
        return recipeRepository.save(recipe);
    }

    public List<Recipe> getRecipeByUser(Long userId) {
        return recipeRepository.findByUserId(userId);
    }

    public Optional<Recipe> getRecipeById(Long recipeId, Long userId) {
        return recipeRepository.findByIdAndUserId(recipeId, userId);
    }

    public RecipeResponse convertToDTO(Recipe recipe) {
        List<RecipeIngredientResponse> ingredients = recipe.getIngredients().stream()
                .map(ri -> new RecipeIngredientResponse(
                        ri.getFood().getNummer(),
                        ri.getFood().getNamn(),
                        ri.getAmountInGrams()
                ))
                .toList();

        return new RecipeResponse(recipe.getId(), recipe.getName(), ingredients);
    }


}
