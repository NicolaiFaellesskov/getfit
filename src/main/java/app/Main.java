package app;

import app.DTOs.FoodDTO;
import app.config.HibernateConfig;
import app.config.SessionConfig;
import app.config.ThymeleafConfig;
import app.controllers.FoodController;
import app.services.foodService.FoodSearchService;
import io.javalin.Javalin;
import io.javalin.rendering.template.JavalinThymeleaf;
import jakarta.persistence.EntityManagerFactory;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class Main {
    private static final EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory();

    public static void main(String[] args) {

        Javalin app = Javalin.create(config -> {
            config.staticFiles.add("/public");
            config.jetty.modifyServletContextHandler(handler -> handler.setSessionHandler(SessionConfig.sessionConfig()));
            config.fileRenderer(new JavalinThymeleaf(ThymeleafConfig.templateEngine()));
        }).start(7070);
        FoodController foodController = new FoodController(emf);
        foodController.addRoutes(app);

        FoodSearchService foodSearchService = new FoodSearchService();

        Future<FoodDTO> future = foodSearchService.searchFood("Kyllingbryst");

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