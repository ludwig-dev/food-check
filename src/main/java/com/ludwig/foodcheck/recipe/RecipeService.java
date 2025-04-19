package com.ludwig.foodcheck.recipe;

import com.ludwig.foodcheck.exception.ResourceNotFoundException;
import com.ludwig.foodcheck.recipe.dto.RecipeIngredientRequest;
import com.ludwig.foodcheck.recipe.dto.RecipeIngredientResponse;
import com.ludwig.foodcheck.recipe.dto.RecipeResponse;
import com.ludwig.foodcheck.food.Food;
import com.ludwig.foodcheck.recipe.dto.RecipeSummaryResponse;
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

    public RecipeResponse createRecipe(String recipeName, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User with ID: " + userId + "not found"));

        Recipe recipe = new Recipe();
        recipe.setName(recipeName);
        recipe.setUser(user);

        List<RecipeIngredient> recipeIngredients = new ArrayList<>();
        recipe.setIngredients(recipeIngredients);
        return convertToDTO(recipeRepository.save(recipe));
    }

    public List<RecipeSummaryResponse> getRecipeByUser(Long userId) {
        return recipeRepository.findByUserId(userId).stream()
                .map(recipe -> new RecipeSummaryResponse(recipe.getId(), recipe.getName())).toList();
    }

    public RecipeResponse getRecipeById(Long recipeId, Long userId) {
        Recipe recipe = findRecipeById(recipeId, userId);
        return convertToDTO(recipe);
    }

    public RecipeResponse addIngredientToRecipe(Long recipeId, RecipeIngredientRequest request, Long userId) {
        Recipe recipe = findRecipeById(recipeId, userId);

        Food food = foodRepository.findById(request.getFoodId())
                .orElseThrow(() -> new ResourceNotFoundException("Food with ID: " + request.getFoodId() + " not found"));

        RecipeIngredient ingredient = recipe.getIngredients().stream()
                .filter(ing -> ing.getFood().getNummer() == request.getFoodId())
                .findFirst()
                .orElse(null);

        if (ingredient != null) {
            ingredient.setAmountInGrams(request.getAmountInGrams());
        } else {
            ingredient = new RecipeIngredient();
            ingredient.setFood(food);
            ingredient.setAmountInGrams(request.getAmountInGrams());
            ingredient.setRecipe(recipe);
            recipe.getIngredients().add(ingredient);
        }

        return convertToDTO(recipeRepository.save(recipe));
    }

    public RecipeResponse removeIngredient(Long recipeId, int foodId, Long userId) {
        Recipe recipe = findRecipeById(recipeId, userId);

        recipe.getIngredients().removeIf(i -> i.getFood().getNummer() == foodId);
        return convertToDTO(recipeRepository.save(recipe));
    }

    public void deleteRecipe(Long recipeId, Long userId) {
        Recipe recipe = findRecipeById(recipeId, userId);
        recipeRepository.delete(recipe);
    }

    public RecipeResponse updateIngredientAmount(Long recipeId, int foodId, double newAmount, Long userId) {
        Recipe recipe = findRecipeById(recipeId, userId);

        RecipeIngredient ingredient = recipe.getIngredients().stream()
                .filter(ri -> ri.getFood().getNummer() == foodId)
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Food with ID: " + foodId + " not found"));

        ingredient.setAmountInGrams(newAmount);
        return convertToDTO(recipeRepository.save(recipe));
    }

    public RecipeResponse setRecipeToPublic(Long recipeId, Long userId) {
        Recipe recipe = findRecipeById(recipeId, userId);
        recipe.setPublic(true);
        return convertToDTO(recipeRepository.save(recipe));
    }

    private RecipeResponse convertToDTO(Recipe recipe) {
        List<RecipeIngredientResponse> ingredients = recipe.getIngredients().stream()
                .map(ri -> new RecipeIngredientResponse(
                        ri.getFood().getNummer(),
                        ri.getFood().getNamn(),
                        ri.getAmountInGrams()
                ))
                .toList();

        return new RecipeResponse(recipe.getId(), recipe.getName(), ingredients, recipe.isPublic());
    }

    private Recipe findRecipeById(Long recipeId, Long userId) {
        return recipeRepository.findByIdAndUserId(recipeId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Recipe with ID: " + recipeId + " not found"));
    }
}
