package com.ludwig.foodcheck.dto.nutrition;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NaringsvardeApiResponse {
    private String namn;
    private double varde;
    private String enhet;
    private String publikationsdatum;
}

