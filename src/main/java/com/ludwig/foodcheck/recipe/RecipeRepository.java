package com.ludwig.foodcheck.recipe;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RecipeRepository extends JpaRepository<Recipe, Long> {
    List<Recipe> findByUserId(Long userId);
    Optional<Recipe> findByIdAndUserId(Long recipeId, Long userId);
    List<Recipe> findByIsPublicTrue();
}
