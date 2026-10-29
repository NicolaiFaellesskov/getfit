package app.DAOs;

import app.controllers.DailyLogController;
import app.entities.Food;
import app.exceptions.DatabaseException;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class FoodDAO {
    private static final Logger logger = LoggerFactory.getLogger(FoodDAO.class);

    private final EntityManagerFactory emf;

    public FoodDAO(EntityManagerFactory emf) {
        this.emf = emf;
    }

    public Food save(Food food) throws DatabaseException {
        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();
            em.persist(food);
            em.getTransaction().commit();
            return food;
        } catch (EntityExistsException e) {
            logger.error("Failed to save food");
            throw new DatabaseException("Error with saving food", e.getMessage());
        } finally {
            em.close();
        }
    }


    public Food findByBarcode(String barcode) throws DatabaseException {
        if (barcode == null || barcode.isBlank()) {
            return null;
        }

        EntityManager em = emf.createEntityManager();

        try {
            Food food = em.createQuery(
                            "SELECT f FROM Food f WHERE f.barcode = :barcode",
                            Food.class
                    )
                    .setParameter("barcode", barcode.trim())
                    .getResultStream()
                    .findFirst()
                    .orElse(null);
            if (food == null) {
                logger.error("Failed to find food by barcode");
            }
            return food;
        } finally {
            em.close();
        }
    }
}
