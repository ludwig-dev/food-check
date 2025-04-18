package com.ludwig.foodcheck.recipe.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class RecipeSummaryResponse {
    private Long id;
    private String name;
}