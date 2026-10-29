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
}

