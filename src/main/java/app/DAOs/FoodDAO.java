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

    public Food save(Food food) {
        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();
            em.persist(food);
            em.getTransaction().commit();
            return food;
        } catch (RuntimeException e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
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
                    "SELECT f FROM Food f",
                    Food.class
            ).getResultList();
        } finally {
            em.close();
        }
    }

    public Food update(Food food) {
        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();
            Food updatedFood = em.merge(food);
            em.getTransaction().commit();
            return updatedFood;
        } catch (RuntimeException e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    public boolean delete(int id) {
        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();

            Food food = em.find(Food.class, id);

            if (food == null) {
                em.getTransaction().rollback();
                return false;
            }

            em.remove(food);
            em.getTransaction().commit();
            return true;
        } catch (RuntimeException e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    public Food findByBarcode(String barcode) {
        if (barcode == null || barcode.isBlank()) {
            return null;
        }

        EntityManager em = emf.createEntityManager();

        try {
            return em.createQuery(
                            "SELECT f FROM Food f WHERE f.barcode = :barcode",
                            Food.class
                    )
                    .setParameter("barcode", barcode.trim())
                    .getResultStream()
                    .findFirst()
                    .orElse(null);
        } finally {
            em.close();
        }
    }
}
