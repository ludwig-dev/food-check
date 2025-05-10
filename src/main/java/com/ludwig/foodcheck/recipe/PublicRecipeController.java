package com.ludwig.foodcheck.recipe;

import com.ludwig.foodcheck.nutrition.NutritionResultDTO;
import com.ludwig.foodcheck.nutrition.NutritionService;
import com.ludwig.foodcheck.recipe.dto.RecipeResponse;
import com.ludwig.foodcheck.recipe.dto.RecipeSummaryResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recipes/public")
public class PublicRecipeController {

    private final RecipeService recipeService;
    private final NutritionService nutritionService;

    public PublicRecipeController(RecipeService recipeService, NutritionService nutritionService) {
        this.recipeService = recipeService;
        this.nutritionService = nutritionService;
    }

    @PutMapping("/{id}/publish")
    public ResponseEntity<RecipeResponse> setRecipeToPublic(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = Long.parseLong(userDetails.getUsername());
        return ResponseEntity.ok(recipeService.setRecipeToPublic(id, userId));
    }

    @PutMapping("/{id}/private")
    public ResponseEntity<RecipeResponse> setRecipeToPrivate(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = Long.parseLong(userDetails.getUsername());
        return ResponseEntity.ok(recipeService.setRecipeToPrivate(id, userId));
    }

    @GetMapping()
    public ResponseEntity<List<RecipeSummaryResponse>> getAllPublishedRecipes() {
        return ResponseEntity.ok(recipeService.getPublicRecipes());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RecipeResponse> getPublicRecipe(@PathVariable Long id) {
        RecipeResponse response = recipeService.getPublicRecipe(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/nutrition")
    public ResponseEntity<List<NutritionResultDTO>> getNutrition(
            @PathVariable Long id) {
        return ResponseEntity.ok(nutritionService.calculateNutritionForPublicRecipes(id));
    }
}
