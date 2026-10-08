package app.routes;

import app.controllers.FoodController;
import io.javalin.apibuilder.EndpointGroup;

import static io.javalin.apibuilder.ApiBuilder.*;

public class FoodRoute {

    private final FoodController foodController =
            new FoodController();

    public EndpointGroup getRoutes() {

        return () -> {
            get("/", foodController::search);
            get("/food", foodController::search);
        };
    }
}