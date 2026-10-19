package app.DTOs;

import lombok.*;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MealDTO {

    private Integer id;
    private String mealName;
    private Integer dailyLogId;

    @Builder.Default
    private Set<FoodAmountDTO> foodAmounts = new java.util.HashSet<>();
}