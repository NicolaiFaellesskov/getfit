package app.DAOs;

import app.config.HibernateConfig;
import app.entities.Meal;
import app.exceptions.DatabaseException;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MealDAOTest {

    private static EntityManagerFactory emf;
    private static MealDAO dao;

    @BeforeAll
    static void setUp() {
        emf = HibernateConfig.getEntityManagerFactory();
        dao = new MealDAO(emf);
    }
    @Test
    void saveShouldPersistMeal() throws DatabaseException{

        // Arrange
        Meal meal = Meal.builder()
                .mealName("Breakfast")
                .build();

        // Act
        dao.save(meal);

        // Assert
        assertNotNull(meal.getId());
        assertEquals("Breakfast", meal.getMealName());


    }

    @Test
    void findByIdShouldReturnMeal() throws DatabaseException {

        // Arrange
        Meal meal = Meal.builder()
                .mealName("Lunch")
                .build();

        // Act
        dao.save(meal);
        Meal foundMeal = dao.findById(meal.getId());

        // Assert
        assertNotNull(foundMeal);
        assertEquals("Lunch", foundMeal.getMealName());
        assertEquals(meal.getId(), foundMeal.getId());
    }


}