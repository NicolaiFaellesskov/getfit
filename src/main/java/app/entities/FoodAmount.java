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

    private int gram;



    @ManyToOne
    @ToString.Exclude
    private Meal meal;



    @ManyToOne
    @ToString.Exclude
    private Food food;
}