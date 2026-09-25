package app;

import app.persistence.FoodDTO;
import app.services.foodService.FoodAPI;
import app.services.foodService.FoodSearchService;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class Main {

    public static void main(String[] args) {

        FoodSearchService foodSearchService = new FoodSearchService();

        Future<FoodDTO> future = foodSearchService.searchFood("Chicken");

        FoodDTO food = null;
        try {
            food = future.get();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }

        if (food == null) {
            System.out.println("Ingen produkter fundet.");
            return;
        }

        System.out.println("Navn: " + food.getName());
        System.out.println("Brand: " + food.getBrand());
        System.out.println("Barcode: " + food.getBarcode());
        System.out.println("Kalorier: " + food.getNutriments()
                .getOrZero(food.getNutriments().getEnergyKcal()));
        System.out.println("Protein: " + food.getNutriments().getOrZero(
                food.getNutriments().getProtein()));
        System.out.println("Fat: " + food.getNutriments().getOrZero(food.getNutriments().getFat()));
        System.out.println("SaturedFat: " + food.getNutriments().getOrZero(
                food.getNutriments().getSaturatedFat()));
        System.out.println("Photo: " + food.getImageUrl());
    }
}