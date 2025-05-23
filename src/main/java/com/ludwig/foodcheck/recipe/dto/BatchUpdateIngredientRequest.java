package com.ludwig.foodcheck.recipe.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class BatchUpdateIngredientRequest {
    private List<UpdateIngredientRequest> updates;
}
