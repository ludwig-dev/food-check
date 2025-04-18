package com.ludwig.foodcheck.recipe;

import com.ludwig.foodcheck.dto.nutrition.NutritionResultDTO;
import com.ludwig.foodcheck.recipe.dto.*;
import com.ludwig.foodcheck.service.NutritionService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recipes")
public class RecipeController {
    private final RecipeService recipeService;
    private final NutritionService nutritionService;

    public RecipeController(RecipeService recipeService, NutritionService nutritionService) {
        this.recipeService = recipeService;
        this.nutritionService = nutritionService;
    }

    @PostMapping
    public ResponseEntity<RecipeResponse> createRecipe(@RequestBody CreateRecipeRequest request,
                                                       @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = Long.parseLong(userDetails.getUsername());
        Recipe recipe = recipeService.createRecipe(request.getName(), request.getIngredients(), userId);
        return ResponseEntity.ok(recipeService.convertToDTO(recipe));
    }

    @GetMapping
    public ResponseEntity<List<RecipeSummaryResponse>> getMyRecipes(@AuthenticationPrincipal UserDetails userDetails) {
        Long userId = Long.parseLong(userDetails.getUsername());
        List<Recipe> recipes = recipeService.getRecipeByUser(userId);
        List<RecipeSummaryResponse> response = recipes.stream()
                .map(recipe -> new RecipeSummaryResponse(recipe.getId(), recipe.getName()))
                .toList();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RecipeResponse> getRecipe(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = Long.parseLong(userDetails.getUsername());
        return recipeService.getRecipeById(id, userId)
                .map(recipeService::convertToDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/nutrition")
    public ResponseEntity<List<NutritionResultDTO>> getNutrition(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {

        Long userId = Long.parseLong(userDetails.getUsername());
        return ResponseEntity.ok(nutritionService.calculateNutrition(id, userId));
    }

    @PutMapping("/{id}/ingredients/add")
    public ResponseEntity<RecipeResponse> addIngredient(
            @PathVariable Long id,
            @RequestBody RecipeIngredientRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        Long userId = Long.parseLong(userDetails.getUsername());
        Recipe updated = recipeService.addIngredient(id, request, userId);
        return ResponseEntity.ok(recipeService.convertToDTO(updated));
    }

    @DeleteMapping("/{id}/ingredients/{foodId}")
    public ResponseEntity<RecipeResponse> removeIngredient(
            @PathVariable Long id,
            @PathVariable int foodId,
            @AuthenticationPrincipal UserDetails userDetails) {

        Long userId = Long.parseLong(userDetails.getUsername());
        Recipe updated = recipeService.removeIngredient(id, foodId, userId);
        return ResponseEntity.ok(recipeService.convertToDTO(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRecipe(@PathVariable Long id,
                                             @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = Long.parseLong(userDetails.getUsername());
        recipeService.deleteRecipe(id, userId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/ingredients/{foodId}")
    public ResponseEntity<RecipeResponse> updateIngredientAmount(
            @PathVariable Long id,
            @PathVariable int foodId,
            @RequestBody UpdateIngredientRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        Long userId = Long.parseLong(userDetails.getUsername());
        Recipe updated = recipeService.updateIngredientAmount(id, foodId, request.getNewAmountInGrams(), userId);
        return ResponseEntity.ok(recipeService.convertToDTO(updated));
    }
}
