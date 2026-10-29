package app.DAOs;

import app.entities.FoodAmount;
import app.exceptions.DatabaseException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class FoodAmountDAO {
    private static final Logger logger = LoggerFactory.getLogger(FoodAmount.class);

    private final EntityManagerFactory emf;

    public FoodAmountDAO(EntityManagerFactory emf) {
        this.emf = emf;
    }

    public void save(FoodAmount foodAmount) throws DatabaseException {
        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();
            em.persist(foodAmount);
            em.getTransaction().commit();
        } catch (RuntimeException e) {
            logger.error("Failed to save food", e);
            throw new DatabaseException("Error, could not save food", e.getMessage());
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
                    "SELECT fa FROM FoodAmount fa",
                    FoodAmount.class
            ).getResultList();
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

