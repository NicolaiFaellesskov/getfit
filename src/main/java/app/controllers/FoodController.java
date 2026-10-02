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

        app.get("/", context -> searchFood(context));
        app.get("/food", context -> searchFood(context));
    }

    private void searchFood(Context context) {
        String search = context.queryParam("search");
        if (search == null || search.isBlank()){
            context.status(400).result("Missing search parameter");
            return;
        }

        Future<FoodDTO> future = foodSearchService.searchFood(search);

        try {

            FoodDTO food = future.get();

            if (food == null) {
                context.status(404).result("No food found");
                return;
            }

            context.json(food);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            context.status(500).result("Search interrupted");

        } catch (ExecutionException e) {
            context.status(500).result("Food search failed");
        }
    }
}

