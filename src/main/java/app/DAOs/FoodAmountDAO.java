package app.DAOs;

import app.entities.Food;
import app.entities.FoodAmount;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import java.util.List;

public class FoodAmountDAO {

    private final EntityManagerFactory emf;

    public FoodAmountDAO(EntityManagerFactory emf) {
        this.emf = emf;
    }

    public void save(FoodAmount foodAmount) {
        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();
            em.persist(foodAmount);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    public FoodAmount findById(int id) {
        EntityManager em = emf.createEntityManager();

        try {
            return em.find(FoodAmount.class, id);
        } finally {
            em.close();
        }
    }

    public List<FoodAmount> findAll() {
        EntityManager em = emf.createEntityManager();

        try {
            return em.createQuery(
                    "SELECT m FROM FoodAmount m",
                    FoodAmount.class
            ).getResultList();
        } finally {
            em.close();
        }
    }

    public void update(FoodAmount foodAmount) {
        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();
            em.merge(foodAmount);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    public void delete(int id) {
        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();

            FoodAmount foodAmount = em.find(FoodAmount.class, id);

            if (foodAmount != null) {
                em.remove(foodAmount);
            }

            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }
}
