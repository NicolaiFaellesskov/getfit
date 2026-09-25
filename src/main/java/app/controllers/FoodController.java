package app.controllers;

import app.DAOs.FoodDAO;
import app.entities.Food;
import io.javalin.Javalin;
import io.javalin.http.Context;
import jakarta.persistence.EntityManagerFactory;

import java.util.List;

public class FoodController {

    public FoodDAO foodDAO;

    public FoodController(EntityManagerFactory emf) {
        this.foodDAO = new FoodDAO(emf);
    }

    public void addRoutes(Javalin app) {
        app.get("/", context -> getAllFood(context));
        app.get("/food", context -> getAllFood(context));
    }

    private void getAllFood(Context context) {
        List<Food> foods = foodDAO.findAll();

        context.render("food.html", java.util.Map.of("food_list", foods));
    }
}

