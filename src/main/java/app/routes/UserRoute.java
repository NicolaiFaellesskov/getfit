package app.routes;

import app.controllers.UserController;
import io.javalin.apibuilder.EndpointGroup;

import static io.javalin.apibuilder.ApiBuilder.*;

public class UserRoute {

    private final UserController userController = new UserController();

    public EndpointGroup getRoutes() {

        return () -> {
            get("/", userController::readAll);
            get("/{id}", userController::read);
            post("/", userController::create);
            put("/{id}", userController::update);
            delete("/{id}", userController::delete);
        };
    }
}
