package com.ludwig.foodcheck.recipe.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RecipeIngredientRequest {
    private int foodId;
    private double amountInGrams;
}

