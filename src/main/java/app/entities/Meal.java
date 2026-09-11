package app.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Entity
public class Meal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String mealName;

    // Relation m:1

    @ManyToOne

    private DailyLog dailyLog;

    // Relation 1:m

    @OneToMany(mappedBy = "meal", cascade = CascadeType.ALL)
    @Builder.Default
    private Set<FoodAmount> foodAmounts = new HashSet<>();


    // Bi-directional update

    public void addFoodAmount(FoodAmount foodAmount) {
        this.foodAmounts.add(foodAmount);

        if (foodAmount != null) {
            foodAmount.setMeal(this);
        }
    }
}