package app.DAOs;

import app.entities.Meal;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import java.util.List;

public class MealDAO {

    private final EntityManagerFactory emf;

    public MealDAO(EntityManagerFactory emf) {
        this.emf = emf;
    }

    public void save(Meal meal) {
        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();
            em.persist(meal);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    public Meal findById(int id) {
        EntityManager em = emf.createEntityManager();

        try {
            return em.find(Meal.class, id);
        } finally {
            em.close();
        }
    }

    public List<Meal> findAll() {
        EntityManager em = emf.createEntityManager();

        try {
            return em.createQuery(
                    "SELECT m FROM Meal m",
                    Meal.class
            ).getResultList();
        } finally {
            em.close();
        }
    }

    public void update(Meal meal) {
        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();
            em.merge(meal);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    public void delete(int id) {
        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();

            Meal meal = em.find(Meal.class, id);

            if (meal != null) {
                em.remove(meal);
            }

            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }
}
