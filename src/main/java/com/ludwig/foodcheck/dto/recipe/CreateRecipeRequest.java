package com.ludwig.foodcheck.dto.recipe;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CreateRecipeRequest {
    private String name;
    private List<RecipeIngredientRequest> ingredients;
}

