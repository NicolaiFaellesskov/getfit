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

        try {
            Future<FoodDTO> future = foodSearchService.searchFood("Nutella");

            FoodDTO food = future.get();

            if (food == null) {
                context.status(404).result("Food not found");
                return;
            }

            context.json(food);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            e.printStackTrace();
            context.status(500).result("Interrupted: " + e.getMessage());

        } catch (ExecutionException e) {
            e.printStackTrace();
            context.status(500).result(
                    "Execution error: " + e.getCause()
            );

        } catch (Exception e) {
            e.printStackTrace();
            context.status(500).result(
                    "Error: " + e.getMessage()
            );
        }
    }


}

