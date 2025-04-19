package com.ludwig.foodcheck.recipe;

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

    public PublicRecipeController(RecipeService recipeService) {
        this.recipeService = recipeService;
    }

    @PutMapping("/{id}/publish")
    public ResponseEntity<RecipeResponse> setRecipeToPublic(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = Long.parseLong(userDetails.getUsername());
        return ResponseEntity.ok(recipeService.setRecipeToPublic(id, userId));
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
}
