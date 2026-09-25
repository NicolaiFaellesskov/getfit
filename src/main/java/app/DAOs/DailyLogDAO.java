package app.DAOs;

import app.entities.DailyLog;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import java.util.List;

public class DailyLogDAO {

    private final EntityManagerFactory emf;

    public DailyLogDAO(EntityManagerFactory emf) {
        this.emf = emf;
    }

    public void save(DailyLog dailyLog) {
        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();
            em.persist(dailyLog);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    public DailyLog findById(int id) {
        EntityManager em = emf.createEntityManager();

        try {
            return em.find(DailyLog.class, id);
        } finally {
            em.close();
        }
    }

    public List<DailyLog> findAll() {
        EntityManager em = emf.createEntityManager();

        try {
            return em.createQuery(
                    "SELECT m FROM DailyLog m",
                    DailyLog.class
            ).getResultList();
        } finally {
            em.close();
        }
    }

    public void update(DailyLog dailyLog) {
        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();
            em.merge(dailyLog);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    public void delete(int id) {
        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();

            DailyLog dailyLog = em.find(DailyLog.class, id);

            if (dailyLog != null) {
                em.remove(dailyLog);
            }

            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }
}
