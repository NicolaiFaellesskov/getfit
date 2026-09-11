package app.entities;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Entity
public class FoodAmount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private int amount;

    // Relation m:1

    @ManyToOne
    private Meal meal;

    // Relation m:1

    @ManyToOne
    private Food food;
}