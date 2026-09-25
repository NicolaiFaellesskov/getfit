package app.DAOs;

import app.entities.Food;
import app.entities.UserDetails;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import java.util.List;

public class UserDetailsDAO {

    private final EntityManagerFactory emf;

    public UserDetailsDAO(EntityManagerFactory emf) {
        this.emf = emf;
    }

    public void save(UserDetails userDetails) {
        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();
            em.persist(userDetails);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    public UserDetails findById(int id) {
        EntityManager em = emf.createEntityManager();

        try {
            return em.find(UserDetails.class, id);
        } finally {
            em.close();
        }
    }

    public List<UserDetails> findAll() {
        EntityManager em = emf.createEntityManager();

        try {
            return em.createQuery(
                    "SELECT m FROM UserDetails m",
                    UserDetails.class
            ).getResultList();
        } finally {
            em.close();
        }
    }

    public void update(UserDetails userDetails) {
        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();
            em.merge(userDetails);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    public void delete(int id) {
        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();

            UserDetails userDetails = em.find(UserDetails.class, id);

            if (userDetails != null) {
                em.remove(userDetails);
            }

            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }
}
