    package app.DAOs;

    import app.entities.Food;
    import app.entities.Meal;
    import app.exceptions.DatabaseException;
    import jakarta.persistence.EntityManager;
    import jakarta.persistence.EntityManagerFactory;
    import org.slf4j.Logger;
    import org.slf4j.LoggerFactory;

    import java.util.List;

    public class MealDAO {
        private static final Logger logger = LoggerFactory.getLogger(MealDAO.class);

        private final EntityManagerFactory emf;

        public MealDAO(EntityManagerFactory emf) {
            this.emf = emf;
        }

        public void save(Meal meal) throws DatabaseException{
            EntityManager em = emf.createEntityManager();

            try {
                em.getTransaction().begin();
                em.persist(meal);
                em.getTransaction().commit();
            } catch (RuntimeException e) {
                logger.error("Failed to save meal", e);
                throw new DatabaseException("Error, could not save meal");
            } finally {
                em.close();
            }
        }

        public Meal findById(int id) throws DatabaseException {
            EntityManager em = emf.createEntityManager();

            try {
                return em.createQuery(
                                "SELECT DISTINCT m FROM Meal m " +
                                        "LEFT JOIN FETCH m.foodAmounts fa " +
                                        "LEFT JOIN FETCH fa.food " +
                                        "WHERE m.id = :id",
                                Meal.class
                        )
                        .setParameter("id", id)
                        .getResultStream()
                        .findFirst()
                        .orElse(null);
            } catch(RuntimeException e) {
                throw new DatabaseException("Error, could not find meal");
            }finally {
                em.close();
            }
        }

        public List<Meal> findAll() throws DatabaseException{
            EntityManager em = emf.createEntityManager();

            try {
                return em.createQuery(
                        "SELECT DISTINCT m FROM Meal m " +
                                "LEFT JOIN FETCH m.foodAmounts fa " +
                                "LEFT JOIN FETCH fa.food",
                        Meal.class
                ).getResultList();
            } catch (RuntimeException e) {
                throw new DatabaseException("Error, could not find all meals");
            } finally {
                em.close();
            }
        }

        public void update(Meal meal) throws DatabaseException {
            EntityManager em = emf.createEntityManager();

            try {
                em.getTransaction().begin();
                em.merge(meal);
                em.getTransaction().commit();
            } catch (RuntimeException e) {
                throw new DatabaseException("Error, could not update meal");
            } finally {
                em.close();
            }
        }

        public void delete(int id) throws DatabaseException {
            EntityManager em = emf.createEntityManager();

            try {
                em.getTransaction().begin();

                Meal meal = em.find(Meal.class, id);

                if (meal != null) {
                    em.remove(meal);
                }

                em.getTransaction().commit();
            } catch (RuntimeException e) {
                throw new DatabaseException("Error, could not delete meal");
            } finally {
                em.close();
            }
        }
    }
