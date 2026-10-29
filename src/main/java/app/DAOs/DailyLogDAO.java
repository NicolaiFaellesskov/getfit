package app.DAOs;

import app.entities.DailyLog;
import app.exceptions.ApiException;
import app.exceptions.DatabaseException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class DailyLogDAO {
    private static final Logger logger = LoggerFactory.getLogger(DailyLogDAO.class);

    private final EntityManagerFactory emf;

    public DailyLogDAO(EntityManagerFactory emf) {
        this.emf = emf;
    }

    public void save(DailyLog dailyLog) throws DatabaseException {
        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();
            em.persist(dailyLog);
            em.getTransaction().commit();
        } catch (RuntimeException e) {
            logger.error("Failed to save DailyLog", e);
            throw new DatabaseException("Error, can't save dailyLog", e.getMessage());
        } finally {
            em.close();
        }
    }

    public DailyLog findById(int id) throws DatabaseException {
        EntityManager em = emf.createEntityManager();

        try {
            DailyLog dailyLog = em.createQuery(
                            "SELECT DISTINCT d FROM DailyLog d " +
                                    "LEFT JOIN FETCH d.meals m " +
                                    "LEFT JOIN FETCH m.foodAmounts fa " +
                                    "LEFT JOIN FETCH fa.food " +
                                    "WHERE d.id = :id",
                            DailyLog.class
                    )
                    .setParameter("id", id)
                    .getResultStream()
                    .findFirst()
                    .orElse(null);
            if (dailyLog == null) {
                throw new DatabaseException("Error, dailyLog does not exist");
            }
            return dailyLog;
        } finally {
            em.close();
        }
    }


    public void update(DailyLog dailyLog) throws DatabaseException {
        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();
            em.merge(dailyLog);
            em.getTransaction().commit();
        } catch (PersistenceException e) {
            logger.error("Failed to update DailyLog", e);
            throw new DatabaseException("Error, cannot update", e.getMessage());
        } finally {
            em.close();
        }
    }

    public void delete(int id)throws DatabaseException {
        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();

            DailyLog dailyLog = em.find(DailyLog.class, id);
            if (dailyLog == null) {
                throw new DatabaseException("DailyLog does not exist");
            }
            if (dailyLog != null) {
                em.remove(dailyLog);
            }

            em.getTransaction().commit();

        }finally {
            em.close();
        }
    }

    public List<DailyLog> findByUserId(Integer userId)throws DatabaseException {
        EntityManager em = emf.createEntityManager();

        try {
            List<DailyLog> dailyLogs = em.createQuery(
                            "SELECT DISTINCT d FROM DailyLog d " +
                                    "LEFT JOIN FETCH d.meals m " +
                                    "LEFT JOIN FETCH m.foodAmounts fa " +
                                    "LEFT JOIN FETCH fa.food " +
                                    "WHERE d.user.id = :userId",
                            DailyLog.class
                    )
                    .setParameter("userId", userId)
                    .getResultList();
            if (dailyLogs.isEmpty()){
                throw new DatabaseException("Error, user dont have any dailyLogs");
            }
            return dailyLogs;
        } finally {
            em.close();
        }
    }


}
