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
public class Food {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;
    private String brand;
    private String barcode;
    private String type;

    private double calories;
    private double proteinGrams;
    private double carbohydratesGrams;
    private double fatGrams;

    private double sugarGrams;
    private double fiberGrams;
    private double saltGrams;
    private double saturatedFatGrams;
}