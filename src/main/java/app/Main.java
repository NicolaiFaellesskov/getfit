package app;

import app.config.ApplicationConfig;
import app.routes.Routes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {

    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {

        new ApplicationConfig()
                .security()
                .route(new Routes().getRoutes())
                .cors()
                .exceptions()
                .apiExceptions()
                .start(7070);

                logger.info("App has successfully started on port 7070");
    }
}
