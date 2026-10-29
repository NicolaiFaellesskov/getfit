
package app.DAOs;

import app.config.HibernateConfig;
import app.entities.FoodAmount;
import app.exceptions.DatabaseException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FoodAmountDAOTest {

    private static EntityManagerFactory emf;
    private static FoodAmountDAO dao;

    @BeforeAll
    static void setUp() {
        emf = HibernateConfig.getEntityManagerFactory();
        dao = new FoodAmountDAO(emf);
    }


    @Test
    void saveShouldPersistFoodAmount() throws DatabaseException {
        FoodAmount foodAmount = FoodAmount.builder()
                .gram(150)
                .build();

        dao.save(foodAmount);

        assertNotNull(foodAmount.getId());

        assertEquals(150, foodAmount.getGram());
    }
}