package app;

import app.DAOs.DailyLogDAO;
import app.config.HibernateConfig;
import app.controllers.DailyLogController;
import app.controllers.FoodController;
import io.javalin.Javalin;
import jakarta.persistence.EntityManagerFactory;

public class Main {

    private static final EntityManagerFactory emf =
            HibernateConfig.getEntityManagerFactory();

    public static void main(String[] args) {

        Javalin app = Javalin.create();

        FoodController foodController = new FoodController();
        foodController.addRoutes(app);

        DailyLogDAO dailyLogDAO = new DailyLogDAO(emf);
        DailyLogController dailyLogController =
                new DailyLogController(dailyLogDAO);

        dailyLogController.addRoutes(app);

        app.start(7070);
    }
}
