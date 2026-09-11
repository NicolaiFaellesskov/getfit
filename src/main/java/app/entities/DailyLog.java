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

    // Relation m:1

    @ManyToOne
    @ToString.Exclude
    private User user;

    // Relation 1:m

    @OneToMany(mappedBy = "dailyLog", cascade = CascadeType.ALL)
    @Builder.Default

    private Set<Meal> meals = new HashSet<>();


    // Bi-directional update

    public void addMeal(Meal meal) {
        this.meals.add(meal);

        if (meal != null) {
            meal.setDailyLog(this);
        }
    }

    @Override
    public String toString() {
        return "DailyLog{" +
                "meals=" + meals +
                ", user=" + user.getId() +
                ", date=" + createdAt +
                '}';
    }
}