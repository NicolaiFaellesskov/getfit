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
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String username;
    private String password;

    // Relation 1:1

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    @ToString.Exclude
    private UserDetails userDetails;

    // Relation 1:m

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @ToString.Exclude
    @Builder.Default
    private Set<DailyLog> dailyLogs = new HashSet<>();


    // Bi-directional update

    public void addUserDetails(UserDetails userDetails) {
        this.userDetails = userDetails;

        if (userDetails != null) {
            userDetails.setUser(this);
        }
    }

    public void addDailyLog(DailyLog dailyLog) {
        this.dailyLogs.add(dailyLog);

        if (dailyLog != null) {
            dailyLog.setUser(this);
        }
    }
}