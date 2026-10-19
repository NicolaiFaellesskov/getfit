package app.controllers;

import app.DTOs.FoodDTO;
import app.services.foodService.FoodSearchService;
import io.javalin.http.Context;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class FoodController {

    private final FoodSearchService foodSearchService =
            new FoodSearchService();

    public void search(Context ctx) {
        String search = ctx.queryParam("search");

        if (search == null || search.isBlank()) {
            ctx.status(400).result("Missing search parameter");
            return;
        }

        Future<List<FoodDTO>> future =
                foodSearchService.searchFood(search);

        try {
            List<FoodDTO> foods = future.get();

            ctx.status(200);
            ctx.json(foods);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            ctx.status(500).result("Search interrupted");

        } catch (ExecutionException e) {
            ctx.status(500).result("Food search failed");
        }
    }
}
