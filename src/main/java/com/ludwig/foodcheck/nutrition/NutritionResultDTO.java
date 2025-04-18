package com.ludwig.foodcheck.nutrition;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NutritionResultDTO {
    private String namn;
    private double totaltVarde;
    private String enhet;
    private Double procentAvRDI; // kan vara null om ej definierat
}

