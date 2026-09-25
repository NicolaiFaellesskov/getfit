package app.controllers;

import app.DTOs.FoodDTO;
import app.services.foodService.FoodSearchService;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class FoodController {

    private final FoodSearchService foodSearchService;

    public FoodController() {
        this.foodSearchService = new FoodSearchService();
    }

    public void addRoutes(Javalin app) {

        app.get("/", context -> getChicken(context));
        app.get("/food", context -> getChicken(context));
    }

    private void getChicken(Context context) {

        Future<FoodDTO> future = foodSearchService.searchFood("Nutella");

        try {

            FoodDTO food = future.get();

            if (food == null) {
                context.render("food.html", java.util.Map.of("food", null));
                return;
            }

            context.render(
                    "food.html",
                    java.util.Map.of("food", food)
            );

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);

        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }
}

