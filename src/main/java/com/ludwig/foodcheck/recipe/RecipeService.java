package com.ludwig.foodcheck.recipe;

import com.ludwig.foodcheck.exception.ResourceNotFoundException;
import com.ludwig.foodcheck.recipe.dto.RecipeIngredientRequest;
import com.ludwig.foodcheck.recipe.dto.RecipeIngredientResponse;
import com.ludwig.foodcheck.recipe.dto.RecipeResponse;
import com.ludwig.foodcheck.food.Food;
import com.ludwig.foodcheck.users.User;
import com.ludwig.foodcheck.food.FoodRepository;
import com.ludwig.foodcheck.users.UserRepository;
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

    public Recipe createRecipe(String recipeName, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User with ID: " + userId + "not found"));

        Recipe recipe = new Recipe();
        recipe.setName(recipeName);
        recipe.setUser(user);

        List<RecipeIngredient> recipeIngredients = new ArrayList<>();
        recipe.setIngredients(recipeIngredients);
        return recipeRepository.save(recipe);
    }

    public List<Recipe> getRecipeByUser(Long userId) {
        return recipeRepository.findByUserId(userId);
    }

    public RecipeResponse getRecipeById(Long recipeId, Long userId) {
        Recipe recipe = recipeRepository.findByIdAndUserId(recipeId, userId).orElseThrow(() -> new ResourceNotFoundException("Recipe with ID: " + recipeId + " not found"));
        return convertToDTO(recipe);
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

    public Recipe addIngredient(Long recipeId, RecipeIngredientRequest request, Long userId) {
        Recipe recipe = recipeRepository.findByIdAndUserId(recipeId, userId)
                .orElseThrow(() -> new RuntimeException("Recipe not found"));

        Food food = foodRepository.findById(request.getFoodId())
                .orElseThrow(() -> new RuntimeException("Food not found"));

        boolean alreadyExists = recipe.getIngredients().stream()
                .anyMatch(i -> i.getFood().getNummer() == request.getFoodId());

        if (!alreadyExists) {
            RecipeIngredient ingredient = new RecipeIngredient();
            ingredient.setFood(food);
            ingredient.setAmountInGrams(request.getAmountInGrams());
            ingredient.setRecipe(recipe);
            recipe.getIngredients().add(ingredient);
        }

        return recipeRepository.save(recipe);
    }

    public Recipe removeIngredient(Long recipeId, int foodId, Long userId) {
        Recipe recipe = recipeRepository.findByIdAndUserId(recipeId, userId)
                .orElseThrow(() -> new RuntimeException("Recipe not found"));

        recipe.getIngredients().removeIf(i -> i.getFood().getNummer() == foodId);
        return recipeRepository.save(recipe);
    }

    public void deleteRecipe(Long recipeId, Long userId) {
        Recipe recipe = recipeRepository.findByIdAndUserId(recipeId, userId)
                .orElseThrow(() -> new RuntimeException("Recept ej hittat"));

        recipeRepository.delete(recipe);
    }

    public Recipe updateIngredientAmount(Long recipeId, int foodId, double newAmount, Long userId) {
        Recipe recipe = recipeRepository.findByIdAndUserId(recipeId, userId)
                .orElseThrow(() -> new RuntimeException("Recipe not found"));

        RecipeIngredient ingredient = recipe.getIngredients().stream()
                .filter(ri -> ri.getFood().getNummer() == foodId)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Ingredient not found"));

        ingredient.setAmountInGrams(newAmount);
        return recipeRepository.save(recipe);
    }


}
