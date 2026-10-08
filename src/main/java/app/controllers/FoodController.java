package app.controllers;

import app.DTOs.FoodDTO;
import app.services.foodService.FoodSearchService;
import io.javalin.http.Context;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class FoodController {

    private final FoodSearchService foodSearchService;

    public FoodController() {
        this.foodSearchService = new FoodSearchService();
    }

    public void search(Context ctx) {

        String search = ctx.queryParam("search");

        if (search == null || search.isBlank()) {
            ctx.status(400);
            ctx.result("Missing search parameter");
            return;
        }

        Future<FoodDTO> future =
                foodSearchService.searchFood(search);

        try {

            FoodDTO food = future.get();

            if (food == null) {
                ctx.status(404);
                ctx.result("No food found");
                return;
            }

            ctx.status(200);
            ctx.json(food);

        } catch (InterruptedException e) {

            Thread.currentThread().interrupt();

            ctx.status(500);
            ctx.result("Search interrupted");

        } catch (ExecutionException e) {

            ctx.status(500);
            ctx.result("Food search failed");
        }
    }
}