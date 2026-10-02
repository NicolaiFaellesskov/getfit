package app.DTOs;

import lombok.*;
import app.DTOs.MealDTO;

import java.time.LocalDate;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DailyLogDTO {

    private Integer id;
    private LocalDate createdAt;
    private Set<MealDTO> meals;
}
