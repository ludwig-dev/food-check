package com.ludwig.foodcheck.dto.recipe;

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