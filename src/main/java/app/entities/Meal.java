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



    @ManyToOne
    @ToString.Exclude
    private DailyLog dailyLog;




    @OneToMany(mappedBy = "meal", cascade = CascadeType.ALL)
    @Builder.Default
    private Set<FoodAmount> foodAmounts = new HashSet<>();




    public void addFoodAmount(FoodAmount foodAmount) {
        if (foodAmount != null) {
            this.foodAmounts.add(foodAmount);
            foodAmount.setMeal(this);
        }
    }
}