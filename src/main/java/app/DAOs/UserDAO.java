package app.DAOs;

import app.entities.Role;
import app.entities.User;
import app.exceptions.ValidationException;
import app.security.ISecurityDAO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import java.util.List;

public class UserDAO implements ISecurityDAO {

    private final EntityManagerFactory emf;

    public UserDAO(EntityManagerFactory emf) {
        this.emf = emf;
    }

    public void save(User user) {
        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();
            em.persist(user);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    public User findById(int id) {
        EntityManager em = emf.createEntityManager();

        try {
            return em.find(User.class, id);
        } finally {
            em.close();
        }
    }

    public List<User> findAll() {
        EntityManager em = emf.createEntityManager();

        try {
            return em.createQuery(
                    "SELECT m FROM User m",
                    User.class
            ).getResultList();
        } finally {
            em.close();
        }
    }

    public void update(User user) {
        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();
            em.merge(user);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    public void delete(int id) {
        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();

            User user = em.find(User.class, id);

            if (user != null) {
                em.remove(user);
            }

            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    @Override
    public User getVerifiedUser(String username, String password) throws ValidationException {
        EntityManager em = emf.createEntityManager();

        try {
            User user = em.createQuery(
                            "SELECT u FROM User u WHERE u.username = :username",
                            User.class
                    )
                    .setParameter("username", username)
                    .getResultStream()
                    .findFirst()
                    .orElse(null);

            if (user == null || !user.verifyPassword(password)) {
                throw new ValidationException("Invalid username or password");
            }

            return user;

        } finally {
            em.close();
        }
    }

    @Override
    public User createUser(String username, String password) {
        User user = new User(username, password);
        save(user);
        return user;
    }

    @Override
    public Role createRole(String role) {
        EntityManager em = emf.createEntityManager();

        try {
            Role newRole = new Role();
            newRole.setName(role);

            em.getTransaction().begin();
            em.persist(newRole);
            em.getTransaction().commit();

            return newRole;

        } finally {
            em.close();
        }
    }

    @Override
    public User addUserRole(String username, String role) {
        EntityManager em = emf.createEntityManager();

        try {
            User user = em.createQuery(
                            "SELECT u FROM User u WHERE u.username = :username",
                            User.class
                    )
                    .setParameter("username", username)
                    .getResultStream()
                    .findFirst()
                    .orElse(null);

            if (user == null) {
                throw new RuntimeException("User not found");
            }

            Role roleEntity = em.createQuery(
                            "SELECT r FROM Role r WHERE r.name = :role",
                            Role.class
                    )
                    .setParameter("role", role)
                    .getResultStream()
                    .findFirst()
                    .orElse(null);

            if (roleEntity == null) {
                throw new RuntimeException("Role not found");
            }

            em.getTransaction().begin();
            user.addRole(roleEntity);
            em.merge(user);
            em.getTransaction().commit();

            return user;

        } finally {
            em.close();
        }
    }

}
