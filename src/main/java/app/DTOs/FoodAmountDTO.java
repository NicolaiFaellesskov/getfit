package app.DTOs;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FoodAmountDTO {

    private Integer id;
    private Integer foodId;
    private String foodName;
    private int amount;
}