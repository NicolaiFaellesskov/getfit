package app.DAOs;

import app.entities.Food;
import app.exceptions.DatabaseException;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityNotFoundException;

import java.util.List;

public class FoodDAO {

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
                throw new DatabaseException("error, barcode does not exist");
            }
            return food;
        } finally {
            em.close();
        }
    }
}
