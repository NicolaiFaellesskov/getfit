package app.routes;

import io.javalin.apibuilder.EndpointGroup;

import static io.javalin.apibuilder.ApiBuilder.path;

public class Routes {

    private final UserRoute userRoute = new UserRoute();
    private final RoleRoute roleRoute = new RoleRoute();
    private final DailyLogRoute dailyLogRoute = new DailyLogRoute();
    private final MealRoute mealRoute = new MealRoute();
    private final FoodRoute foodRoute = new FoodRoute();

    public EndpointGroup getRoutes() {

        return () -> {

            path("/users", userRoute.getRoutes());

            path("/roles", roleRoute.getRoutes());

            path("/dailylogs", dailyLogRoute.getRoutes());

            path("/meals", mealRoute.getRoutes());

            path("/food", foodRoute.getRoutes());
        };
    }
}