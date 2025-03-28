package com.ludwig.foodcheck.service;

import com.ludwig.foodcheck.dto.nutrition.NaringsvardeApiResponse;
import com.ludwig.foodcheck.dto.nutrition.NutritionResultDTO;
import com.ludwig.foodcheck.model.Recipe;
import com.ludwig.foodcheck.model.RecipeIngredient;
import com.ludwig.foodcheck.repository.RecipeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

import static java.util.Map.entry;

@Service
public class NutritionService {

    private final RecipeRepository recipeRepository;
    private final RestTemplate restTemplate = new RestTemplate();

    private static final String SLV_BASE_URL = "https://dataportal.livsmedelsverket.se/livsmedel/api/v1/livsmedel/";

    public NutritionService(RecipeRepository recipeRepository) {
        this.recipeRepository = recipeRepository;
    }

    private static final Map<String, Double> RDI = Map.ofEntries(
            entry("Energi (kcal)", 2500.0),
            entry("Protein", 50.0),
            entry("Kolhydrater, tillgängliga", 310.0),
            entry("Fibrer", 30.0),
            entry("Fett, totalt", 70.0),
            entry("Salt, NaCl", 6.0),

            entry("Vitamin A", 800.0),
            entry("Folat, totalt", 300.0),
            entry("Niacinekvivalenter", 16.0),
            entry("Vitamin B1", 1.1),
            entry("Tiamin", 1.1),
            entry("Vitamin B2", 1.4),
            entry("Riboflavin", 1.4),
            entry("Vitamin B3", 16.0),
            entry("Niacin", 16.0),
            entry("Vitamin B12", 2.0),
            entry("Vitamin C", 80.0),
            entry("Vitamin D", 10.0),
            entry("Vitamin E", 11.0),
            entry("Vitamin K", 70.0),

            entry("Fosfor", 700.0),
            entry("Fosfor, P", 700.0),
            entry("Jod", 150.0),
            entry("Jod, I", 150.0),
            entry("Järn", 9.0),
            entry("Järn, Fe", 9.0),
            entry("Kalcium", 800.0),
            entry("Kalcium, Ca", 800.0),
            entry("Kalium", 3500.0),
            entry("Kalium, K", 3500.0),
            entry("Magnesium", 375.0),
            entry("Magnesium, Mg", 375.0),
            entry("Natrium", 2400.0),
            entry("Natrium, Na", 2400.0),
            entry("Selen", 60.0),
            entry("Selen, Se", 60.0),
            entry("Zink", 10.0),
            entry("Zink, Zn", 10.0),

            entry("DHA (C22:6)", 250.0),
            entry("DPA (C22:5)", 250.0),
            entry("EPA (C20:5)", 250.0),

            entry("Linolsyra C18:2", 10000.0),
            entry("Kolesterol", 300.0), // ej officiellt, men ibland används
            entry("Vatten", 2000.0)
    );

    private static final Set<String> RELEVANTA_NAMN = Set.of(
            "Betakaroten/β-Karoten", "Folat, totalt", "Niacinekvivalenter", "Retinol", "Vitamin A",
            "Vitamin B1", "Tiamin", "Vitamin B2", "Riboflavin", "Vitamin B3", "Niacin", "Vitamin B12",
            "Vitamin C", "Vitamin D", "Vitamin E", "Vitamin K",

            "Fosfor", "Fosfor, P", "Jod", "Jod, I", "Järn", "Järn, Fe", "Kalcium", "Kalcium, Ca",
            "Kalium", "Kalium, K", "Magnesium", "Magnesium, Mg", "Natrium", "Natrium, Na",
            "Selen", "Selen, Se", "Zink", "Zink, Zn",

            "DHA (C22:6)", "DPA (C22:5)", "EPA (C20:5)", "Linolensyra C18:3", "Linolsyra C18:2",

            "Kolesterol", "Monosackarider", "Disackarider", "Sackaros",

            "Alkohol", "Aska", "Energi (kcal)", "Protein", "Kolhydrater, tillgängliga",
            "Fett, totalt", "Fibrer", "Fullkorn totalt", "Salt, NaCl", "Vatten"
    );

    public List<NutritionResultDTO> calculateNutrition(Long recipeId, Long userId) {
        Recipe recipe = recipeRepository.findByIdAndUserId(recipeId, userId)
                .orElseThrow(() -> new RuntimeException("Recipe not found"));

        Map<String, NutritionResultDTO> totals = new HashMap<>();

        for (RecipeIngredient ingredient : recipe.getIngredients()) {
            int foodId = ingredient.getFood().getNummer();
            double amount = ingredient.getAmountInGrams();

            String url = SLV_BASE_URL + foodId + "/naringsvarden";
            NaringsvardeApiResponse[] response = restTemplate.getForObject(url, NaringsvardeApiResponse[].class);

            if (response == null) continue;

            for (NaringsvardeApiResponse nv : response) {
                if (!RELEVANTA_NAMN.contains(nv.getNamn())) continue;

                double scaled = (nv.getVarde() * amount) / 100.0;

                totals.merge(nv.getNamn(), new NutritionResultDTO(
                                nv.getNamn(),
                                scaled,
                                nv.getEnhet(),
                                calculatePercentOfRDI(nv.getNamn(), scaled)
                        ),
                        (oldVal, newVal) -> {
                            oldVal.setTotaltVarde(oldVal.getTotaltVarde() + newVal.getTotaltVarde());
                            oldVal.setProcentAvRDI(calculatePercentOfRDI(nv.getNamn(), oldVal.getTotaltVarde()));
                            return oldVal;
                        });
            }
        }

        return new ArrayList<>(totals.values());
    }

    private Double calculatePercentOfRDI(String namn, double varde) {
        if (RDI.containsKey(namn)) {
            return (varde / RDI.get(namn)) * 100.0;
        }
        return null;
    }

}
