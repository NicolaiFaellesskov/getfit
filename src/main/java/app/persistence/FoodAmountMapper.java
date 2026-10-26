package app.persistence;

import app.DTOs.FoodAmountDTO;
import app.entities.FoodAmount;

public class FoodAmountMapper {

    public static FoodAmountDTO toDTO(FoodAmount entity) {
        if (entity == null) {
            return null;
        }

        return FoodAmountDTO.builder()
                .id(entity.getId())
                .foodId(
                        entity.getFood() != null
                                ? entity.getFood().getId()
                                : null
                )
                .foodName(
                        entity.getFood() != null
                                ? entity.getFood().getName()
                                : null
                )
                .amount(entity.getGram())
                .build();
    }
}