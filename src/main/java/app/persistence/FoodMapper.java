package app.mappers;

import app.DTOs.FoodDTO;
import app.entities.Food;

public class FoodMapper {

    public static Food toEntity(FoodDTO dto) {
        if (dto == null) {
            return null;
        }

        FoodDTO.Nutriments n = dto.getNutriments();

        return Food.builder()
                .name(dto.getName())
                .brand(dto.getBrand())
                .barcode(dto.getBarcode())
                .type("FOOD")
                .calories(n.getOrZero(n.getEnergyKcal()))
                .proteinGrams(n.getOrZero(n.getProtein()))
                .carbohydratesGrams(n.getOrZero(n.getCarbohydrates()))
                .fatGrams(n.getOrZero(n.getFat()))
                .sugarGrams(n.getOrZero(n.getSugars()))
                .fiberGrams(n.getOrZero(n.getFiber()))
                .saltGrams(n.getOrZero(n.getSalt()))
                .saturatedFatGrams(n.getOrZero(n.getSaturatedFat()))
                .build();
    }
}