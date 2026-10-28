package app.DAOs;

import app.config.HibernateConfig;
import app.entities.DailyLog;
import app.entities.User;
import app.entities.UserDetails;
import app.exceptions.DatabaseException;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class DailyLogDAOTest {
    EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory();
    DailyLogDAO dailyLogDAO = new DailyLogDAO(emf);
    @Test
    void saveThrowsExceptionWhenUserDoesNotExist() {
        // Arrange
        DailyLog dailyLog = new DailyLog();

        User wrongUser = new User();
        wrongUser.setId(999999);

        dailyLog.setUser(wrongUser);

        // Act & Assert
        assertThrows(DatabaseException.class, () -> {
            dailyLogDAO.save(dailyLog);
        });
    }

    @Test
    void findByIdThrowsExceptionWhenDailyLogDoesNotExist() {
        // Arrange
        int id = 99999;

        // Act & Assert
        assertThrows(DatabaseException.class,
                () -> dailyLogDAO.findById(id)
        );
    }

    @Test
    void updateThrowsExceptionWhenDailyLogDoesNotExist() {
        // Arrange
        DailyLog dailyLog = new DailyLog();
        dailyLog.setId(9999);

        // Act & Assert
        assertThrows(DatabaseException.class,
                () -> dailyLogDAO.update(dailyLog));
    }
    @Test
    void deleteThrowsExceptionWhenDailyLogDoesNotExist(){
        // Arrange
        int id = 99999;

        // Act & Assert
        assertThrows(DatabaseException.class,
                () -> dailyLogDAO.delete(id));
    }

    @Test
    void findUserByIdThrowsExceptionWhenDailyLogDoesNotExist(){
        // Arrange
        User wrongUser = new User();
        wrongUser.setId(99999);


        // Act & Assert
        assertThrows(DatabaseException.class,
                () -> dailyLogDAO.findByUserId(wrongUser.getId()));
    }

}