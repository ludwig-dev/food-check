package com.ludwig.foodcheck.dto.recipe;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RecipeIngredientRequest {
    private int foodId;
    private double amountInGrams;
}

