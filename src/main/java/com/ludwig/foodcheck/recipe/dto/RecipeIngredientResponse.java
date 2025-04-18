package com.ludwig.foodcheck.recipe.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RecipeIngredientResponse {
    private int foodId;
    private String foodName;
    private double amountInGrams;
}
