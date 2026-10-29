package app.persistence;

import app.DTOs.MealDTO;
import app.entities.Meal;

import java.util.stream.Collectors;

public class MealMapper {

    public static MealDTO toDTO(Meal meal) {
        if (meal == null) {
            return null;
        }

        return MealDTO.builder()
                .id(meal.getId())
                .mealName(meal.getMealName())
                .dailyLogId(meal.getDailyLog() != null ? meal.getDailyLog().getId() : null)
                .foodAmounts(meal.getFoodAmounts().stream().map(FoodAmountMapper::toDTO).collect(Collectors.toSet()))
                .build();
    }
}