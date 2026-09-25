package app;

import app.config.HibernateConfig;
import app.config.SessionConfig;
import app.config.ThymeleafConfig;
import app.controllers.FoodController;
import io.javalin.Javalin;
import io.javalin.rendering.template.JavalinThymeleaf;
import jakarta.persistence.EntityManagerFactory;

public class Main {

    private static final EntityManagerFactory emf =
            HibernateConfig.getEntityManagerFactory();

    public static void main(String[] args) {

        Javalin app = Javalin.create(config -> {
            config.staticFiles.add("/public");

            config.jetty.modifyServletContextHandler(handler ->
                    handler.setSessionHandler(SessionConfig.sessionConfig())
            );

            config.fileRenderer(
                    new JavalinThymeleaf(
                            ThymeleafConfig.templateEngine()
                    )
            );

        }).start(7070);

        FoodController foodController = new FoodController();
        foodController.addRoutes(app);
    }
}

