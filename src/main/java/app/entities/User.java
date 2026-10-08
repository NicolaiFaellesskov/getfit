package app.entities;

import app.security.ISecurityUser;
import jakarta.persistence.*;
import lombok.*;
import org.mindrot.jbcrypt.BCrypt;

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
public class User implements ISecurityUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String username;
    private String password;

    @Override
    public boolean verifyPassword(String pw) {
        return BCrypt.checkpw(pw, this.password);
    }

    // Security constructor
    public User(String username, String password) {
        this.username = username;
        this.password = BCrypt.hashpw(password, BCrypt.gensalt());
    }

    // Relation 1:1

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    @ToString.Exclude
    private UserDetails userDetails;

    // Relation 1:m

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @ToString.Exclude
    @Builder.Default
    private Set<DailyLog> dailyLogs = new HashSet<>();

    // Relation m:m

    @ManyToMany
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    @ToString.Exclude
    @Builder.Default
    private Set<Role> roles = new HashSet<>();

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

    @Override
    public Set<String> getRolesAsStrings() {
        Set<String> roleNames = new HashSet<>();

        for (Role role : roles) {
            roleNames.add(role.getName());
        }

        return roleNames;
    }

    @Override
    public void addRole(Role role) {
        roles.add(role);
    }

    @Override
    public void removeRole(String role) {
        roles.removeIf(r -> r.getName().equals(role));
    }
}
