package app;

import app.config.ApplicationConfig; import app.routes.Routes;

public class Main {

    public static void main(String[] args) {

        new ApplicationConfig()
                .security()
                .route(new Routes().getRoutes())
                .cors()
                .exceptions()
                .apiExceptions()
                .start(7070); } }