package app.routes;

import app.controllers.LoginController;
import io.javalin.apibuilder.EndpointGroup;

import static io.javalin.apibuilder.ApiBuilder.post;

public class AuthRoutes {

    private final LoginController loginController =
            new LoginController();

    public EndpointGroup getRoutes() {
        return () -> {
            post("/login", loginController::login);
        };
    }
}