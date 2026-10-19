package app.DTOs;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddFoodRequest {
    private FoodDTO food;
    private int amount;
}