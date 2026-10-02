package app;

import app.config.HibernateConfig;
import app.controllers.FoodController;
import io.javalin.Javalin;
import io.javalin.rendering.template.JavalinThymeleaf;
import jakarta.persistence.EntityManagerFactory;

public class Main {

    private static final EntityManagerFactory emf =
            HibernateConfig.getEntityManagerFactory();

    public static void main(String[] args) {

        Javalin app = Javalin.create(config -> {
        }).start(7070);

        FoodController foodController = new FoodController();
        foodController.addRoutes(app);
    }
}

