package com.ludwig.foodcheck.recipe.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CreateRecipeRequest {
    private String name;
    private List<RecipeIngredientRequest> ingredients;
}

