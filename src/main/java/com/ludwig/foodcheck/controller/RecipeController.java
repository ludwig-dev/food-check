package com.ludwig.foodcheck.controller;

import com.ludwig.foodcheck.dto.recipe.CreateRecipeRequest;
import com.ludwig.foodcheck.dto.recipe.RecipeResponse;
import com.ludwig.foodcheck.dto.recipe.RecipeSummaryResponse;
import com.ludwig.foodcheck.model.Recipe;
import com.ludwig.foodcheck.service.RecipeService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recipes")
public class RecipeController {
    private final RecipeService recipeService;

    public RecipeController(RecipeService recipeService) {
        this.recipeService = recipeService;
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
}
