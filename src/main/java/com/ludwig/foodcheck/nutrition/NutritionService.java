package com.ludwig.foodcheck.nutrition;

import com.ludwig.foodcheck.exception.ResourceNotFoundException;
import com.ludwig.foodcheck.recipe.Recipe;
import com.ludwig.foodcheck.recipe.RecipeIngredient;
import com.ludwig.foodcheck.recipe.RecipeRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

import static java.util.Map.entry;

@Service
public class NutritionService {

    private final RecipeRepository recipeRepository;
    private final RestTemplate restTemplate = new RestTemplate();

    public NutritionService(RecipeRepository recipeRepository) {
        this.recipeRepository = recipeRepository;
    }

    public List<NutritionResultDTO> calculateNutrition(Long recipeId, Long userId) {
        Recipe recipe = getRecipeOrThrow(recipeId, userId);
        Map<String, NutritionResultDTO> totals = new HashMap<>();

        for (RecipeIngredient ingredient : recipe.getIngredients()) {
            int foodId = ingredient.getFood().getNummer();
            double amount = ingredient.getAmountInGrams();
            List<NaringsvardeApiResponse> values = fetchNutritionData(foodId);
            updateTotals(totals, values, amount);
        }

        return new ArrayList<>(totals.values());
    }

    private Recipe getRecipeOrThrow(Long recipeId, Long userId) {
        return recipeRepository.findByIdAndUserId(recipeId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Recipe not found"));
    }

    private List<NaringsvardeApiResponse> fetchNutritionData(int foodId) {
        String url = NutritionConstants.SLV_BASE_URL + foodId + "/naringsvarden";
        NaringsvardeApiResponse[] response = restTemplate.getForObject(url, NaringsvardeApiResponse[].class);
        return response != null ? List.of(response) : Collections.emptyList();
    }

    private void updateTotals(Map<String, NutritionResultDTO> totals, List<NaringsvardeApiResponse> values, double grams) {
        for (NaringsvardeApiResponse nv : values) {
            if (!NutritionConstants.RELEVANTA_NAMN.contains(nv.getNamn())) continue;

            double scaled = (nv.getVarde() * grams) / 100.0;

            totals.merge(nv.getNamn(),
                    new NutritionResultDTO(nv.getNamn(), scaled, nv.getEnhet(), calculatePercentOfRDI(nv.getNamn(), scaled)),
                    (oldVal, newVal) -> {
                        oldVal.setTotaltVarde(oldVal.getTotaltVarde() + newVal.getTotaltVarde());
                        oldVal.setProcentAvRDI(calculatePercentOfRDI(nv.getNamn(), oldVal.getTotaltVarde()));
                        return oldVal;
                    });
        }
    }

    private Double calculatePercentOfRDI(String name, double value) {
        if (NutritionConstants.RDI.containsKey(name)) {
            return (value / NutritionConstants.RDI.get(name)) * 100.0;
        }
        return null;
    }

}
