package app.routes;

import app.controllers.RoleController;
import io.javalin.apibuilder.EndpointGroup;

import static io.javalin.apibuilder.ApiBuilder.*;

public class RoleRoute {

    private final RoleController roleController =
            new RoleController();

    public EndpointGroup getRoutes() {

        return () -> {
            get("/", roleController::readAll);
            get("/{id}", roleController::read);
            get("/name/{name}", roleController::readByName);
            post("/", roleController::create);
            put("/{id}", roleController::update);
            delete("/{id}", roleController::delete);
        };
    }
}