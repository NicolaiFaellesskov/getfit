package app.config;

import app.entities.*;

import org.hibernate.cfg.Configuration;

final class EntityRegistry {

    private EntityRegistry() {}

    static void registerEntities(Configuration configuration) {
        configuration.addAnnotatedClass(DailyLog.class);
        configuration.addAnnotatedClass(Food.class);
        configuration.addAnnotatedClass(FoodAmount.class);
        configuration.addAnnotatedClass(Meal.class);
        configuration.addAnnotatedClass(User.class);
        configuration.addAnnotatedClass(UserDetails.class);

        // TODO: Add more entities here...
    }
}