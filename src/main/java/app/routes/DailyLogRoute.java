package app.routes;

import app.controllers.DailyLogController;
import io.javalin.apibuilder.EndpointGroup;

import static io.javalin.apibuilder.ApiBuilder.*;

public class DailyLogRoute {

    private final DailyLogController dailyLogController =
            new DailyLogController();

    public EndpointGroup getRoutes() {

        return () -> {
            get("/", dailyLogController::readAll);
            get("/{id}", dailyLogController::read);
            post("/", dailyLogController::create);
            put("/{id}", dailyLogController::update);
            delete("/{id}", dailyLogController::delete);
        };
    }
}