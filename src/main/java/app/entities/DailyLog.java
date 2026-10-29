package app.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Entity
public class DailyLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private LocalDate createdAt;



    @ManyToOne
    @ToString.Exclude
    private User user;



    @OneToMany(mappedBy = "dailyLog", cascade = CascadeType.ALL)
    @Builder.Default

    private Set<Meal> meals = new HashSet<>();




    public void addMeal(Meal meal) {
        if (meal != null) {
            this.meals.add(meal);
            meal.setDailyLog(this);
        }
    }

}