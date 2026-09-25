package app.DAOs;

import app.entities.Food;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import java.util.List;

public class FoodDAO {

    private final EntityManagerFactory emf;

    public FoodDAO(EntityManagerFactory emf) {
        this.emf = emf;
    }

    public void save(Food food) {
        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();
            em.persist(food);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    public Food findById(int id) {
        EntityManager em = emf.createEntityManager();

        try {
            return em.find(Food.class, id);
        } finally {
            em.close();
        }
    }

    public List<Food> findAll() {
        EntityManager em = emf.createEntityManager();

        try {
            return em.createQuery(
                    "SELECT m FROM Food m",
                    Food.class
            ).getResultList();
        } finally {
            em.close();
        }
    }

    public void update(Food food) {
        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();
            em.merge(food);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    public void delete(int id) {
        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();

            Food food = em.find(Food.class, id);

            if (food != null) {
                em.remove(food);
            }

            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }
}
