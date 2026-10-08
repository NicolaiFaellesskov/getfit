package app.DAOs;

import app.entities.Role;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import java.util.List;

public class RoleDAO {

    private final EntityManagerFactory emf;

    public RoleDAO(EntityManagerFactory emf) {
        this.emf = emf;
    }

    public Role save(Role role) {

        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();

            em.persist(role);

            em.getTransaction().commit();

            return role;

        } catch (Exception e) {

            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }

            throw e;

        } finally {
            em.close();
        }
    }

    public Role findById(int id) {

        EntityManager em = emf.createEntityManager();

        try {
            return em.find(Role.class, id);

        } finally {
            em.close();
        }
    }

    public List<Role> findAll() {

        EntityManager em = emf.createEntityManager();

        try {
            return em.createQuery(
                    "SELECT r FROM Role r",
                    Role.class
            ).getResultList();

        } finally {
            em.close();
        }
    }

    public Role findByName(String name) {

        EntityManager em = emf.createEntityManager();

        try {
            return em.createQuery(
                            "SELECT r FROM Role r WHERE r.name = :name",
                            Role.class
                    )
                    .setParameter("name", name)
                    .getResultStream()
                    .findFirst()
                    .orElse(null);

        } finally {
            em.close();
        }
    }

    public Role update(Role role) {

        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();

            Role updatedRole = em.merge(role);

            em.getTransaction().commit();

            return updatedRole;

        } catch (Exception e) {

            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }

            throw e;

        } finally {
            em.close();
        }
    }

    public boolean delete(int id) {

        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();

            Role role = em.find(Role.class, id);

            if (role == null) {
                em.getTransaction().rollback();
                return false;
            }

            em.remove(role);

            em.getTransaction().commit();

            return true;

        } catch (Exception e) {

            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }

            throw e;

        } finally {
            em.close();
        }
    }
}