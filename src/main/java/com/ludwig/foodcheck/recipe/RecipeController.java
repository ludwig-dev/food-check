package com.ludwig.foodcheck.recipe;

import com.ludwig.foodcheck.nutrition.NutritionResultDTO;
import com.ludwig.foodcheck.recipe.dto.*;
import com.ludwig.foodcheck.nutrition.NutritionService;
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
        return ResponseEntity.ok(recipeService.createRecipe(request.getName(), userId));
    }

    @GetMapping
    public ResponseEntity<List<RecipeSummaryResponse>> getMyRecipes(@AuthenticationPrincipal UserDetails userDetails) {
        Long userId = Long.parseLong(userDetails.getUsername());
        return ResponseEntity.ok(recipeService.getRecipeByUser(userId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RecipeResponse> getRecipe(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = Long.parseLong(userDetails.getUsername());
        RecipeResponse response = recipeService.getRecipeById(id, userId);
        return ResponseEntity.ok(response);
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
        RecipeResponse response = recipeService.addIngredientToRecipe(id, request, userId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}/ingredients/{foodId}")
    public ResponseEntity<RecipeResponse> removeIngredient(
            @PathVariable Long id,
            @PathVariable int foodId,
            @AuthenticationPrincipal UserDetails userDetails) {

        Long userId = Long.parseLong(userDetails.getUsername());
        return ResponseEntity.ok(recipeService.removeIngredient(id, foodId, userId));
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
        return ResponseEntity.ok(recipeService.updateIngredientAmount(id, foodId, request.getNewAmountInGrams(), userId));
    }

    @PutMapping("/{id}/ingredients")
    public ResponseEntity<RecipeResponse> updateIngredientAmounts(
            @PathVariable Long id,
            @RequestBody BatchUpdateIngredientRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        Long userId = Long.parseLong(userDetails.getUsername());
        RecipeResponse updated = recipeService.updateIngredientAmounts(
                id,
                request.getUpdates(),
                userId
        );
        return ResponseEntity.ok(updated);
    }

    @PutMapping("/{id}/rename")
    public ResponseEntity<UpdateRecipeNameDTO> updateRecipeName(
            @PathVariable Long id,
            @RequestBody UpdateRecipeNameDTO request,
            @AuthenticationPrincipal UserDetails userDetails) {

        Long userId = Long.parseLong(userDetails.getUsername());
        UpdateRecipeNameDTO updated = recipeService.updateRecipeName(id, request.getName(), userId);
        return ResponseEntity.ok(updated);
    }
}
