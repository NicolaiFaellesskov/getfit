package app.DTOs;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MealDTO {

    private Integer id;
    private String mealName;
    private Integer dailyLogId;
}
