package com.ludwig.foodcheck.recipe.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UpdateIngredientRequest {
    private int foodId;
    private double newAmountInGrams;
}
