package app.persistence;

import app.DTOs.DailyLogDTO;
import app.entities.DailyLog;

import java.util.stream.Collectors;

public class DailyLogMapper {

    public static DailyLogDTO toDTO(DailyLog dailyLog) {
        if (dailyLog == null) {
            return null;
        }

        return DailyLogDTO.builder()
                .id(dailyLog.getId())
                .createdAt(dailyLog.getCreatedAt())
                .meals(dailyLog.getMeals() == null
                        ? null
                        : dailyLog.getMeals().stream()
                        .map(MealMapper::toDTO)
                        .collect(Collectors.toSet()))
                .build();
    }
}