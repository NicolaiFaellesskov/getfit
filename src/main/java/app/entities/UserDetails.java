package app.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Entity
public class UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private LocalDate birthDate;
    private int startWeight;
    private int weight;
    private int height;
    private String gender;


    @OneToOne
    @JoinColumn(name = "user_id", unique = true)
    @ToString.Exclude
    private User user;
}