package app.DAOs;

import app.config.HibernateConfig;
import app.entities.Food;
import app.exceptions.ApiException;
import app.exceptions.DatabaseException;
import jakarta.persistence.*;
import lombok.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FoodDAOTest {
    EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory();
    FoodDAO foodDAO = new FoodDAO(emf);

    @Test
    void savingTheSameFoodWithTheSameNameAndBrand() {
//Arrange
        Food food1 = new Food(20, "kylling", "sailing", "303070",
                "Meat", 150.0, 40.0,
                200.0, 40.0, 20.0,
                30.0, 30.0, 400.0);
        Food food2 = new Food(21, "kylling", "sailing", "303071",
                "Meat", 180.0, 35.0,
                150.0, 25.0, 10.0,
                15.0, 5.0, 300.0);

        //Act & Assert
        assertThrows(DatabaseException.class, () -> foodDAO.save(food1));
        assertThrows(DatabaseException.class, () -> foodDAO.save(food2));

    }

    @Test
    void TryFindAFoodWithNoId() {
        // Arrange
        String barcode = "1";

        // Act & Assert
        assertThrows(DatabaseException.class,
                () -> foodDAO.findByBarcode(barcode)
        );
    }


}