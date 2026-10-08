package app.routes;

import app.controllers.MealController;
import io.javalin.apibuilder.EndpointGroup;

import static io.javalin.apibuilder.ApiBuilder.*;

public class MealRoute {

    private final MealController mealController =
            new MealController();

    public EndpointGroup getRoutes() {

        return () -> {
            get("/", mealController::readAll);
            get("/{id}", mealController::read);
            post("/", mealController::create);
            put("/{id}", mealController::update);
            delete("/{id}", mealController::delete);
        };
    }
}