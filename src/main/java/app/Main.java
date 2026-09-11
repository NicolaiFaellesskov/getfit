package app;

import app.persistence.FoodDTO;
import app.services.foodService.FoodAPI;

public class Main {

    public static void main(String[] args) {

        FoodAPI foodAPI = new FoodAPI();

        FoodDTO food = foodAPI.searchFood("Chicken");

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